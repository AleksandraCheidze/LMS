package de.aittr.lmsbe.zoom.dto;

import de.aittr.lmsbe.dto.LessonDto;
import de.aittr.lmsbe.model.Lesson;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import de.aittr.lmsbe.zoom.mapper.ZoomMeetingMapper;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Schema(description = "Data Transfer Object for Meeting Information")
@Slf4j
public class ZoomMeetingInfoDto {

    @Schema(description = "List of lesson data", implementation = List.class)
    @ArraySchema(schema = @Schema(implementation = LessonDto.class))
    ZoomMeetingDto meeting;

    @Schema(description = "Zoom Meeting Data", implementation = ZoomMeetingDto.class)
    List<LessonDto> lesson;

    public static ZoomMeetingInfoDto from(ZoomMeeting savedMeeting, List<Lesson> lessonData) {
        log.debug("Mapping meeting '{}' with lessons '{}' to ZoomParamsDtoResponse", savedMeeting, lessonData);

        List<LessonDto> convertedLessons = lessonData.stream()
                .map(LessonDto::from)
                .collect(Collectors.toList());
        ZoomMeetingDto convertedMeeting = ZoomMeetingMapper.toDto(savedMeeting);

        ZoomMeetingInfoDto convertedDto = new ZoomMeetingInfoDto(convertedMeeting, convertedLessons);
        log.debug("Mapping meeting to ZoomParamsDtoResponse, result '{}'", convertedDto);
        return convertedDto;
    }
}
