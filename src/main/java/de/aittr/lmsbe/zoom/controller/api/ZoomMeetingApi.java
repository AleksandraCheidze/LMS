package de.aittr.lmsbe.zoom.controller.api;

import de.aittr.lmsbe.dto.StandardResponseDto;
import de.aittr.lmsbe.exception.UnauthorizedException;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import de.aittr.lmsbe.validation.dto.ValidationErrorsDto;
import de.aittr.lmsbe.zoom.dto.ZoomMeetingDto;
import de.aittr.lmsbe.zoom.dto.ZoomMeetingInfoDto;
import de.aittr.lmsbe.zoom.dto.ZoomParamsDto;
import de.aittr.lmsbe.zoom.meeting.ZoomMeetingSettings;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "ZoomMeeting", description = "Operations related to zoom meetings")
public interface ZoomMeetingApi {

    @Operation(summary = "Create new Zoom meeting", description = "ADMIN and TEACHER allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting is created",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ZoomMeetingDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Access to the requested resource is denied.",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @PostMapping("/create-meeting")
    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('TEACHER')")
    ZoomMeetingDto createMeeting(@Parameter(required = true, description = "Zoom meeting params")
                                 @RequestBody @Valid ZoomMeetingSettings zoomMeetingSettings,
                                 @AuthenticationPrincipal @Parameter(hidden = true) AuthenticatedUser currentUser);


    @Operation(summary = "Create new parametrized meeting", description = "ADMIN and TEACHER allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting is created",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ZoomMeetingInfoDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "401", description = "Unauthorized Exception",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedException.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Access to the requested resource is denied.",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @PostMapping("/create-param-meeting")
    @PreAuthorize("hasAnyAuthority({'ADMIN', 'TEACHER'})")
    ZoomMeetingInfoDto createParamMeeting(@Parameter(required = true, description = "Zoom meeting params")
                                             @RequestBody @Valid ZoomParamsDto zoomParamsDto,
                                          @AuthenticationPrincipal @Parameter(hidden = true) AuthenticatedUser currentUser);

    @Hidden
    @GetMapping("/google-calendar-callback")
    ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code, @RequestParam(value = "state", required = false) String state);

    @Operation(summary = "Get meetings for a user", description = "Retrieve a list of meetings scheduled for a particular user within a specific date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ZoomMeetingDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access to the resource is denied"),
            @ApiResponse(responseCode = "404", description = "The user was not found")
    })
    @GetMapping("/meetings/teacher")
    @PreAuthorize("hasAnyAuthority({'ADMIN', 'TEACHER'})")
    List<ZoomMeetingDto> getMeetingsByUser(
            @Parameter(required = true, example = "2024-06-01", description = "The start date of the range in YYYY-MM-DD format")
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @Parameter(required = true, example = "2024-06-31", description = "The end date of the range in YYYY-MM-DD format")
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,
            @AuthenticationPrincipal
            @Parameter(hidden = true)
            AuthenticatedUser currentUser
    );

    @Operation(summary = "Get meeting information by UUID", description = "Retrieve information about a meeting and related lessons based on the meeting's UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ZoomMeetingInfoDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access to the resource is denied"),
            @ApiResponse(responseCode = "404", description = "The meeting with provided UUID was not found")
    })
    @GetMapping("/meetings/{meetingUUID}")
    @PreAuthorize("hasAnyAuthority({'ADMIN', 'TEACHER'})")
    ZoomMeetingInfoDto getMeetingInfoByLessonId(
            @Parameter(required = true, example = "123e4567-e89b-12d3-a456", description = "The UUID of the meeting")
            @PathVariable String meetingUUID);
}
