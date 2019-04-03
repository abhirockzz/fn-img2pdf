
package com.example.fn.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cloudEventsVersion",
    "eventID",
    "eventType",
    "source",
    "eventTypeVersion",
    "eventTime",
    "schemaURL",
    "contentType",
    "extensions",
    "data"
})
public class OCICloudEvent {

    @JsonProperty("cloudEventsVersion")
    private String cloudEventsVersion;
    @JsonProperty("eventID")
    private String eventID;
    @JsonProperty("eventType")
    private String eventType;
    @JsonProperty("source")
    private String source;
    @JsonProperty("eventTypeVersion")
    private String eventTypeVersion;
    @JsonProperty("eventTime")
    private String eventTime;
    @JsonProperty("schemaURL")
    private Object schemaURL;
    @JsonProperty("contentType")
    private String contentType;
    @JsonProperty("extensions")
    private Extensions extensions;
    @JsonProperty("data")
    private Data data;

    @JsonProperty("cloudEventsVersion")
    public String getCloudEventsVersion() {
        return cloudEventsVersion;
    }

    @JsonProperty("cloudEventsVersion")
    public void setCloudEventsVersion(String cloudEventsVersion) {
        this.cloudEventsVersion = cloudEventsVersion;
    }

    @JsonProperty("eventID")
    public String getEventID() {
        return eventID;
    }

    @JsonProperty("eventID")
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    @JsonProperty("eventType")
    public String getEventType() {
        return eventType;
    }

    @JsonProperty("eventType")
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("eventTypeVersion")
    public String getEventTypeVersion() {
        return eventTypeVersion;
    }

    @JsonProperty("eventTypeVersion")
    public void setEventTypeVersion(String eventTypeVersion) {
        this.eventTypeVersion = eventTypeVersion;
    }

    @JsonProperty("eventTime")
    public String getEventTime() {
        return eventTime;
    }

    @JsonProperty("eventTime")
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    @JsonProperty("schemaURL")
    public Object getSchemaURL() {
        return schemaURL;
    }

    @JsonProperty("schemaURL")
    public void setSchemaURL(Object schemaURL) {
        this.schemaURL = schemaURL;
    }

    @JsonProperty("contentType")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("contentType")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("extensions")
    public Extensions getExtensions() {
        return extensions;
    }

    @JsonProperty("extensions")
    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    @JsonProperty("data")
    public Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Data data) {
        this.data = data;
    }

}
