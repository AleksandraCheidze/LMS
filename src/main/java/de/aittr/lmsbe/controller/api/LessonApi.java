package de.aittr.lmsbe.controller.api;

import de.aittr.lmsbe.dto.LessonDto;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "LessonController", description = "Operations related to lessons")
@RequestMapping("/lesson")
public interface LessonApi {

    /**
     * Method to retrieve a lesson by its details.
     *
     * @param groupName  The group name associated with the lesson.
     * @param moduleName The module name associated with the lesson.
     * @param typeName   The type name associated with the lesson.
     * @param lessonNr   The lesson number.
     * @return The lesson details.
     */
    @Operation(summary = "Retrieve a lesson by its details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = LessonDto.class))),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @GetMapping("/{groupName}/{moduleName}/{typeName}/{lessonNr}")
    LessonDto getLesson(
            @Parameter(description = "The group name associated with the lesson", required = true, example = "Cohort 25")
            @PathVariable String groupName,
            @Parameter(description = "The module name associated with the lesson", required = true, example = "basic_programming")
            @PathVariable String moduleName,
            @Parameter(description = "The type name associated with the lesson", required = true, example = "lecture")
            @PathVariable String typeName,
            @Parameter(description = "The lesson number", required = true, example = "09")
            @PathVariable Integer lessonNr
    );

    /**
     * Method to retrieve lessons within a date range.
     *
     * @param currentUser The authenticated user.
     * @param cohortId    The ID of the cohort.
     * @param from        The start date.
     * @param to          The end date.
     * @param modules     The list of module names.
     * @param types       The list of lesson types.
     * @return The list of lessons.
     */
    @Operation(summary = "Retrieve lessons within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LessonDto.class)))),
            @ApiResponse(responseCode = "403", description = "Permission denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyAuthority({'ADMIN', 'TEACHER'})")
    @GetMapping("/cohort/{cohortId}")
    List<LessonDto> getLessonsByUserBetweenDate(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Parameter(description = "The ID of the cohort", required = true, example = "123") @PathVariable long cohortId,
            @Parameter(description = "The start date", required = true, example = "2024-05-01") @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "The end date", required = true, example = "2024-05-31") @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "The list of module names", required = true) @RequestParam(value = "modules") List<String> modules,
            @Parameter(description = "The list of lesson types", required = true) @RequestParam(value = "types") List<String> types
    );

    /**
     * Method to retrieve completed lessons by the authenticated user.
     *
     * @param teacherId The ID of the teacher.
     * @param cohortId  The ID of the cohort.
     * @param from      The start date.
     * @param to        The end date.
     * @param modules   The list of module names.
     * @param types     The list of lesson types.
     * @return The list of completed lessons.
     */
    @Operation(summary = "Retrieve completed lessons by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LessonDto.class)))),
            @ApiResponse(responseCode = "403", description = "Permission denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyAuthority({'ADMIN'})")
    @GetMapping("teacher/{teacherId}/cohort/{cohortId}/completed")
    List<LessonDto> getLessonsByAuthUserBetweenDate(
            @Parameter(description = "The ID of the teacher", required = true, example = "456") @PathVariable long teacherId,
            @Parameter(description = "The ID of the cohort", required = true, example = "123") @PathVariable long cohortId,
            @Parameter(description = "The start date", required = true, example = "2024-05-01") @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "The end date", required = true, example = "2024-05-31") @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "The list of module names", required = true) @RequestParam(value = "modules") List<String> modules,
            @Parameter(description = "The list of lesson types", required = true) @RequestParam(value = "types") List<String> types
    );
}


