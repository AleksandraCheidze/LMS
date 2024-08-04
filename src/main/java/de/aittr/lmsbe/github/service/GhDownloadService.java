package de.aittr.lmsbe.github.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * This class represents a service for downloading files from GitHub.
 */
@Service
@RequiredArgsConstructor
public class GhDownloadService {

    @Value("${github.key}")
    private String githubKey;

    private final RestTemplate restTemplate;

    /**
     * Downloads a file from the given GitHub download URL.
     *
     * @param ghDownloadUrl the GitHub download URL of the file
     * @return the byte array containing the downloaded file, or an empty byte array if the download fails
     */
    public byte[] downloadFile(String ghDownloadUrl) {
        if (StringUtils.isBlank(ghDownloadUrl)) {
            return new byte[0];
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", githubKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    URLDecoder.decode(ghDownloadUrl, StandardCharsets.UTF_8),
                    HttpMethod.GET, entity, byte[].class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody() == null ? new byte[0] : response.getBody();
            } else {
                return new byte[0];
            }
        } catch (RestClientException e) {
            return new byte[0];
        }
    }
}
