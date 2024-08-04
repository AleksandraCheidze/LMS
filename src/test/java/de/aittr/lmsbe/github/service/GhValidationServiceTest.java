package de.aittr.lmsbe.github.service;

import de.aittr.lmsbe.github.exception.GithubUploadFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;


/**
 * GhUploadValidatorService Test class for validating file upload functionality.
 *
 * @author Andrej Reutow
 * created on 01.04.2024
 */
@ExtendWith(MockitoExtension.class)
class GhValidationServiceTest {

    @InjectMocks
    private GhValidationService ghValidationService;

    @BeforeEach
    void setUp() {
        int maxUploadFile = 300;
        long maxUploadFileSize = 500;

        ReflectionTestUtils.setField(ghValidationService, "maxGhFileToUpload", maxUploadFile);
        ReflectionTestUtils.setField(ghValidationService, "maxFileSize", maxUploadFileSize);
    }

    @DisplayName("Tests validateMaxUploadSize with acceptable size.")
    @Test
    void testValidateMaxUploadSizeAcceptableSize() {
        int fileSize = 100;

        try {
            ghValidationService.validateMaxUploadSize(fileSize);
        } catch (GithubUploadFileException e) {
            fail("Validation should not fail with size: " + fileSize);
        }
    }

    @DisplayName("Tests validateMaxUploadSize with size exceeding the limit.")
    @Test
    void testValidateMaxUploadSizeExcessiveSize() {
        int fileSize = 400;

        Exception exception = assertThrows(GithubUploadFileException.class, () -> {
            ghValidationService.validateMaxUploadSize(fileSize);
        });

        String expectedMessage = "The number of files exceeds the maximum limit. Max files to upload: 300, but received: ";

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testValidateFileSize_fileSizeUnderMax_limit() {
        Long fileSize = 100L;
        ghValidationService.validateFileSize(fileSize);
    }

    @Test
    void testValidateFileSize_fileSizeOverMax_limit() {
        Long fileSize = 10000000L;
        assertThrows(GithubUploadFileException.class, () -> ghValidationService.validateFileSize(fileSize));
    }
}
