package de.aittr.lmsbe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordRecoveryDto {

    @Schema(description = "User email", example = "example@gmail.com")
    @Email(message = "{email.invalid}")
    @NotNull(message = "{field.isNull}")
    @NotBlank(message = "{field.isBlank}")
    private String email;

}