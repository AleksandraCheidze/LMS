package de.aittr.lmsbe.github.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class GHUploadFile {
    private MultipartFile file;
    private String fileDestination;
}
