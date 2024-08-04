package de.aittr.lmsbe.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GHJsonMeta {

    private String topic;

    private String date;

    private String plan;

    private String theory;

    private String homework;

    private String video;

    private List<String> code;
}
