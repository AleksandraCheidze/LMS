package de.aittr.lmsbe.feedback.controller.api;

import de.aittr.lmsbe.dto.StandardResponseDto;
import de.aittr.lmsbe.feedback.dto.LessonFeedbackDto;
import de.aittr.lmsbe.feedback.dto.NewLessonFeedbackDto;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import de.aittr.lmsbe.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Tags(value = {
        @Tag(name = "LessonFeedback")
})
@RequestMapping("/lesson-feedback")
public interface LessonFeedbackApi {

    @Operation(summary = "Add new lesson feedback", description = "Only authenticated user with confirmed state allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question is created",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = LessonFeedbackDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "409", description = "Conflict error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{cohortId}/{lessonModule}/{lessonType}/{lessonNr}")
    @ResponseStatus(HttpStatus.CREATED)
    LessonFeedbackDto addLessonFeedback(@Parameter(required = true, description = "LessonFeedback")
                                        @Valid @RequestBody NewLessonFeedbackDto newLessonFeedbackDto,
                                        @PathVariable Long cohortId,
                                        @PathVariable String lessonModule,
                                        @PathVariable String lessonType,
                                        @PathVariable Integer lessonNr,
                                        @AuthenticationPrincipal @Parameter(hidden = true) AuthenticatedUser currentUser);


    @Operation(summary = "Get list of all lesson feedbacks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all lesson feedbacks",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = LessonFeedbackDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    List<LessonFeedbackDto> getAllLessonFeedbacks();

    @Operation(summary = "Get list of my lesson feedbacks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of my lesson feedbacks",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = LessonFeedbackDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/my")
    List<LessonFeedbackDto> getMyLessonFeedbacks(@AuthenticationPrincipal @Parameter(hidden = true) AuthenticatedUser currentUser);

    @Operation(summary = "Get list of filtered lesson feedbacks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of filtered lesson feedbacks",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = LessonFeedbackDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/filtered")
    List<LessonFeedbackDto> getFilteredLessonFeedbacks(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long cohortId,
            @RequestParam(required = false) String lessonModule,
            @RequestParam(required = false) String lessonType,
            @RequestParam(required = false) Integer lessonNr,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate
    );
}
