package de.aittr.lmsbe.github.service;

import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.github.model.GHFileExtension;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHContent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static de.aittr.lmsbe.github.model.GHFileExtension.getImageExtension;
import static de.aittr.lmsbe.github.model.GHFileExtension.languageByExtension;

/**
 * The GHFileEncodeService class provides methods to read and encode file content from GitHub.
 */
@Service
@RequiredArgsConstructor
public class GHFileEncodeService {

    /**
     * This class represents a service for downloading files from GitHub.
     */
    private final GhDownloadService ghDownloadService;

    /**
     * Reads the content of a file as a string.
     *
     * @param ghContent     the GHContent object representing the file
     * @param fileExtension the GHFileExtension of the file (optional, can be null)
     * @return the content of the file as a string
     * @throws RestException if an error occurs while reading the file
     */
    public String readFileContentAsString(GHContent ghContent, GHFileExtension fileExtension) {
        if (ghContent == null) {
            return "File not exist or invalid path to file";
        }
        GHFileExtension ghFileExtension = validateFileExtension(ghContent, fileExtension);

        if (getImageExtension().contains(ghFileExtension)) {
            return downloadAndReadImage(ghContent);
        }

        return new String(readFileContent(ghContent, ghFileExtension), StandardCharsets.UTF_8);
    }

    /**
     * Downloads and reads an image file.
     *
     * @param ghContent The GHContent object representing the file.
     * @return The content of the image file as a Base64-encoded string.
     * @throws RestException If an error occurs while downloading or reading the file.
     */
    private String downloadAndReadImage(GHContent ghContent) {
        String downloadUrl;
        try {
            downloadUrl = ghContent.getDownloadUrl();
        } catch (IOException e) {
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        byte[] fileBytes = ghDownloadService.downloadFile(downloadUrl);
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    /**
     * Validates the file extension by checking if it is null. If it is null, the language of the file is determined
     * based on the file name. Otherwise, the provided file extension is used.
     *
     * @param ghContent     the GHContent object representing the file
     * @param fileExtension the GHFileExtension of the file (optional, can be null)
     * @return the validated GHFileExtension object
     */
    private GHFileExtension validateFileExtension(GHContent ghContent, GHFileExtension fileExtension) {
        return fileExtension == null ? languageByExtension(ghContent.getName()) : fileExtension;
    }

    /**
     * Reads the content of a file as a byte array.
     *
     * @param ghContent     the GHContent object representing the file
     * @param fileExtension the GHFileExtension of the file (optional, can be null)
     * @return the content of the file as a byte array
     * @throws RestException if an error occurs while reading the file
     */
    private byte[] readFileContent(GHContent ghContent, GHFileExtension fileExtension) {

        if (fileExtension.isReadable()) {
            try (InputStream stream = ghContent.read()) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = stream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                return buffer.toByteArray();
            } catch (IOException e) {
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        return new byte[0];
    }
}
