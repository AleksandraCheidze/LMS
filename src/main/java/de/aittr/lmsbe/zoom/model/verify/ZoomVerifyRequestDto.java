package de.aittr.lmsbe.zoom.model.verify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZoomVerifyRequestDto {
    private Payload payload;
    @JsonProperty("event_ts")
    private Long eventTs;
    private String event;
}
