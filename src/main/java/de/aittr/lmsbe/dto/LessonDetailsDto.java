package de.aittr.lmsbe.dto;

import lombok.Value;

import java.util.List;

@Value
public class LessonDetailsDto {

    List<LessonDto> completedLessons;
    List<LessonDto> todayLessons;
    List<LessonDto> comingLessons;
}
