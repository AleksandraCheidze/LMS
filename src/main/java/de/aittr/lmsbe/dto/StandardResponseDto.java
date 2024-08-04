package de.aittr.lmsbe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "StandardResponseDto", description = "response details")
public class StandardResponseDto {

    @Schema(description = "message")
    private String message;
    @Schema(description = "HTTP-status")
    private int status;
}
