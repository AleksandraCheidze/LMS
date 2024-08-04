package de.aittr.lmsbe.service.interfaces;

import de.aittr.lmsbe.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface IGoogleService {

    /**
     * Generates an authorization URL with the provided UUID for Google OAuth2.
     *
     * @param uuid The unique identifier to include in the authorization URL state parameter.
     * @return The generated Google OAuth2 authorization URL.
     * @throws BadRequestException if an error occurs during authorization.
     */
    String googleAuthorizeWithUUID(String uuid);

    /**
     * Handles the Google OAuth2 callback, processes the authorization code, and creates an event in Google Calendar.
     *
     * @param code  The authorization code received from the OAuth2 callback.
     * @param state The state parameter received from the OAuth2 callback, which should contain the UUID.
     * @return A ResponseEntity indicating the status of the callback processing.
     * @throws BadRequestException if an error occurs during the OAuth2 callback processing.
     */
    ResponseEntity<String> googleOauth2Callback(String code, String state);

    /**
     * Creates an event in Google Calendar using the provided UUID to look up the associated Zoom meeting details.
     *
     * @param uuid The unique identifier of the Zoom meeting.
     * @throws BadRequestException if the UUID is invalid or the Zoom meeting is not found.
     * @throws RuntimeException    if an error occurs while interacting with Google Calendar API.
     */
    void createEventToGoogleCalendarWithUuid(String uuid);

    /**
     * Retrieves the Google OAuth2 authorization URL with the provided UUID.
     *
     * @param uuid The unique identifier to include in the authorization URL state parameter.
     * @return The generated Google OAuth2 authorization URL.
     */
    String getAuthorizationUrl(String uuid);
}
