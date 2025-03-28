package de.aittr.lmsbe.validation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationErrorDto {

    private String field;
    private String message;
    private String rejectedValue;
}
