package de.aittr.lmsbe.controller.api;

import de.aittr.lmsbe.security.details.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Tag(name = "VideoController", description = "Operations related to video materials")
public interface VideoApi {

    /**
     * Method to retrieve a list of presigned links to video materials.
     *
     * @param cohort    The cohort to which the video material belongs.
     * @param module    The module associated with the video material.
     * @param type      The type of video material (optional). If not specified, the default value will be used.
     * @param lessonsNr The lesson number with the video material.
     * @return A list of presigned links to video materials.
     */

    @Operation(summary = "Retrieve presigned links to video materials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "Permission is forbidden error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/get-videos")
    @ResponseStatus(HttpStatus.OK)
    List<String> getVideos(
            @Parameter(description = "The cohort to which the video material belongs", required = true, example = "36")
            @RequestParam String cohort,

            @Parameter(description = "The module associated with the video material", required = true, example = "basic_programming")
            @RequestParam String module,

            @Parameter(description = "The type of video material (optional). If not specified, the default value will be used.", example = "lecture")
            @RequestParam(required = false) String type,

            @Parameter(description = "The lesson number with the video material", required = true, example = "09")
            @RequestParam String lessonsNr,

            @Parameter(hidden = true, description = "current user")
            @AuthenticationPrincipal AuthenticatedUser currentUser);
}
