package de.aittr.lmsbe.csv.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CsvValidationResultForReport {
    private List<CsvValidationResult> validationResult;
    private int usersAdded;
    private int errorsCounter;
}
