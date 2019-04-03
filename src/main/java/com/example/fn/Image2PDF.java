package com.example.fn;

import com.example.fn.event.OCICloudEvent;

import com.google.common.base.Supplier;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Image2PDF {

    private ObjectStorage objStoreClient = null;
    private static String VALID_FILE_TYPES;

    public Image2PDF() {

        try {
            String privateKey = System.getenv().get("OCI_PRIVATE_KEY_FILE_NAME");
            System.out.println("OCI private key file " + privateKey);
            Supplier<InputStream> privateKeySupplier = () -> {
                InputStream is = null;
                String ociPrivateKeyPath = "/function/" + privateKey;
                System.err.println("Private key location --- " + ociPrivateKeyPath);

                try {
                    is = new FileInputStream(ociPrivateKeyPath);
                } catch (FileNotFoundException ex) {
                    System.err.println("Problem accessing OCI private key at " + ociPrivateKeyPath + " - " + ex.getMessage());
                }

                return is;

            };

            AuthenticationDetailsProvider authProvider
                    = SimpleAuthenticationDetailsProvider.builder()
                            .tenantId(System.getenv().get("TENANCY"))
                            .userId(System.getenv().get("USER"))
                            .fingerprint(System.getenv().get("FINGERPRINT"))
                            .passPhrase(System.getenv().get("PASSPHRASE"))
                            .privateKeySupplier(privateKeySupplier)
                            .build();

            objStoreClient = new ObjectStorageClient(authProvider);
            objStoreClient.setRegion(System.getenv().get("REGION"));
            System.out.println("Object Store client instantiated");

            VALID_FILE_TYPES = System.getenv().getOrDefault("VALID_FILE_TYPES", "jpeg,jpg,png");

        } catch (Throwable ex) {
            System.err.println("Error occurred in Image2PDF constructor - " + ex.getMessage());
        }

    }

    public String image2pdf(OCICloudEvent event) {
        String result = null;

        String bucketName = event.getData().getBucketName();
        String imageFileName = event.getData().getDisplayName();
        String objectStoreNamespace = event.getData().getNamespace();

        System.out.println("Bucket name -- " + bucketName);
        System.out.println("Image file name -- " + imageFileName);
        System.out.println("Object Storage Namespace -- " + objectStoreNamespace);
        String fileType = imageFileName.split("\\.")[1];
        System.out.println("File type - " + fileType);

        if (!Arrays.asList(VALID_FILE_TYPES.split(",")).contains(fileType)) {
            result = "Supported image types are " + VALID_FILE_TYPES + ". Please try again";
            System.out.println(result);
            return result;
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .namespaceName(objectStoreNamespace)
                    .bucketName(bucketName)
                    .objectName(imageFileName)
                    .build();
            GetObjectResponse resp = objStoreClient.getObject(getObjectRequest);

            System.out.println("Loaded image from bucket...");
            BufferedImage image = ImageIO.read(resp.getInputStream());
            convertImg2PDF(image);

            String outputBucket = System.getenv("OUTPUT_BUCKET");

            System.out.println("Saving PDF to output bucket " + outputBucket);

            String fileName = imageFileName.split("\\.")[0];

            objStoreClient.putObject(PutObjectRequest.builder()
                    .bucketName(outputBucket)
                    .objectName(fileName + ".pdf")
                    .contentType("application/pdf")
                    .namespaceName(objectStoreNamespace)
                    .putObjectBody(new FileInputStream("/tmp/output.pdf"))
                    .build());

            result = "Successfully saved PDF " + fileName + ".pdf" + " to bucket " + outputBucket;

            System.out.println(result);
        } catch (Exception ex) {
            System.err.println("Error - " + ex.getMessage());
            result = "Error - " + ex.getMessage();
        }

        return result;
    }

    /*
    Thanks to https://gist.github.com/tckb/7372725
    */
    private static void convertImg2PDF(BufferedImage image) {
        System.out.println("Converting image to PDF.....");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            Dimension pdfPageDim = new Dimension((int) PDRectangle.A4.getWidth(), (int) PDRectangle.A4.getHeight());
            Dimension imageDim = new Dimension(image.getWidth(), image.getHeight());
            Dimension newDim = getScaledDimension(imageDim, pdfPageDim);

            int w = (int) newDim.getWidth();
            int h = (int) newDim.getHeight();

            BufferedImage newimg = new BufferedImage(w, h, image.getType());
            Graphics2D g = newimg.createGraphics();
            g.setComposite(AlphaComposite.Src);

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(image, 0, 0, w, h, null);
            g.dispose();

            PDImageXObject pdImage = LosslessFactory.createFromImage(doc, newimg);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.drawImage(pdImage, PDRectangle.A4.getLowerLeftX(), PDRectangle.A4.getLowerLeftY());

            }
            doc.save("/tmp/output.pdf");
            System.out.println("Image converted to PDF");

        } catch (IOException ex) {
            Logger.getLogger(Image2PDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        if (original_width > bound_width) {
            new_width = bound_width;
            new_height = (new_width * original_height) / original_width;
        }

        if (new_height > bound_height) {
            new_height = bound_height;
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    private static void _convertImg2PDF(BufferedImage image) {
        System.out.println("Converting image to PDF");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            PDImageXObject pdImage = LosslessFactory.createFromImage(doc, image);

            // draw the image at full size at (x=20, y=20)
            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                // draw the image at full size at (x=20, y=20)
                contents.drawImage(pdImage, 20, 20);

                // to draw the image at half size at (x=20, y=20) use
                // contents.drawImage(pdImage, 20, 20, pdImage.getWidth() / 2, pdImage.getHeight() / 2); 
            }
            doc.save("/tmp/output.pdf");
            System.out.println("Image converted to PDF");

        } catch (IOException ex) {
            Logger.getLogger(Image2PDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
