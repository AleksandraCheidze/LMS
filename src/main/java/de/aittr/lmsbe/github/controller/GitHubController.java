package de.aittr.lmsbe.github.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.aittr.lmsbe.github.dto.LessonCode;
import de.aittr.lmsbe.github.dto.LessonMeta;
import de.aittr.lmsbe.github.model.GHUploadFile;
import de.aittr.lmsbe.github.model.GHUploadFileInfo;
import de.aittr.lmsbe.github.service.GHServiceImpl;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static de.aittr.lmsbe.github.model.GHLessonType.CONSULTATION;
import static de.aittr.lmsbe.github.model.GHLessonType.LESSON;

@RestController
@RequestMapping("github")
@RequiredArgsConstructor
public class GitHubController {

    private final GHServiceImpl gitHubService;

    @Operation(summary = "Returns All Modules by Cohort")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched the modules successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "404", description = "Cohort not found")
    })
    @GetMapping("/{cohort}")
    public List<String> getModuls(@PathVariable String cohort) {
        return gitHubService.getLessonModuls(cohort);
    }

    @Operation(summary = "Returns Lessons by Cohort and Module")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched the lessons successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "404", description = "Module or cohort not found")
    })
    @GetMapping("/{cohort}/{module}")
    public List<String> getLessons(
            @Parameter(description = "The cohort to fetch the lessons from",
                    required = true,
                    example = "36")
            @PathVariable String cohort,
            @Parameter(description = "The module to fetch the lessons from",
                    required = true,
                    example = "basic_programming")
            @PathVariable String module) {
        return gitHubService.getLessons(cohort, module, LESSON);
    }

    @Operation(summary = "Returns Lesson Metadata by Cohort, Module, and Lesson Number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched the lesson metadata successfully",
                    content = @Content(schema = @Schema(implementation = LessonMeta.class))),
            @ApiResponse(responseCode = "404", description = "Lesson, module or cohort not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{cohort}/{module}/{lessonNr}")
    public LessonMeta getLessonCode(
            @Parameter(description = "The cohort to which the video material belongs",
                    required = true,
                    example = "36")
            @PathVariable String cohort,
            @Parameter(description = "The module associated with the video material",
                    required = true,
                    example = "basic_programming")
            @PathVariable String module,
            @Parameter(description = "The lesson number with the video material",
                    required = true,
                    example = "09")
            @PathVariable String lessonNr,
            @Parameter(hidden = true, description = "Current user")
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        final User user = currentUser == null ? null : currentUser.getUser();
        return gitHubService.getlessonMeta(cohort, module, lessonNr, user, LESSON);
    }

    @Operation(summary = "Returns Consultations by Cohort and Module")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched the consultations successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "404", description = "Module or cohort not found")
    })
    @GetMapping("/{cohort}/{module}/consultation")
    public List<String> getConsultations(@PathVariable String cohort,
                                         @PathVariable String module) {
        return gitHubService.getLessons(cohort, module, CONSULTATION);
    }

    @Operation(summary = "Returns Consultation Metadata by Cohort, Module, and Lesson Number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched the consultation metadata successfully",
                    content = @Content(schema = @Schema(implementation = LessonMeta.class))),
            @ApiResponse(responseCode = "404", description = "Consultation, module or cohort not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{cohort}/{module}/{lessonNr}/consultation")
    public LessonMeta getConsultationCode(
            @Parameter(description = "The cohort to which the consultation material belongs",
                    required = true,
                    example = "36")
            @PathVariable String cohort,
            @Parameter(description = "The module associated with the consultation material",
                    required = true,
                    example = "basic_programming")
            @PathVariable String module,
            @Parameter(description = "The consultation number with the material",
                    required = true,
                    example = "09")
            @PathVariable String lessonNr,
            @Parameter(hidden = true, description = "Current user")
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        final User user = currentUser == null ? null : currentUser.getUser();
        return gitHubService.getlessonMeta(cohort, module, lessonNr, user, CONSULTATION);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{cohort}/{module}/{lessonNr}/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void addFiles(@PathVariable String cohort,
                         @PathVariable String module,
                         @PathVariable String lessonNr,
                         @RequestParam("files") MultipartFile[] files,
                         @RequestParam("fileDestinations") String jsonInfo,
                         @AuthenticationPrincipal AuthenticatedUser currentUser) throws JsonProcessingException {

        final List<GHUploadFile> ghUploadFiles = new ArrayList<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<GHUploadFileInfo> fileDestinations = objectMapper.readValue(jsonInfo, new TypeReference<>() {
        });
        for (GHUploadFileInfo GHUploadFileInfo : fileDestinations) {
            ghUploadFiles.add(new GHUploadFile(files[GHUploadFileInfo.getFileIndex()], GHUploadFileInfo.getPath()));
        }

        gitHubService.addFilesToLesson(cohort,
                module,
                lessonNr,
                currentUser.getUser(),
                ghUploadFiles);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{cohort}/{module}/{lessonNr}/hw")
    public LessonMeta getHomeWorkByLesson(@PathVariable String cohort,
                                          @PathVariable String module,
                                          @PathVariable String lessonNr,
                                          @AuthenticationPrincipal AuthenticatedUser currentUser) {

        return gitHubService.getHomeWorkFiles(cohort,
                module,
                lessonNr,
                currentUser.getUser());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/{cohort}/file/{filePath:.+}")
    public LessonCode deleteRepoFile(@PathVariable String cohort,
                                     @PathVariable String filePath,
                                     @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return gitHubService.deleteRepoFile(cohort, filePath.replace("~", "/"), currentUser.getUser());
    }
}

