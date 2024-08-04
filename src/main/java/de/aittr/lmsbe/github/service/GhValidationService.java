package de.aittr.lmsbe.github.service;

import de.aittr.lmsbe.github.exception.GithubUploadFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The GhUploadValidatorService class is responsible for validating file uploads in a GitHub repository.
 */
@Service
@Slf4j
public class GhValidationService {
    private static final String UPLOAD_ERROR_FORMAT = "The file size exceeds the maximum limit. Max file size to upload: "
            + "%d bytes, but received file size: %d bytes. Check the uploading file size and try again.";

    /**
     * The maximum size (in bytes) for a file upload in a GitHub repository.
     * <p>
     * Default value: 300
     */
    @Value("${git.upload.file.max:300}")
    private int maxGhFileToUpload;

    /**
     * The maximum file size (in bytes) for file uploads in a GitHub repository.
     * <p>
     * This variable is obtained from the `git.upload.file.maxFileSize` property, which can be configured externally. If the size of a file to be uploaded exceeds this maximum limit
     * , a `GithubUploadFileException` will be thrown.
     * <p>
     * Usage example:
     * GhValidationService service = new GhValidationService();
     * service.validateFileSize(1024); // Throws GithubUploadFileException if file size exceeds maxFileSize
     */
    @Value("${git.upload.file.maxFileSize}")
    private long maxFileSize;

    /**
     * Validates the maximum upload size of a file.
     *
     * @param fileSize The size of the file to be uploaded.
     * @throws GithubUploadFileException if the file size exceeds the maximum limit.
     */
    public void validateMaxUploadSize(int fileSize) {
        if (fileSize > maxGhFileToUpload) {
            final String uploadStatusMessage = "The number of files exceeds the maximum limit. Max files to upload: "
                    + maxGhFileToUpload + ", but received: " + fileSize + ". " +
                    "Check the quantity of uploading files and try again.";
            throw new GithubUploadFileException(uploadStatusMessage);
        }
    }

    /**
     * Validates the size of a file to be uploaded.
     *
     * @param size The size of the file to be uploaded.
     * @throws GithubUploadFileException If the file size exceeds the maximum limit.
     */
    public void validateFileSize(final Long size) {
        if (size > maxFileSize) {
            String uploadStatusMessage = String.format(UPLOAD_ERROR_FORMAT, maxFileSize, size);
            throw new GithubUploadFileException(uploadStatusMessage);
        }
    }
}
