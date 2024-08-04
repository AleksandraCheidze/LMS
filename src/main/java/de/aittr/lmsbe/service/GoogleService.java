package de.aittr.lmsbe.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.service.interfaces.IGoogleService;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import de.aittr.lmsbe.zoom.repository.ZoomMeetingRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

@Service
@Data
@Slf4j
public class GoogleService implements IGoogleService {

    private static final String APPLICATION_NAME = "AIT TR TEST APP";
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static Calendar calendar;

    private final ZoomMeetingRepository zoomMeetingRepository;

    GoogleClientSecrets clientSecrets;
    GoogleAuthorizationCodeFlow flow;
    Credential credential;

    @Value("${google.client.client-id}")
    private String clientId;

    @Value("${google.client.client-secret}")
    private String clientSecret;

    @Value("${google.client.redirectUri}")
    private String redirectURI;

    @Override
    public String googleAuthorizeWithUUID(String uuid) {

        AuthorizationCodeRequestUrl authorizationUrl;
        try {

            if (flow == null) {
                Details web = new Details();
                web.setClientId(clientId);
                web.setClientSecret(clientSecret);
                clientSecrets = new GoogleClientSecrets().setWeb(web);
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                        Collections.singleton(CalendarScopes.CALENDAR)).build();
            }
            authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI).setState(uuid);
            log.debug("Google authorization Url: " + authorizationUrl.toString());
            return authorizationUrl.build();
        } catch (Exception e) {
            log.error("An error occurred while authorizing with UUID: {}", uuid, e);
            throw new BadRequestException("An error occurred while authorizing with UUID:" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> googleOauth2Callback(@RequestParam(value = "code") String code, @RequestParam(value = "state", required = false) String state) {
        try {
            String uuid = URLDecoder.decode(state, StandardCharsets.UTF_8);
            log.debug("Received UUID is: " + uuid);
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            credential = flow.createAndStoreCredential(response, "userID");
            calendar = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
            createEventToGoogleCalendarWithUuid(uuid);
        } catch (Exception e) {
            log.warn("Exception while handling OAuth2 callback (" + e.getMessage());
            throw new BadRequestException("An error occurred with OAuth2 callback:" + e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public void createEventToGoogleCalendarWithUuid(String uuid) {
        try {
            log.debug("Received UUID is: " + uuid);
            uuid = uuid.replace(" ", "+");
            log.debug("Replaced UUID is: " + uuid);
            ZoomMeeting zoomMeeting = zoomMeetingRepository.findByUuid(uuid);
            if (zoomMeeting == null) {
                throw new BadRequestException("Invalid UUID value: " + uuid);
            }
            TimeZone berlinTimeZone = TimeZone.getTimeZone("Europe/Berlin");
            LocalDateTime startMeetingTime = zoomMeeting.getMeetingTime();
            ZonedDateTime startZonedDateTime = startMeetingTime.atZone(berlinTimeZone.toZoneId());
            DateTime startDateTime = new DateTime(Date.from(startZonedDateTime.toInstant()));
            int durationMinutes = zoomMeeting.getDuration();
            LocalDateTime endMeetingTime = startMeetingTime.plusMinutes(durationMinutes);
            ZonedDateTime endZonedDateTime = endMeetingTime.atZone(berlinTimeZone.toZoneId());
            DateTime endDateTime = new DateTime(Date.from(endZonedDateTime.toInstant()));
            Event event = new Event()
                    .setSummary(zoomMeeting.getAgenda())
                    .setDescription("Host URL:\n" + zoomMeeting.getHostUrl())
                    .setStart(new EventDateTime().setDateTime(startDateTime))
                    .setEnd(new EventDateTime().setDateTime(endDateTime))
                    .setLocation("Invite URL:\n" + zoomMeeting.getInviteUrl());

            event.setConferenceData(new ConferenceData()
                    .setCreateRequest(new CreateConferenceRequest()
                            .setRequestId(uuid)
                    ));

            calendar.events().insert("primary", event).execute();
        } catch (Exception e) {
            log.error("An error occurred while creating event in Google Calendar", e);
        }
    }

    @Override
    public String getAuthorizationUrl(String uuid) {
        return googleAuthorizeWithUUID(uuid);
    }

}
