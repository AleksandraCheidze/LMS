package de.aittr.lmsbe.zoom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.aittr.lmsbe.zoom.dto.RecordingCompletedDto;
import de.aittr.lmsbe.zoom.model.verify.ZoomVerifyRequestDto;
import de.aittr.lmsbe.zoom.service.ZoomEventProcessingService;
import de.aittr.lmsbe.zoom.service.ZoomService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@Profile("!dev")
@RequiredArgsConstructor
@RestController
@Slf4j
public class ZoomEventController {

    private final ObjectMapper objectMapper;
    private final ZoomEventProcessingService zoomEventProcessingService;
    private final ZoomService zoomService;
    private static final String RECORDING_COMPLETE_EVENT_NAME = "recording.completed";


    @PostMapping("/uploadToS3")
    @SneakyThrows
    public ResponseEntity<?> uploadFile(HttpEntity<String> httpEntity,
                                        @RequestHeader(name = "x-zm-signature", required = false) String requestSignature,
                                        @RequestHeader(name = "x-zm-request-timestamp", required = false)
                                        String requestTimestamp) {


        String body = httpEntity.getBody();
        if (!zoomService.isValidRequestSignature(body, requestSignature, requestTimestamp)) {
            log.warn("Request is not valid");
            return ResponseEntity.badRequest().build();
        }

        ZoomVerifyRequestDto verifyDto = objectMapper.readValue(body, ZoomVerifyRequestDto.class);
        if (verifyDto.getEvent().equals("endpoint.url_validation")) {
            return ResponseEntity.ok(zoomService.processVerifyRequest(verifyDto));
        }

        RecordingCompletedDto recordingCompletedDto = objectMapper.readValue(body, RecordingCompletedDto.class);
        if (RECORDING_COMPLETE_EVENT_NAME.equals(recordingCompletedDto.getEvent())) {
            log.debug("Receive recordingCompletedDto...");
            log.debug("ZoomObjectData '{}'", recordingCompletedDto.getPayload().getZoomObjectData());
            log.debug("Event '{}', EventTs '{}'", recordingCompletedDto.getEvent(), recordingCompletedDto.getEventTs());
            log.debug("'{}'", recordingCompletedDto.getEventTs());
            zoomEventProcessingService.processRecordingCompleteEvent(recordingCompletedDto);
            return ResponseEntity.ok().build();
        }

        log.warn("Not expected event name: {}, expected: {}", recordingCompletedDto.getEvent(), RECORDING_COMPLETE_EVENT_NAME);
        return ResponseEntity.badRequest().build();
    }
}
