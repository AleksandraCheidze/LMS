package de.aittr.lmsbe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(description = "Data for update User")
public class UpdateUserDto extends NewUserDto {
    @Schema(description = "E-Mail zoom Account or Zoom Account ID", example = "user@user.com")
    private String zoomAccount;
}
