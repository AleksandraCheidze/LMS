package de.aittr.lmsbe.feedback.service;

import de.aittr.lmsbe.dto.LessonDto;
import de.aittr.lmsbe.dto.UserDto;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.feedback.dto.*;
import de.aittr.lmsbe.feedback.model.Answer;
import de.aittr.lmsbe.feedback.model.FeedbackAnswer;
import de.aittr.lmsbe.feedback.model.LessonFeedback;
import de.aittr.lmsbe.feedback.model.Question;
import de.aittr.lmsbe.feedback.repository.FeedbackAnswerRepository;
import de.aittr.lmsbe.feedback.repository.LessonFeedbackRepository;
import de.aittr.lmsbe.model.Lesson;
import de.aittr.lmsbe.model.LessonModul;
import de.aittr.lmsbe.model.LessonType;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.service.LessonService;
import de.aittr.lmsbe.service.cohort.CohortService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LessonFeedbackService {

    private final LessonService lessonService;
    private final QuestionService questionService;
    private final CohortService cohortService;
    private final LessonFeedbackRepository lessonFeedbackRepository;
    private final FeedbackAnswerRepository feedbackAnswerRepository;

    public LessonFeedbackDto addLessonFeedback(NewLessonFeedbackDto newLessonFeedbackDto,
                                               String lessonModule,
                                               String lessonType,
                                               Integer lessonNr,
                                               Long cohortId,
                                               User student) {

        if (!cohortService.checkUserCohorts(student, cohortId)) {
            throw new BadRequestException("User can't leave feedback for this cohortId: " + cohortId);
        }

        Lesson lesson = lessonService.findLesson(lessonModule, lessonType, lessonNr, cohortId);

        List<FeedbackAnswer> feedbackAnswers = buildFeedbackAnswers(newLessonFeedbackDto.getFeedbackAnswerDtoList());

        saveFeedbackAnswers(feedbackAnswers);

        LessonFeedback lessonFeedback = saveLessonFeedback(student, lesson, feedbackAnswers);

        return lessonFeedbackBuilder(lessonFeedback);
    }

    private List<FeedbackAnswer> buildFeedbackAnswers(List<FeedbackAnswerDto> feedbackAnswerDtoList) {
        return feedbackAnswerDtoList.stream()
                .map(this::buildFeedbackAnswer)
                .collect(Collectors.toList());
    }

    private FeedbackAnswer buildFeedbackAnswer(FeedbackAnswerDto feedbackAnswerDto) {
        Question question = questionService.getQuestionOrThrow(feedbackAnswerDto.getQuestionId());
        Answer selectedAnswer = null;
        String answerText = null;

        if (feedbackAnswerDto.getAnswerId() != null) {
            selectedAnswer = findAnswerById(question.getAnswers(), feedbackAnswerDto.getAnswerId());
        } else {
            answerText = feedbackAnswerDto.getAnswerText();
        }

        return FeedbackAnswer.builder()
                .question(question)
                .answer(selectedAnswer)
                .answerText(answerText)
                .build();
    }

    private Answer findAnswerById(List<Answer> answers, Long answerId) {
        return answers.stream()
                .filter(answer -> Objects.equals(answer.getId(), answerId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Answer not found"));
    }

    private void saveFeedbackAnswers(List<FeedbackAnswer> feedbackAnswers) {
        feedbackAnswerRepository.saveAll(feedbackAnswers);
    }

    private LessonFeedback saveLessonFeedback(User student, Lesson lesson, List<FeedbackAnswer> feedbackAnswers) {
        LessonFeedback lessonFeedback = LessonFeedback.builder()
                .student(student)
                .teacher(lesson.getTeacher())
                .lesson(lesson)
                .timestamp(LocalDateTime.now())
                .selectedAnswers(feedbackAnswers)
                .build();
        return lessonFeedbackRepository.save(lessonFeedback);
    }

    private LessonFeedbackDto lessonFeedbackBuilder(LessonFeedback lessonFeedback) {

        int[] rates = new int[6];
        boolean hasTextAnswer = false;

        Map<Long, LessonFeedbackQuestionDto> lessonFeedbackQuestionMap = new HashMap<>();

        List<FeedbackAnswer> feedbackAnswerList = lessonFeedback.getSelectedAnswers();

        for (FeedbackAnswer feedbackAnswer : feedbackAnswerList) {

            Question question = feedbackAnswer.getQuestion();
            Long questionId = question.getId();
            List<Answer> answers = question.getAnswers();
            LessonFeedbackQuestionDto lessonFeedbackQuestion = new LessonFeedbackQuestionDto();

            if (lessonFeedbackQuestionMap.containsKey(questionId)) {
                lessonFeedbackQuestion = lessonFeedbackQuestionMap.get(questionId);
                List<LessonFeedbackAnswerDto> lessonFeedbackAnswerList = lessonFeedbackQuestion.getAnswerList();

                for (LessonFeedbackAnswerDto lessonFeedbackAnswer : lessonFeedbackAnswerList) {
                    if (feedbackAnswer.getAnswer() != null && feedbackAnswer.getAnswer().getId() != null) {
                        if (lessonFeedbackAnswer.getAnswerText().equals(feedbackAnswer.getAnswer().getAnswerText())) {
                            lessonFeedbackAnswer.setChecked(true);
                            rates[feedbackAnswer.getAnswer().getRate()]++;
                        }
                    } else {
                        lessonFeedbackQuestion.setTextField(feedbackAnswer.getAnswerText());
                        hasTextAnswer = true;
                    }
                }

                lessonFeedbackQuestion.setAnswerList(lessonFeedbackAnswerList);

            } else {
                lessonFeedbackQuestion.setQuestionText(question.getQuestionText());
                List<LessonFeedbackAnswerDto> lessonFeedbackAnswerList = new ArrayList<>();

                for (Answer answer : answers) {

                    LessonFeedbackAnswerDto lessonFeedbackAnswer = LessonFeedbackAnswerDto.builder()
                            .answerText(answer.getAnswerText())
                            .build();

                    if (feedbackAnswer.getAnswer() != null && feedbackAnswer.getAnswer().getId() != null) {
                        if (Objects.equals(answer.getId(), feedbackAnswer.getAnswer().getId())) {
                            lessonFeedbackAnswer.setChecked(true);
                            rates[answer.getRate()]++;
                        }
                    } else {
                        lessonFeedbackQuestion.setTextField(feedbackAnswer.getAnswerText());
                        hasTextAnswer = true;
                    }

                    lessonFeedbackAnswerList.add(lessonFeedbackAnswer);
                }
                lessonFeedbackQuestion.setAnswerList(lessonFeedbackAnswerList);
                lessonFeedbackQuestionMap.put(questionId, lessonFeedbackQuestion);
            }
        }

        List<LessonFeedbackQuestionDto> lessonFeedbackQuestionList = new ArrayList<>(lessonFeedbackQuestionMap.values());

        UserDto student = UserDto.from(lessonFeedback.getStudent());
        UserDto teacher = UserDto.from(lessonFeedback.getTeacher());
        LessonDto lesson = LessonDto.from(lessonFeedback.getLesson());

        return LessonFeedbackDto.builder()
                .student(student)
                .teacher(teacher)
                .lesson(lesson)
                .questionList(lessonFeedbackQuestionList)
                .timestamp(lessonFeedback.getTimestamp())
                .rates(rates)
                .hasTextAnswer(hasTextAnswer)
                .build();
    }

    public List<LessonFeedbackDto> getAllLessonFeedbacks() {
        return lessonFeedbackRepository.findAll().stream()
                .map(this::lessonFeedbackBuilder)
                .collect(Collectors.toList());
    }

    public List<LessonFeedbackDto> getMyLessonFeedbacks(User currentUser) {

        List<LessonFeedback> lessonFeedbacks = lessonFeedbackRepository.findAllByTeacher(currentUser);

        return lessonFeedbacks.stream()
                .map(this::lessonFeedbackBuilder)
                .collect(Collectors.toList());
    }

    public List<LessonFeedbackDto> getFilteredLessonFeedbacks(Long teacherId,
                                                              Long studentId,
                                                              Long cohortId,
                                                              String lessonModuleDto,
                                                              String lessonTypeDto,
                                                              Integer lessonNr,
                                                              LocalDateTime startDate,
                                                              LocalDateTime endDate) {

        LessonModul lessonModule = lessonModuleDto == null ? null : LessonModul.getByName(lessonModuleDto);
        LessonType lessonType = lessonTypeDto == null ? null : LessonType.getByName(lessonTypeDto);

        List<LessonFeedback> LessonFeadbackList = lessonFeedbackRepository.findAllByParams(teacherId, studentId, cohortId,
                lessonModule, lessonType, lessonNr, startDate, endDate);

        return LessonFeadbackList.stream()
                .map(this::lessonFeedbackBuilder)
                .collect(Collectors.toList());
    }
}
