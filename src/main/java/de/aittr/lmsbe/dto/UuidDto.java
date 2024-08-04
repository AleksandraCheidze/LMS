package de.aittr.lmsbe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "UUID for confirm registration")
public class UuidDto {

    @Schema(description = "UUID", example = "6c2f764a-9f9c-4856-8bad-61097e099e7c")
    @NotNull(message = "{field.isNull}")
    @NotBlank(message = "{field.isBlank}")
    private String uuid;
}
