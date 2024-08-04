package de.aittr.lmsbe.zoom.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import de.aittr.lmsbe.zoom.model.json.Payload;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RecordingCompletedDto {
    private Payload payload;
    private Long eventTs;
    private String event;
    private String downloadToken;
}
