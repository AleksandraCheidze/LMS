package de.aittr.lmsbe.feedback.service;

import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.exception.NotFoundException;
import de.aittr.lmsbe.feedback.dto.AnswerDto;
import de.aittr.lmsbe.feedback.dto.NewQuestionDto;
import de.aittr.lmsbe.feedback.dto.QuestionDto;
import de.aittr.lmsbe.feedback.model.Answer;
import de.aittr.lmsbe.feedback.model.Question;
import de.aittr.lmsbe.feedback.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.aittr.lmsbe.feedback.dto.QuestionDto.from;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionDto addQuestion(NewQuestionDto newQuestion) {

        // if question is active it must be final
        boolean isFinal = newQuestion.isActive() || newQuestion.isFinal();

        Question question = Question.builder()
                .questionText(newQuestion.getQuestionText().trim())
                .answers(new ArrayList<>())
                .isMultiCheck(newQuestion.isMultiCheck())
                .isTextOn(newQuestion.isTextOn())
                .isActive(newQuestion.isActive())
                .isFinal(isFinal)
                .isLesson(newQuestion.isLesson())
                .timestamp(LocalDateTime.now())
                .build();

        newQuestion.getAnswers().forEach(answerDto -> {
            Answer answer = new Answer();
            answer.setAnswerText(answerDto.getAnswerText());
            answer.setQuestion(question);
            answer.setRate(rateCheck(answerDto));
            question.getAnswers().add(answer);
        });

        questionRepository.save(question);

        return from(question);
    }


    public List<QuestionDto> getAllQuestions(String scope) {
        List<Question> questionList = questionRepository.findAll();

        switch (scope) {
            case "lesson":
                List<Question> lessonQuestions = questionList.stream()
                        .filter(question -> question.isActive() && question.isLesson())
                        .collect(Collectors.toList());
                return QuestionDto.from(lessonQuestions);

            case "week":
                List<Question> weekQuestions = questionList.stream()
                        .filter(question -> question.isActive() && !question.isLesson())
                        .collect(Collectors.toList());
                return QuestionDto.from(weekQuestions);

            case "all":
                return QuestionDto.from(questionList);

            default:
                throw new BadRequestException("Incorrect scope parameter value: " + scope);

        }
    }

    public QuestionDto updateQuestion(NewQuestionDto questionDto, Long questionId) {
        Question question = getQuestionOrThrow(questionId);

        if (question.isFinal()) {
            question.setActive(questionDto.isActive());
        } else {
            BeanUtils.copyProperties(questionDto, question, "id");
            question.setFinal(questionDto.isActive() || question.isFinal());

            List<AnswerDto> questionDtoAnswers = questionDto.getAnswers();

            if (questionDtoAnswers != null && !questionDtoAnswers.isEmpty()) {

                for (AnswerDto answerDto : questionDtoAnswers) {
                    rateCheck(answerDto);
                    Optional<Answer> optionalAnswer = question.getAnswers().stream()
                            .filter(answer -> answer.getId().equals(answerDto.getId()))
                            .findFirst();
                    if (optionalAnswer.isPresent()) {
                        Answer answer = optionalAnswer.get();
                        BeanUtils.copyProperties(answerDto, answer, "id");
                    } else {
                        throw new NotFoundException("Answer", answerDto.getId());
                    }
                }
            }
        }

        Question updatedQuestion = questionRepository.save(question);

        return QuestionDto.from(updatedQuestion);
    }

    public QuestionDto updateAnswer(AnswerDto newAnswer, Long questionId) {
        Question question = getQuestionOrThrow(questionId);

        if (question.isFinal()) {
            throw new BadRequestException("This question is already FINAL!");
        }

        Answer answer = new Answer();
        answer.setAnswerText(newAnswer.getAnswerText());
        answer.setQuestion(question);
        answer.setRate(rateCheck(newAnswer));
        question.getAnswers().add(answer);

        Question updatedQuestion = questionRepository.save(question);

        return QuestionDto.from(updatedQuestion);
    }

    public Question getQuestionOrThrow(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(
                () -> new NotFoundException("Question", questionId));
    }

    private Integer rateCheck(AnswerDto answerDto) {
        int rate = answerDto.getRate();
        if (rate < 1 || rate > 5) {
            throw new BadRequestException("Rate out of range");
        }
        return rate;
    }
}
