package de.aittr.lmsbe.github.uttils;

import de.aittr.lmsbe.github.dto.LessonCode;
import de.aittr.lmsbe.github.model.GHFileExtension;
import de.aittr.lmsbe.github.service.GHFileEncodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHContent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

import static de.aittr.lmsbe.github.model.GHFileExtension.languageByExtension;

/**
 * The GithubHelper class is a utility class that provides methods for interacting with GitHub content.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GHHelper {

    private final GHFileEncodeService ghFileEncodeService;

    /**
     * Represents the file path separator.
     *
     * <p>
     * The PATH_SEPARATOR variable is a string that defines the separator used to separate components
     * in a file path. In Java, the forward slash ("/") is commonly used as the path separator on most
     * operating systems.
     * <br>
     * For example, if you have a file path: "C:/Users/Documents/file.txt"
     * </p>
     *
     * <p>
     * Usage:
     *
     * <pre>
     * String path = "C:" + PATH_SEPARATOR + "Users" + PATH_SEPARATOR + "Documents" + PATH_SEPARATOR + "file.txt";
     * </pre>
     * </p>
     */
    public static final String PATH_SEPARATOR = "/";

    /**
     * Creates a LessonCode object based on the provided GHContent.
     *
     * @param ghContent the GHContent object representing the lesson code
     * @return a LessonCode object created from the provided GHContent
     */
    public LessonCode createLessonCode(GHContent ghContent) {
        final String fileName = ghContent.getName();
        final String repositoryUrl = ghContent.getPath();
        final GHFileExtension language = languageByExtension(fileName);
        final String codeContent = ghFileEncodeService.readFileContentAsString(ghContent, language);
        final String downloadUrl = getDownloadUrlSafely(ghContent);

        return new LessonCode(repositoryUrl,
                fileName,
                codeContent,
                language.getLanguage(),
                language.isReadable(),
                downloadUrl);
    }


    public LessonCode createLessonCode(GHContent ghContent, String pathToCode, String fileName) {
        if (ghContent == null) {
            final GHFileExtension language = languageByExtension(fileName);
            return new LessonCode(
                    pathToCode,
                    fileName,
                    "File not exist or invalid path to file",
                    language.getLanguage(),
                    language.isReadable(),
                    "");
        } else {
            return createLessonCode(ghContent);
        }
    }

    /**
     * Safely retrieves the download URL of a GHContent object.
     * If an IOException occurs while getting the download URL, an empty string will be returned.
     *
     * @param ghContent the GHContent object from which to retrieve the download URL
     * @return the download URL as a string, or an empty string if an IOException occurs
     */
    private String getDownloadUrlSafely(GHContent ghContent) {
        try {
            return ghContent.getDownloadUrl();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }


    /**
     * Determines if the given file name is ignored.
     *
     * @param name The name of the file to check.
     * @return {@code true} if the file name is ignored, {@code false} otherwise.
     */
    public static boolean isFileIgnored(String name) {
        if (StringUtils.isBlank(name)) {
            return true;
        }
        return GHFileExtension.UNKNOWN.equals(GHFileExtension.languageByExtension(name));
    }

    /**
     * Checks if the given file destination is ignored.
     *
     * @param fileDestination The file destination to be checked.
     * @return {@code true} if the file destination is ignored, {@code false} otherwise.
     */
    public static boolean isDestinationIgnored(String fileDestination) {
        if (StringUtils.isBlank(fileDestination)) {
            return true;
        }
        return Arrays.stream(fileDestination.split(PATH_SEPARATOR))
                .anyMatch(GHDirectoryIgnore.IGNORE_PATHS::contains);
    }
}
