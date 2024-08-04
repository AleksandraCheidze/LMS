package de.aittr.lmsbe.csv.service;

import lombok.Value;

@Value
public class CsvFieldError {

    String columnName;
    String message;
}