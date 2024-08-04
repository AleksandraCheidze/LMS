package de.aittr.lmsbe.zoom.model.verify;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZoomVerifyResponseDto {
    private String plainToken;
    private String encryptedToken;
}
