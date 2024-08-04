package de.aittr.lmsbe.github.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for GhDownloadService")
class GhDownloadServiceTest {

    @InjectMocks
    private GhDownloadService unterTest;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private ResponseEntity<byte[]> mockResponse;

    private final String validUrl = "http://valid.url";

    @BeforeEach
    public void setUp() {
        String githubKey = "valid.key";
        ReflectionTestUtils.setField(unterTest, "githubKey", githubKey);

    }

    @Test
    @DisplayName("Should download file successfully")
    void downloadFile_success() {

        byte[] fileContent = new byte[]{1, 2, 3};

        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(mockResponse.getBody()).thenReturn(fileContent);
        when(mockRestTemplate.exchange(eq(validUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(byte[].class)))
                .thenReturn(mockResponse);

        byte[] result = unterTest.downloadFile(validUrl);
        assertArrayEquals(fileContent, result);
        verify(mockRestTemplate, times(1)).exchange(eq(validUrl),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(byte[].class));
    }

    @Test
    @DisplayName("Should return empty array when file content is null")
    void downloadFile_success_responseContentNull() {

        byte[] fileContent = null;

        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(mockResponse.getBody()).thenReturn(fileContent);
        when(mockRestTemplate.exchange(eq(validUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(byte[].class)))
                .thenReturn(mockResponse);

        byte[] result = unterTest.downloadFile(validUrl);
        assertArrayEquals(new byte[0], result);
        verify(mockRestTemplate, times(1)).exchange(eq(validUrl),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(byte[].class));
    }

    @Test
    @DisplayName("Should fail to download file when url is invalid")
    void downloadFile_fail_invalidUrl() {
        String invalidUrl = "";

        byte[] result = unterTest.downloadFile(invalidUrl);
        assertEquals(0, result.length);
        verify(mockRestTemplate, never()).exchange(anyString(),
                any(HttpMethod.class), any(HttpEntity.class), eq(byte[].class));
    }

    @Test
    @DisplayName("Should fail to download file when url is invalid (null)")
    void downloadFile_fail_invalidUrlNull() {
        String invalidUrl = null;

        byte[] result = unterTest.downloadFile(invalidUrl);
        assertEquals(0, result.length);
        verify(mockRestTemplate, never()).exchange(anyString(),
                any(HttpMethod.class), any(HttpEntity.class), eq(byte[].class));
    }

    @Test
    @DisplayName("Should fail to download file when status code is not 2xx")
    void downloadFile_fail_not2xx() {

        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(mockRestTemplate.exchange(eq(validUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(byte[].class)))
                .thenReturn(mockResponse);

        byte[] result = unterTest.downloadFile(validUrl);
        assertEquals(0, result.length);
        verify(mockRestTemplate, times(1)).exchange(eq(validUrl),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(byte[].class));
    }
}
