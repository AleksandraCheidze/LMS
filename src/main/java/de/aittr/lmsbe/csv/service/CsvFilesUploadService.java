package de.aittr.lmsbe.csv.service;

import de.aittr.lmsbe.csv.dto.CsvImportDto;
import de.aittr.lmsbe.csv.helpers.CsvValidationReport;
import de.aittr.lmsbe.csv.helpers.FinalReportParams;
import de.aittr.lmsbe.csv.model.CsvFileInfo;
import de.aittr.lmsbe.csv.repository.CsvFilesRepository;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import de.aittr.lmsbe.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor


public class CsvFilesUploadService {

    @Value("${csv.bucketNameForValidationReports}")
    String bucketNameForValidationReports;

    @Value("${csv.bucketNameForImportReports}")
    String bucketNameForImportReports;

    private final CsvFilesRepository csvFilesRepository;

    private final CsvFilesProcessingService csvFilesProcessingService;

    private final UsersService usersService;

    private final FilesSaveToS3Service filesSaveToS3Service;

    private final PdfReportCreationService pdfReportCreationService;

    private final CsvFileDataValidationService csvFileDataValidationService;

    public CsvImportDto csvFileUpload(MultipartFile file, User user) throws IOException {

        String fileName = file.getOriginalFilename();
        checkFileParams(file, fileName);

        csvFilesProcessingService.parseCsvFile(file);

        String csvFileMd5 = csvFilesProcessingService.calculatedMd5(file.getBytes());
        String importedFileLink = filesSaveToS3Service.saveCsvFileToS3(file);
        CsvValidationReport validationResults = csvFileDataValidationService.validateDataOfCsv(file);

        List<CsvValidationResult> validationResultsValidationResults = validationResults.getValidationResults();
        PDDocument pdfReportOfValidation = pdfReportCreationService.createPdfReportOfValidation(validationResultsValidationResults);
        String validationReportLink = filesSaveToS3Service.saveValidationReportTOS3(pdfReportOfValidation, "validation_report.pdf", bucketNameForValidationReports);

        FinalReportParams finalReportParams = new FinalReportParams(
                fileName,
                file.getSize(),
                csvFileMd5,
                LocalDateTime.now(),
                validationResults.getErrorsCounter(),
                user.getEmail(),
                validationResults.getUsersAdded(),
                validationResults.getNumberOfLines(),
                getCloudFileName(importedFileLink),
                getCloudFileName(validationReportLink)
        );

        boolean isValid = validationResults.getErrorsCounter() == 0;
        String result = isValid ? (validationResults.getUsersAdded() + " users added") : "import impossible";

        PDDocument finalReport = pdfReportCreationService.createReportOfImport(finalReportParams, "Mass user registration", result);
        String importReportLink = filesSaveToS3Service.saveValidationReportTOS3(finalReport, "report_of_import.pdf", bucketNameForImportReports);

        save(user, csvFileMd5, importedFileLink, validationReportLink, importReportLink);

        if (isValid) {
            AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            validationResults.getImportedUsers()
                    .forEach(newUser -> usersService.registerUser(newUser, currentUser == null ? null : currentUser.getUser()));
        }

        return new CsvImportDto(
                isValid,
                importReportLink,
                validationReportLink);
    }

    private static void checkFileParams(MultipartFile file, String fileName) {
        String extension;
        if (fileName != null) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            throw new BadRequestException("Original file name must not be null");
        }

        if (!extension.equalsIgnoreCase("csv") || (file.getSize() >= 30L * 1024L * 1024L)) {
            throw new BadRequestException("File size or file extension does not meet the requirements. Requirements: size up to 30mb," +
                    " extension *.csv");
        }
    }

    public void save(User user,
                     String csvFileMd5,
                     String importedFileLink,
                     String validationReportLink,
                     String importReportLink) {
        CsvFileInfo csvFileInfo = CsvFileInfo.builder()
                .csvFileUploadTime(LocalTime.now())
                .csvFileUploadDate(LocalDate.now())
                .user(user)
                .csvFileMD5(csvFileMd5)
                .importedFilePath(importedFileLink)
                .validationReportPath(validationReportLink)
                .importReportPath(importReportLink)
                .build();
        csvFilesRepository.save(csvFileInfo);
    }

    private String getCloudFileName(String url) {
        int startIndex = url.lastIndexOf("/");
        String filePath = url.substring(startIndex + 1);
        return URLDecoder.decode(filePath, StandardCharsets.UTF_8);
    }
}

