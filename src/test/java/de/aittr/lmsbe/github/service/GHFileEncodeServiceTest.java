package de.aittr.lmsbe.github.service;

import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.github.model.GHFileExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHContent;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GHFileEncodeServiceTest {
    @Mock
    private GhDownloadService ghDownloadService;
    @Mock
    private GHContent ghContent;

    @InjectMocks
    private GHFileEncodeService underTest;

    @Test
    @DisplayName("Should return 'File not exist or invalid path to file' if ghContent is null")
    void shouldReturnMessageWhenGhContentIsNull() {
        String result = underTest.readFileContentAsString(null, GHFileExtension.JAVA);
        assertEquals("File not exist or invalid path to file", result);
    }

    @Test
    @DisplayName("Should return encoded string of image content")
    void shouldReturnImageContent() throws IOException {
        byte[] imageBytes = {123, 45, 67};
        when(ghDownloadService.downloadFile(anyString())).thenReturn(imageBytes);
        when(ghContent.getDownloadUrl()).thenReturn("url");

        String result = underTest.readFileContentAsString(ghContent, GHFileExtension.JPG);

        String expectedResult = Base64.getEncoder().encodeToString(imageBytes);
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Should throw RestException when IOException occurs in downloading image")
    void shouldThrowExceptionWhenImageDownloadFails() throws IOException {
        when(ghContent.getDownloadUrl()).thenThrow(IOException.class);

        assertThrows(RestException.class, () -> underTest.readFileContentAsString(ghContent, GHFileExtension.JPG));
    }

    @Test
    @DisplayName("Should return string of file content when file is readable")
    void shouldReturnFileContentAsString() throws IOException {
        InputStream is = new ByteArrayInputStream("This is a string".getBytes(StandardCharsets.UTF_8));
        when(ghContent.read()).thenReturn(is);

        String result = underTest.readFileContentAsString(ghContent, GHFileExtension.JAVA);
        assertEquals("This is a string", result);
    }

    @Test
    @DisplayName("Should return empty string when file is not readable")
    void shouldReturnEmptyStringFileNotReadable() {
        String result = underTest.readFileContentAsString(ghContent, GHFileExtension.PDF);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Should throw RestException when IOException occurs in reading file content")
    void shouldThrowExceptionWhenFileContentReadingFails() throws IOException {
        when(ghContent.read()).thenThrow(IOException.class);

        assertThrows(RestException.class, () -> underTest.readFileContentAsString(ghContent, GHFileExtension.JAVA));
    }
}
