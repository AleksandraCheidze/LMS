package de.aittr.lmsbe.csv.model;

import de.aittr.lmsbe.model.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsvFileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String csvFileMD5;

    @Column
    private LocalDate csvFileUploadDate;

    @Column
    private LocalTime csvFileUploadTime;

    @Column
    private String importedFilePath;

    @Column
    private String validationReportPath;

    @Column
    private String importReportPath;

    @ManyToOne
    @JoinColumn(name = "user_email", nullable = false)
    private User user;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CsvFileInfo csvFileInfo = (CsvFileInfo) o;

        return Objects.equals(id, csvFileInfo.id) &&
                Objects.equals(csvFileMD5, csvFileInfo.csvFileMD5) &&
                Objects.equals(csvFileUploadDate, csvFileInfo.csvFileUploadDate) &&
                Objects.equals(csvFileUploadTime, csvFileInfo.csvFileUploadTime) &&
                Objects.equals(importedFilePath, csvFileInfo.importedFilePath) &&
                Objects.equals(validationReportPath, csvFileInfo.validationReportPath) &&
                Objects.equals(importReportPath, csvFileInfo.importReportPath) &&
                Objects.equals(user, csvFileInfo.user);
    }

    @Override
    public final int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (csvFileMD5 != null ? csvFileMD5.hashCode() : 0);
        result = 31 * result + (csvFileUploadDate != null ? csvFileUploadDate.hashCode() : 0);
        result = 31 * result + (csvFileUploadTime != null ? csvFileUploadTime.hashCode() : 0);
        result = 31 * result + (importedFilePath != null ? importedFilePath.hashCode() : 0);
        result = 31 * result + (validationReportPath != null ? validationReportPath.hashCode() : 0);
        result = 31 * result + (importReportPath != null ? importReportPath.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "CsvFileInfo{" +
                "id=" + id +
                ", csvFileMD5='" + csvFileMD5 + '\'' +
                ", csvFileUploadDate=" + csvFileUploadDate +
                ", csvFileUploadTime=" + csvFileUploadTime +
                ", importedFilePath='" + importedFilePath + '\'' +
                ", validationReportPath='" + validationReportPath + '\'' +
                ", importReportPath='" + importReportPath + '\'' +
                ", userId=" + (user == null ? "null" : String.valueOf(user.getId()) +
                '}');
    }
}
