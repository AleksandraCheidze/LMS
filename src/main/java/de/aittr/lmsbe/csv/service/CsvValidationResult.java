package de.aittr.lmsbe.csv.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CsvValidationResult {

    private int lineNr;
    private List<CsvFieldError> errors;
}
