package de.aittr.lmsbe.csv.dto;

import lombok.Value;

@Value
public class CsvImportDto {

    boolean isValid;
    String reportFileLink;
    String validationFileLink;
}
