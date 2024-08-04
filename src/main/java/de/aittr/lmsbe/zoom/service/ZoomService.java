package de.aittr.lmsbe.zoom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.aittr.lmsbe.configuration.ZoomConfiguration;
import de.aittr.lmsbe.configuration.ZoomConstants;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.repository.UsersRepository;
import de.aittr.lmsbe.zoom.dto.ZoomParamsDto;
import de.aittr.lmsbe.zoom.meeting.ZoomMeetingResponse;
import de.aittr.lmsbe.zoom.meeting.ZoomMeetingSettings;
import de.aittr.lmsbe.zoom.model.verify.ZoomVerifyRequestDto;
import de.aittr.lmsbe.zoom.model.verify.ZoomVerifyResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;

@RequiredArgsConstructor
@Service
@Slf4j
public class ZoomService {

    private final ObjectMapper objectMapper;
    private final ZoomConfiguration zoomConfiguration;
    private final RestTemplate restTemplate;
    private final UsersRepository usersRepository;


    @SneakyThrows
    public String getZoomAccessToken() {
        URL url = new URL("https://zoom.us/oauth/token");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Host", "zoom.us");
        httpConn.setRequestProperty("Authorization", getBasicAuthenticationHeader(zoomConfiguration.getClientId(), zoomConfiguration.getClientSecret()));
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write("grant_type=account_credentials&account_id=" + zoomConfiguration.getAccountId());
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        if (StringUtils.isNotBlank(response)) {
            log.info("Token received");
        } else {
            log.error("Token not received");
        }
        s.close();
        responseStream.close();
        Map<String, Object> map = objectMapper.readValue(response, new TypeReference<>() {
        });
        return (String) map.get("access_token");
    }

    public ZoomVerifyResponseDto processVerifyRequest(ZoomVerifyRequestDto verifyDto) {

        String encryptedToken = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, zoomConfiguration.getSecretHookToken()).hmacHex(verifyDto.getPayload().getPlainToken());
        return new ZoomVerifyResponseDto(verifyDto.getPayload().getPlainToken(), encryptedToken);
    }

    public boolean isValidRequestSignature(String body, String requestSignature, String requestTimestamp) {
        log.info("requestSignature: " + requestSignature + " requestTimestamp: " + requestTimestamp);
        String message = "v0:" + requestTimestamp + ":" + body;
        log.info("message: " + message);
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, zoomConfiguration.getSecretHookToken()).hmacHex(message);
        String signature = "v0=" + hmac;
        log.info("signature: " + signature);

        return requestSignature.equals(signature);
    }

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    public ZoomMeetingResponse createMeeting(ZoomMeetingSettings zoomMeeting, User user) {
        validateZoomMeetingStartDateTime(zoomMeeting);
        if (user.getZoomAccount() == null) {
            throw new BadRequestException("Contact with administrator. You dont have Zoom Account in your profile");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getZoomAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String requestBody = objectMapper.writeValueAsString(zoomMeeting);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            String zoomPath = String.format(ZoomConstants.ZOOM_API_URL, user.getZoomAccount());
            ResponseEntity<ZoomMeetingResponse> responseEntity = restTemplate.postForEntity(zoomPath, requestEntity, ZoomMeetingResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                log.info("Zoom API request successful. Response: {}", responseEntity.getBody());
                return responseEntity.getBody();
            } else {
                log.error("Zoom API returned an unexpected response: HTTP status code = {}, Response body = {}", responseEntity.getStatusCode(), responseEntity.getBody());
                return null;
            }
        } catch (HttpClientErrorException e) {
            String message = e.getMessage();
            if (e.getStatusCode().is4xxClientError() && message != null && message.contains("1001")) {
                log.error("User does not have permission to create a Zoom meeting: ", e);
                throw new BadRequestException("Contact with administrator. You don't have permission to create a meeting.");
            }

            log.error("An error occurred while making the HTTP request: ", e);
            throw new RestException(HttpStatus.BAD_REQUEST, "An error occurred while making the HTTP request: " + e.getMessage());
        } catch (RestClientException | JsonProcessingException e) {
            log.error("Error while making the HTTP request or processing JSON: ", e);
            throw new RestException(HttpStatus.BAD_REQUEST, "An error occurred while making the HTTP request or processing JSON: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error while creating Zoom meeting: ", e);
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void validateZoomMeetingStartDateTime(ZoomMeetingSettings zoomMeeting) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = LocalDateTime.of(zoomMeeting.getDateToStart(), zoomMeeting.getTimeToStart());

        if (isStartTimeInPast(startDateTime, now)) {
            log.error("Error: The provided start time {} is in the past for agenda '{}'.", startDateTime, zoomMeeting.getAgenda());
            throw new RestException(HttpStatus.BAD_REQUEST, "The provided start time is in the past. Please provide a future date and time.");
        }

        zoomMeeting.setStartTime(startDateTime);
    }

    private boolean isStartTimeInPast(LocalDateTime startTime, LocalDateTime now) {
        return startTime.isBefore(now);
    }

    public ZoomMeetingResponse createParamMeeting(ZoomParamsDto dto, User user) {
        if (user.getRole() == null || user.getRole().equals(User.Role.STUDENT) || !user.getIsActive()) {
            throw new RestException(HttpStatus.FORBIDDEN, "No access");
        }
        ZoomMeetingSettings zoomMeetingSettings = getZoomMeetingSettings(dto);

        return createMeeting(zoomMeetingSettings, user);
    }

    public ZoomMeetingSettings getZoomMeetingSettings(ZoomParamsDto dto) {
        ZoomMeetingSettings zoomMeetingSettings = new ZoomMeetingSettings();
        zoomMeetingSettings.setTopic(dto.getAgenda());
        zoomMeetingSettings.setDuration(dto.getDuration());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.parse(dto.getTimeToStart(), formatter);
        zoomMeetingSettings.setTimeToStart(localTime);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dto.getDateToStart(), dateFormatter);
        zoomMeetingSettings.setDateToStart(localDate);

        zoomMeetingSettings.setAgenda(dto.getAgenda());
        return zoomMeetingSettings;
    }
}
