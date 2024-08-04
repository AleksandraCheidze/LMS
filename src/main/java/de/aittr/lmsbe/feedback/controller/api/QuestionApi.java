package de.aittr.lmsbe.feedback.controller.api;

import de.aittr.lmsbe.dto.StandardResponseDto;
import de.aittr.lmsbe.feedback.dto.AnswerDto;
import de.aittr.lmsbe.feedback.dto.NewQuestionDto;
import de.aittr.lmsbe.feedback.dto.QuestionDto;
import de.aittr.lmsbe.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tags(value = {
        @Tag(name = "Question")
})
@RequestMapping("/question")
public interface QuestionApi {

    @Operation(summary = "Add new question", description = "Only ADMIN allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question is created",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionDto.class))
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    QuestionDto addQuestion(@Parameter(required = true, description = "Question")
                            @RequestBody @Valid NewQuestionDto newQuestion);


    @Operation(summary = "Get list of all questions (scope = lesson, week, all)", description = "Only ADMIN allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions list",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionDto.class))
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
    @GetMapping("/get-questions/{scope}")
    List<QuestionDto> getAllQuestions(@PathVariable String scope);

    @Operation(summary = "Update question", description = "Only ADMIN allowed")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Question is updated",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Question not found error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),

    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{questionId}/update-question")
    QuestionDto updateQuestion(
            @RequestBody NewQuestionDto questionDto, @PathVariable Long questionId);

    @Operation(summary = "Add new answer", description = "Only ADMIN allowed")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Question is updated",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Question not found error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),

    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{questionId}/update-answer")
    QuestionDto updateAnswer(
            @Valid @RequestBody AnswerDto newAnswer, @PathVariable Long questionId
    );
}
