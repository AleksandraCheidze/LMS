package de.aittr.lmsbe.csv.service;

import de.aittr.lmsbe.csv.helpers.CsvValidationReport;
import de.aittr.lmsbe.dto.NewUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvFileDataValidationService {

    private final CsvFilesProcessingService csvFilesProcessingService;
    private final CsvDataValidationRules csvDataValidationRules;

    @Value("${csv.lineLimit:100}")
    int maxNumberOfRecords;

    public CsvValidationReport validateDataOfCsv(MultipartFile file) {
        List<String[]> csvData = csvFilesProcessingService.parseCsvFile(file);
        CsvValidationReport report = new CsvValidationReport();
        if (csvData.isEmpty()) {
            return report;
        }

        List<CsvValidationResult> validationResults = report.getValidationResults();

        int lines = csvData.size();

        if (lines > maxNumberOfRecords) {
            CsvValidationResult csvValidationResult = new CsvValidationResult(0,
                    List.of(new CsvFieldError(Strings.EMPTY,
                            "Error: the number of records must not be more than "
                                    + maxNumberOfRecords)));
            validationResults.add(csvValidationResult);
            return report;
        }

        List<NewUserDto> importedUsers = report.getImportedUsers();
        Set<String> emailsSet = new HashSet<>();

        //start reading since 2 line, 1 line in csv-file is a capture
        for (int startReadLine = 1; startReadLine < lines; startReadLine++) {
            int currentLine = (startReadLine + 1);

            String[] validatedDataOfCsv = csvData.get(startReadLine);
            List<CsvFieldError> csvFieldErrors = new ArrayList<>();
            CsvValidationResult csvValidationResult = new CsvValidationResult(currentLine, csvFieldErrors);
            String name = getField(validatedDataOfCsv, 0);
            String lastName = getField(validatedDataOfCsv, 1);
            String email = getField(validatedDataOfCsv, 2);
            String role = getField(validatedDataOfCsv, 3);
            String primaryGroup = getField(validatedDataOfCsv, 4);

            csvDataValidationRules.validateColumnName("NAME", name, csvFieldErrors);
            csvDataValidationRules.validateColumnName("LAST_NAME", lastName, csvFieldErrors);
            csvDataValidationRules.validateColumnEmail(email, role, csvFieldErrors);
            csvDataValidationRules.validateColumnRole(role, csvFieldErrors);
            csvDataValidationRules.validateColumnPrimaryGroup(primaryGroup, role, csvFieldErrors);

            if (!emailsSet.add(email)) {
                csvFieldErrors.add(new CsvFieldError("EMAIL", "Email " + email + " is already presented in csv"));
            }

            validationResults.add(csvValidationResult);

            if (csvFieldErrors.isEmpty()) {
                importedUsers.add(NewUserDto
                        .builder()
                        .firstName(name)
                        .lastName(lastName)
                        .email(email)
                        .role(role)
                        .cohort(primaryGroup)
                        .build());
            }
        }

        int errorsCounter = 0;
        for (CsvValidationResult validationResult : validationResults) {
            List<CsvFieldError> errors = validationResult.getErrors();
            errorsCounter += errors.size();
        }

        if (errorsCounter == 0) {
            report.setUsersAdded(importedUsers.size());
        } else {
            report.setUsersAdded(0);
        }

        report.setErrorsCounter(errorsCounter);
        report.setNumberOfLines(lines - 1);

        return report;
    }

    private String getField(String[] records, int index) {
        return (index >= 0 && index < records.length) ? records[index] : Strings.EMPTY;
    }

}

