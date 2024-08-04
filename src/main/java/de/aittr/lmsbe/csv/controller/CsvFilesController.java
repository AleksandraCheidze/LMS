package de.aittr.lmsbe.csv.controller;

import de.aittr.lmsbe.csv.controller.api.CsvFileApi;
import de.aittr.lmsbe.csv.dto.CsvImportDto;
import de.aittr.lmsbe.csv.service.CsvFilesUploadService;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class CsvFilesController implements CsvFileApi {

    private final CsvFilesUploadService csvFilesUploadService;

    @Override
    public CsvImportDto csvFileUpload(MultipartFile file, AuthenticatedUser currentUser) throws IOException {
        return csvFilesUploadService.csvFileUpload(file, currentUser.getUser());
    }
}
