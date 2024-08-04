package de.aittr.lmsbe.zoom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class LessonTopicObject {
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> cohort;
    private String module;
    private String type;
    private String lesson;
    private String topic;
}
