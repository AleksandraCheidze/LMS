package de.aittr.lmsbe.zoom.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Payload {
    private String accountId;
    @JsonProperty("object")
    private ZoomObjectData zoomObjectData;
}
