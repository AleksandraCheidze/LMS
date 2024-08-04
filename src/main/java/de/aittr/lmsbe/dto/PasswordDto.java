package de.aittr.lmsbe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordDto {

    @Schema(description = "UUID", example = "6c2f764a-9f9c-4856-8bad-61097e099e7c")
    @NotNull(message = "{field.isNull}")
    @NotBlank(message = "{field.isBlank}")
    private String uuid;

    @Schema(description = "Password", example = "Strong_password-123")
    @NotNull(message = "{field.isNull}")
    @NotBlank(message = "{field.isBlank}")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[-_@#$%^&+=!])(?=\\S+$).{8,}$", message = "{password.isWeak}")
    private String password;
}
