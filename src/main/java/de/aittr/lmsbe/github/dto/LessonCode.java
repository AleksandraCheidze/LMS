package de.aittr.lmsbe.github.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a lesson code, which contains information about a lesson code file.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Schema(description = "Details about a lesson code")
public class LessonCode {

    @Schema(description = "Path to lesson code file", example = "github/path/to/file")
    protected String pathToFile;

    @Schema(description = "File name of the lesson code", example = "lesson1.java")
    protected String fileName;

    @Schema(description = "File data of the lesson code", example = "public class Lesson1 {} (as bytes)")
    protected String fileData;

    @Schema(description = "Programming language of the lesson code", example = "Java")
    protected String codeLanguage;

    @Schema(description = "Indicates if the lesson code is readable", example = "true")
    protected boolean isReadable;

    @Schema(description = "Download URL for the lesson code", example = "http://github.com/download/...")
    protected String downloadUrl;

    public boolean isReadable() {
        return isReadable;
    }
}
