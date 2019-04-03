# Event driven function for converting images to PDF

This event driven function converts images files (jpeg, png etc.) to PDF. Once you drop the image into an Oracle Cloud Infrastructure Object Storage Bucket and configure the appropriate trigger rule, the function will convert it into PDF and store the converted file (with a `.pdf` extension) in an output bucket specified by the user 

- The function written in Java and uses [Apache PDFBox](https://pdfbox.apache.org/) for file conversion.
- Uses the [OCI Java SDK](https://github.com/oracle/oci-java-sdk) to execute Object Storage read and write operations
- A custom `Dockerfile` is used to build the function

## Pre-requisites

- [Create input and output buckets in Oracle Cloud Infrastructure Object Storage](https://docs.cloud.oracle.com/iaas/Content/Object/Tasks/managingbuckets.htm#usingconsole)
- Collect the following information for you OCI tenancy (you'll need these in subsequent steps) - Tenancy OCID, User OCID of a user in the tenancy, OCI private key, OCI public key passphrase, OCI region
- Clone this repository
- Change into the correct directory - `cd fn-img2pdf` 
- Copy your OCI private key to folder. If you don't already have one, [please follow the documentation](https://docs.cloud.oracle.com/iaas/Content/API/Concepts/apisigningkey.htm#How)

### Switch to correct context

- `fn use context <your context name>`
- Check using `fn ls apps`

## Create application

`fn create app img2pdf --annotation oracle.com/oci/subnetIds='SUBNET_OCIDs' --config TENANT=<TENANT_OCID> --config USER=<USER_OCID> --config FINGERPRINT=<PUBLIC_KEY_FINGERPRINT> --config PASSPHRASE=<PRIVATE_KEY_PASSPHRASE> --config REGION=<OCI_REGION> --config OUTPUT_BUCKET=<STORAGE_OUTPUT_BUCKET_NAME>`

> The OCI user credentials you provide should have read and write access to the specified Object Storage bucket)

The function logic uses the following image file types/extensions by default - `jpeg`,`jpg`, `png`. If you want override it (e.g. you only want to support `gif` and `jpg` types), please add `VALID_FILE_TYPES` configuration to the `fn create` e.g. `--config VALID_FILE_TYPES=jpg,gif`.

e.g.

`fn create app img2pdf --annotation oracle.com/oci/subnetIds='["ocid1.subnet.oc1.phx.aaaaaaaaghmsma7mpqhqdhbgnby25u2zo4wqlrrcskvu7jg56dryxtfoobar"]' --config TENANT=ocid1.tenancy.oc1..aaaaaaaaydrjm77otncda2xn7qtv7l3hqnd3zxn2u6siwdhniibwfvfoobar --config USER=ocid1.user.oc1..aaaaaaaavz5efq7jwjjipbvm536plgylg7rfr53obvtghpi2vbg3qyfoobar --config FINGERPRINT=41:82:5f:44:ca:a1:2e:58:d2:63:6a:af:52:42:42:42 --config PASSPHRASE=4242 --config REGION=us-phoenix-1 --config OUTPUT_BUCKET=pdf-output-bucket`

### Check deployed application

`fn inspect app img2pdf`

## Deploy the application

`fn -v deploy --app img2pdf --build-arg PRIVATE_KEY_NAME=<private_key_name>` 

e.g. 

`fn -v deploy --app img2pdf --build-arg PRIVATE_KEY_NAME=oci_private_key.pem`

## Create Events rule

### Before you proceed...

Find the function OCID (use the command below) and replace it in `actions.json` file

`fn inspect fn img2pdf convertimg2pdf | jq '.id' | sed -e 's/^"//' -e 's/"$//'`

    {
        "actions": [
            {
            "actionType": "FAAS",
            "description": "Invoke function on object store event",
            "isEnabled": true,
            "functionId": "enter function OCID here"
            }
        ]
    }

Go ahead and create the rule using OCI CLI

`oci --profile <oci-config-profile-name> cloud-events rule create --display-name <rule-name> --is-enabled true --condition '{"eventType": ["com.oraclecloud.objectstorage.object.create"],"data": {"bucketName": ["<input-bucket-name>"]}}' --compartment-id <compartment-ocid> --actions file://<json-file-name>`

Replace `<input-bucket-name>` with the (input) Object Storage bucket name where you will upload the image

e.g.

`oci --profile devrel-abhishek cloud-events rule create --display-name invoke-img2pdf-function --is-enabled true --condition '{"eventType": ["com.oraclecloud.objectstorage.object.create"],"data": {"bucketName": ["image-input-bucket"]}}' --compartment-id ocid1.compartment.oc1..aaaaaaaaokbzj2jn3hf5kwdwqoxl2dq7u54p3tsmxrjd7s3uu7x23tfoobar --actions file://actions.json`

## Test

Upload an image file to the Object Storage bucket which you configured as the input bucket (in the above rule). Wait for a bit and check the bucket which you configured as the output - you should see the converted PDF file there.