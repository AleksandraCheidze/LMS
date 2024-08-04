package de.aittr.lmsbe.controller.api;

import de.aittr.lmsbe.dto.cohort.*;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Tags(value = {
        @Tag(name = "Groups")
})
@RequestMapping("/cohorts")
public interface CohortApi {


   
    /**
     * Retrieves all cohorts. Accessible by any authenticated user.
     *
     * @return A list of all cohorts.
     */
    @Operation(summary = "Retrieve all cohorts, works for Authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CohortDto.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    List<CohortDto> getAllCohorts();

    /**
     * Retrieves students by cohort ID. Accessible only by ADMIN.
     *
     * @param cohortId The ID of the cohort.
     * @return The cohort details with students.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/student/{cohortId}")
    CohortStudentDto getStudentByCohort(@PathVariable long cohortId);

    /**
     * Retrieves all student cohorts. Accessible only by ADMIN.
     *
     * @return A list of all student cohorts.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/student")
    List<StudentCohortDto> getAllStudentsCohort();

    /**
     * Retrieves all teacher cohorts within a date range. Accessible by ADMIN and TEACHER.
     *
     * @param currentUser The current authenticated user.
     * @param from The start date of the range.
     * @param to The end date of the range.
     * @return A list of teacher cohorts within the specified date range.
     */
    @PreAuthorize("hasAnyAuthority({'ADMIN', 'TEACHER'})")
    @GetMapping("/teacher")
    List<TeacherCohortDto> getAllTeachersCohort(@AuthenticationPrincipal @Parameter(hidden = true)
                                                AuthenticatedUser currentUser,
                                                @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                LocalDate from,
                                                @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                LocalDate to);

    /**
     * Filters student groups by a search term. Accessible only by ADMIN.
     *
     * @param scope The scope of the search.
     * @param term The search term.
     * @param searchPrimary Whether to search in primary cohorts.
     * @return A list of filtered student cohorts.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/student/filter")
    List<StudentCohortDto> filterGroupsByTerm(@RequestParam(value = "scope") String scope,
                                              @RequestParam(value = "term") String term,
                                              @RequestParam(value = "searchPrimary") boolean searchPrimary);
    @Operation(summary = "Create a new cohort", description = "ADMIN allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cohort created successfully",
                    content = @Content(schema = @Schema(implementation = CohortDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Cohort or repository already exists"),
    })
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    CohortRepoDto createCohort(@Valid @RequestBody CohortDto cohortDto);

    @Operation(summary = "Update an existing cohort", description = "ADMIN allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cohort updated successfully",
                    content = @Content(schema = @Schema(implementation = CohortDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data or cohort not exists"),
            @ApiResponse(responseCode = "409", description = "Cohort or repository  already exists"),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    CohortRepoDto updateCohort(@PathVariable Long id, @Valid @RequestBody CohortDto cohortDto);
}


