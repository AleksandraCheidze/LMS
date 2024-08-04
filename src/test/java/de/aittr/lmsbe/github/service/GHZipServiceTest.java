package de.aittr.lmsbe.github.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHContent;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GHZipServiceTest {

    @InjectMocks
    private GHZipService underTest;

    @Mock
    private GhDownloadService ghDownloadService;

    @Mock
    private GHContent mockContent;

    @Test
    @DisplayName("When the list contains valid GHContent objects, zipFilesToByteArray should return successfully zipped byte array stream")
    void testZipFilesToByteArray_ValidContent() throws Exception {
        when(mockContent.getPath()).thenReturn("validPath");
        when(mockContent.getEncoding()).thenReturn(GHZipService.BASE64_ENCODING);
        when(mockContent.read()).thenReturn(new ByteArrayInputStream("hello".getBytes()));

        ByteArrayOutputStream zippedBytes = underTest.zipFilesToByteArray(List.of(mockContent));
        assertNotNull(zippedBytes);
        assertTrue(zippedBytes.size() > 0);
        verify(ghDownloadService, never()).downloadFile(anyString());
    }

    @Test
    @DisplayName("When the list contains invalid GHContent objects, zipFilesToByteArray should still return successfully zipped byte array for the valid ones")
    void testZipFilesToByteArray_InvalidContent() throws Exception {
        when(mockContent.getPath()).thenReturn(null);

        ByteArrayOutputStream zippedBytes = underTest.zipFilesToByteArray(List.of(mockContent, mockContent));
        assertNotNull(zippedBytes);
        assertEquals(22, zippedBytes.size());
        verify(ghDownloadService, never()).downloadFile(anyString());
    }

    @Nested
    @DisplayName("When testing the private methods of the class with Reflections")
    class ReflectionTests {

        @Test
        @DisplayName("Test for the `modifyZipDirPath` method")
        void testModifyZipDirPath() throws Exception {
            Method method = GHZipService.class.getDeclaredMethod("modifyZipDirPath", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(underTest, "test/with/four/parts");

            assertEquals("parts", result);
        }
    }

    @Test
    @DisplayName("GhDownloadService should be invoked when the content encoding is not base64")
    void testProcessFileContent_withNonBase64Encoding() throws Exception {
        String downloadUrl = "https://someurl.com";

        when(mockContent.getPath()).thenReturn("validPath");
        when(mockContent.getEncoding()).thenReturn("non-base64");
        when(mockContent.getDownloadUrl()).thenReturn(downloadUrl);

        byte[] fileBytes = new byte[]{1, 2, 3};
        when(ghDownloadService.downloadFile(anyString())).thenReturn(fileBytes);

        underTest.zipFilesToByteArray(Collections.singletonList(mockContent));
        verify(ghDownloadService).downloadFile(downloadUrl);
    }

    @Test
    @DisplayName("GhDownloadService should be invoked when the content encoding is null")
    void testProcessFileContent_withNullBase64Encoding() throws Exception {
        String downloadUrl = "https://someurl.com";

        when(mockContent.getPath()).thenReturn("validPath");
        when(mockContent.getEncoding()).thenReturn(null);
        when(mockContent.getDownloadUrl()).thenReturn(downloadUrl);

        byte[] fileBytes = new byte[]{1, 2, 3};
        when(ghDownloadService.downloadFile(anyString())).thenReturn(fileBytes);

        underTest.zipFilesToByteArray(Collections.singletonList(mockContent));
        verify(ghDownloadService).downloadFile(downloadUrl);
    }

    @Test
    @DisplayName("GhDownloadService should not be invoked when the content encoding is base64")
    void testProcessFileContent_withBase64Encoding() throws Exception {
        when(mockContent.getPath()).thenReturn("validPath");
        when(mockContent.getEncoding()).thenReturn(GHZipService.BASE64_ENCODING);
        when(mockContent.read()).thenReturn(new ByteArrayInputStream("encodedString".getBytes()));

        underTest.zipFilesToByteArray(Collections.singletonList(mockContent));
        verify(ghDownloadService, never()).downloadFile(anyString());
    }
}
