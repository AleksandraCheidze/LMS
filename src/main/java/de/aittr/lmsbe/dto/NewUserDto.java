package de.aittr.lmsbe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "Data for adding User")
public class NewUserDto {

    @Schema(description = "User group alias", example = "Cohort 21", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    protected String cohort;

    @Schema(description = "User email", example = "example@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "{email.invalid}")
    @NotBlank(message = "{field.isBlank}")
    protected String email;

    @Schema(description = "User first name", example = "Jack", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{field.isBlank}")
    @Size(min = 2, max = 50, message = "{name.size}")
    @Pattern(regexp = "^[a-zA-Z-'\\s]{2,50}$", message = "{name.invalid}")
    protected String firstName;

    @Schema(description = "User last name", example = "Black", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{field.isBlank}")
    @Size(min = 2, max = 50, message = "{name.size}")
    @Pattern(regexp = "^[a-zA-Z-'\\s]{2,50}$", message = "{name.invalid}")
    protected String lastName;

    @Schema(description = "User country", example = "Germany", requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @Size(min = 2, max = 30, message = "{country.size}")
    @Pattern(regexp = "^[a-zA-Z]+(?:[-\\s][a-zA-Z]+)*[a-zA-Z]$", message = "{country.invalid}")
    protected String country;

    @Schema(description = "User role: STUDENT, TEACHER, ADMIN", example = "STUDENT", requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    protected String role;

    @Schema(description = "User phone number", example = "+49 123 4567890", requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    @Pattern(regexp = "^(\\+\\d{1,2}\\s?)?\\d{2,5}[-\\s]?\\d{3,12}$", message = "{phone.invalid}")
    protected String phone;
}
