
package com.example.fn.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "compartmentId"
})
public class Extensions {

    @JsonProperty("compartmentId")
    private String compartmentId;

    @JsonProperty("compartmentId")
    public String getCompartmentId() {
        return compartmentId;
    }

    @JsonProperty("compartmentId")
    public void setCompartmentId(String compartmentId) {
        this.compartmentId = compartmentId;
    }

}
