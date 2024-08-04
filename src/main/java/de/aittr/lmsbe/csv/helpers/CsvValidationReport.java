package de.aittr.lmsbe.csv.helpers;

import de.aittr.lmsbe.csv.service.CsvValidationResult;
import de.aittr.lmsbe.dto.NewUserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CsvValidationReport {

    private List<CsvValidationResult> validationResults = new ArrayList<>();
    private List<NewUserDto> importedUsers = new ArrayList<>();
    private int errorsCounter;
    private int usersAdded;
    private int numberOfLines;
}
