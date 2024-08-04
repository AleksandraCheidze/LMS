package de.aittr.lmsbe.zoom.controller;

import de.aittr.lmsbe.security.details.AuthenticatedUser;
import de.aittr.lmsbe.service.GoogleService;
import de.aittr.lmsbe.zoom.controller.api.ZoomMeetingApi;
import de.aittr.lmsbe.zoom.dto.ZoomMeetingDto;
import de.aittr.lmsbe.zoom.dto.ZoomMeetingInfoDto;
import de.aittr.lmsbe.zoom.dto.ZoomParamsDto;
import de.aittr.lmsbe.zoom.meeting.ZoomMeetingSettings;
import de.aittr.lmsbe.zoom.service.ZoomMeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ZoomMeetingController implements ZoomMeetingApi {

    private final ZoomMeetingService zoomMeetingService;
    private final GoogleService googleService;


    @Override
    public ZoomMeetingDto createMeeting(ZoomMeetingSettings zoomMeetingSettings, AuthenticatedUser currentUser) {
        return zoomMeetingService.createAndSaveMeeting(zoomMeetingSettings, currentUser.getUser());
    }

    @Override
    public ZoomMeetingInfoDto createParamMeeting(ZoomParamsDto zoomParamsDto, AuthenticatedUser currentUser) {
        return zoomMeetingService.createAndSaveMeetingWithLesson(zoomParamsDto, currentUser.getUser());
    }

    @Override
    public ResponseEntity<String> oauth2Callback(String code, String state) {
        return googleService.googleOauth2Callback(code, state);
    }

    @Override
    public List<ZoomMeetingDto> getMeetingsByUser(LocalDate from, LocalDate to, AuthenticatedUser currentUser) {
        return zoomMeetingService.getMeetingsByUser(currentUser.getUser(), from, to);
    }

    @Override
    public ZoomMeetingInfoDto getMeetingInfoByLessonId(String meetingUUID) {
        return zoomMeetingService.getMeetingInfoByMeetingUUID(meetingUUID);
    }
}
