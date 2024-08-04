package de.aittr.lmsbe.zoom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.aittr.lmsbe.zoom.model.LessonTopicObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class JsObjectService {

    private final JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    private final ObjectMapper objectMapper;

    public String jsObjectToJSON(String jsObject) throws ParseException {
        log.debug("Parse and Converting js-topic(string) '{}' to jsonObject...", jsObject);
        JSONObject jsonObject = (JSONObject) parser.parse(jsObject);
        log.debug("Parsed js-topic (string) '{}' as jsonObject: '{}'", jsObject, jsonObject);
        return jsonObject.toJSONString(JSONStyle.NO_COMPRESS);
    }

    public LessonTopicObject topicToLessonTopicObject(String topic) {
        try {
            String topicJson = jsObjectToJSON("{" + topic + "}");
            log.info("Converted topic '{}' as JSON: '{}'", topic, topicJson);

            LessonTopicObject result = objectMapper.readValue(topicJson, LessonTopicObject.class);

            if (result.getCohort() == null) {
                log.warn("Cohort list is null, set default cohort 00");
                result.setCohort(Collections.singletonList("00"));
            }

            if (Strings.isBlank(result.getTopic())) {
                log.warn("Topic is blank, set default topic");
                result.setCohort(Collections.singletonList("default topic"));
            }

            log.info("Successfully converted topic to LessonTopicObject: '{}'", result);
            return result;
        } catch (JsonProcessingException e) {
            log.error("Error converting topic to LessonTopicObject, JsonProcessingException: '{}'", e.getMessage());
            return null;
        } catch (ParseException e) {
            log.error("Error converting topic to LessonTopicObject, ParseException: '{}'", e.getMessage());
            return null;
        }
    }
}
