package de.aittr.lmsbe.feedback.controller;

import de.aittr.lmsbe.feedback.controller.api.QuestionApi;
import de.aittr.lmsbe.feedback.dto.AnswerDto;
import de.aittr.lmsbe.feedback.dto.NewQuestionDto;
import de.aittr.lmsbe.feedback.dto.QuestionDto;
import de.aittr.lmsbe.feedback.service.QuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class QuestionController implements QuestionApi {

    QuestionService questionService;

    @Override
    public QuestionDto addQuestion(NewQuestionDto newQuestion) {
        return questionService.addQuestion(newQuestion);
    }


    @Override
    public List<QuestionDto> getAllQuestions(String scope) {
        return questionService.getAllQuestions(scope);
    }

    @Override
    public QuestionDto updateQuestion(NewQuestionDto questionDto, Long questionId) {
        return questionService.updateQuestion(questionDto, questionId);
    }

    @Override
    public QuestionDto updateAnswer(AnswerDto newAnswer, Long questionId) {
        return questionService.updateAnswer(newAnswer, questionId);
    }
}
