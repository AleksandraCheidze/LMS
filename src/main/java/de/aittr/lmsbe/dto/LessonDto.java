package de.aittr.lmsbe.dto;

import de.aittr.lmsbe.dto.cohort.CohortDto;
import de.aittr.lmsbe.model.Lesson;
import de.aittr.lmsbe.model.LessonModul;
import de.aittr.lmsbe.model.LessonType;
import de.aittr.lmsbe.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Lesson}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonDto implements Serializable {

    private Long id;
    private CohortDto cohortDto;
    private LessonModul lessonModul;
    private LessonType lessonType;
    private Integer lessonNr;
    private String lessonTopic;
    private TeacherDto teacher;
    private LocalDateTime lessonTime;

    private List<String> videos;


    public static LessonDto from(final Lesson lesson, final List<String> videos) {
        final LessonDto from = from(lesson);
        from.videos.addAll(videos);
        return from;
    }

    public static LessonDto from(final Lesson lesson) {
        User teacher = lesson.getTeacher();
        TeacherDto teacherDto = teacher != null ? TeacherDto.from(teacher) : null;
        return new LessonDto(lesson.getId(),
                CohortDto.from(lesson.getCohort()),
                lesson.getLessonModul(),
                lesson.getLessonType(),
                lesson.getLessonNr(),
                lesson.getLessonTopic(),
                teacherDto,
                lesson.getLessonTime(),
                new ArrayList<>());
    }
}



