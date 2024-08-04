package de.aittr.lmsbe.feedback.controller;

import de.aittr.lmsbe.feedback.controller.api.LessonFeedbackApi;
import de.aittr.lmsbe.feedback.dto.LessonFeedbackDto;
import de.aittr.lmsbe.feedback.dto.NewLessonFeedbackDto;
import de.aittr.lmsbe.feedback.service.LessonFeedbackService;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class LessonFeedbackController implements LessonFeedbackApi {

    LessonFeedbackService lessonFeedbackService;

    @Override
    public LessonFeedbackDto addLessonFeedback(NewLessonFeedbackDto NewLessonFeedbackDto, Long cohortId, String lessonModule,
                                               String lessonType, Integer lessonNr, AuthenticatedUser currentUser) {
        return lessonFeedbackService.addLessonFeedback(NewLessonFeedbackDto, lessonModule, lessonType, lessonNr, cohortId, currentUser.getUser());
    }

    @Override
    public List<LessonFeedbackDto> getAllLessonFeedbacks() {
        return lessonFeedbackService.getAllLessonFeedbacks();
    }

    @Override
    public List<LessonFeedbackDto> getMyLessonFeedbacks(AuthenticatedUser currentUser) {
        return lessonFeedbackService.getMyLessonFeedbacks(currentUser.getUser());
    }

    @Override
    public List<LessonFeedbackDto> getFilteredLessonFeedbacks(Long teacherId, Long studentId, Long cohortId,
                                                              String lessonModule, String lessonType, Integer lessonNr,
                                                              LocalDateTime startDate, LocalDateTime endDate) {
        return lessonFeedbackService.getFilteredLessonFeedbacks(teacherId, studentId, cohortId, lessonModule, lessonType,
                lessonNr, startDate, endDate);
    }
}
