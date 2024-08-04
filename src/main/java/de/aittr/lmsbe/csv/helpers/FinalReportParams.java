package de.aittr.lmsbe.csv.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class FinalReportParams {

    String fileName;
    Long fileSize;
    String fileMD5;
    LocalDateTime uploadDate;
    int errorCounter;
    String uploadUserEmail;
    int addedUsers;
    int numberOfLines;
    String cloudFileName;
    String cloudValidationFileName;

}
