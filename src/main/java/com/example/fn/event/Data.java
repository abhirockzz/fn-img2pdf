
package com.example.fn.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "namespace",
    "displayName",
    "eTag",
    "bucketId",
    "bucketName",
    "bucketFreeformTags",
    "bucketDefinedTags",
    "archivalState"
})
public class Data {

    @JsonProperty("namespace")
    private String namespace;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("eTag")
    private String eTag;
    @JsonProperty("bucketId")
    private String bucketId;
    @JsonProperty("bucketName")
    private String bucketName;
    @JsonProperty("bucketFreeformTags")
    private BucketFreeformTags bucketFreeformTags;
    @JsonProperty("bucketDefinedTags")
    private BucketDefinedTags bucketDefinedTags;
    @JsonProperty("archivalState")
    private String archivalState;

    @JsonProperty("namespace")
    public String getNamespace() {
        return namespace;
    }

    @JsonProperty("namespace")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("eTag")
    public String getETag() {
        return eTag;
    }

    @JsonProperty("eTag")
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    @JsonProperty("bucketId")
    public String getBucketId() {
        return bucketId;
    }

    @JsonProperty("bucketId")
    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    @JsonProperty("bucketName")
    public String getBucketName() {
        return bucketName;
    }

    @JsonProperty("bucketName")
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @JsonProperty("bucketFreeformTags")
    public BucketFreeformTags getBucketFreeformTags() {
        return bucketFreeformTags;
    }

    @JsonProperty("bucketFreeformTags")
    public void setBucketFreeformTags(BucketFreeformTags bucketFreeformTags) {
        this.bucketFreeformTags = bucketFreeformTags;
    }

    @JsonProperty("bucketDefinedTags")
    public BucketDefinedTags getBucketDefinedTags() {
        return bucketDefinedTags;
    }

    @JsonProperty("bucketDefinedTags")
    public void setBucketDefinedTags(BucketDefinedTags bucketDefinedTags) {
        this.bucketDefinedTags = bucketDefinedTags;
    }

    @JsonProperty("archivalState")
    public String getArchivalState() {
        return archivalState;
    }

    @JsonProperty("archivalState")
    public void setArchivalState(String archivalState) {
        this.archivalState = archivalState;
    }

}
