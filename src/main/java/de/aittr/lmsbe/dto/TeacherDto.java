package de.aittr.lmsbe.dto;


import de.aittr.lmsbe.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(name = "TeacherDto", description = "User simple details")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto implements Serializable {

    @Schema(description = "user's identifier", example = "1")
    private Long id;

    @Schema(description = "user's firstname", example = "John")
    @NotBlank
    @Length(max = 100)
    private String firstName;

    @Schema(description = "user's lastname", example = "Doe")
    @NotBlank
    @Length(max = 100)
    private String lastName;

    @Schema(description = "user's email", example = "john.doe@ait-tr.de")
    @NotBlank
    @Length(max = 100)
    @Email
    private String email;

    public static TeacherDto from(User entity) {
        if (entity == null) {
            return null;
        }
        return new TeacherDto(entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail());
    }
}

