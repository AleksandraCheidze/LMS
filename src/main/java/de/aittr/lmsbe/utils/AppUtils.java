package de.aittr.lmsbe.utils;

import com.amazonaws.services.s3.model.ObjectMetadata;
import de.aittr.lmsbe.zoom.model.LessonTopicObject;
import de.aittr.lmsbe.zoom.model.json.RecordingFile;
import de.aittr.lmsbe.zoom.model.json.ZoomObjectData;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class AppUtils {

    private AppUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final DateTimeFormatter FILE_PATH_SAFE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");


    public static LocalDateTime parseZoomDateTime(String zoomDateTime) {
        ZonedDateTime dateTimeUtc = ZonedDateTime.parse(zoomDateTime);
        ZonedDateTime dateTimeInSystemTimeZone = dateTimeUtc.withZoneSameInstant(ZoneId.systemDefault());
        return dateTimeInSystemTimeZone.toLocalDateTime();
    }

    public static String localDateTimeToFilePathSafeString(LocalDateTime localDateTime) {
        return localDateTime.format(FILE_PATH_SAFE_DATE_TIME_FORMATTER);
    }

    public static Map<String, Map<String, Map<String, List<String>>>> groupFilePaths(List<String> files) {
        Map<String, Map<String, Map<String, List<String>>>> groupedFiles = new HashMap<>();
        for (String filePath : files) {
            String[] pathParts = filePath.split("/");
            if (pathParts.length >= 2) {
                String module = pathParts[1];
                String lessontype = pathParts[2];
                String lesson = pathParts[3];

                if (!groupedFiles.containsKey(module)) {
                    groupedFiles.put(module, new HashMap<>());
                }
                if (!groupedFiles.get(module).containsKey(lessontype)) {
                    groupedFiles.get(module).put(lessontype, new HashMap<>());
                }
                if (!groupedFiles.get(module).get(lessontype).containsKey(lesson)) {
                    groupedFiles.get(module).get(lessontype).put(lesson, new ArrayList<>());
                }
                groupedFiles.get(module).get(lessontype).get(lesson).add(filePath);
            }
        }
        return groupedFiles;
    }

    public static boolean isValidLessonTopicObject(LessonTopicObject lessonTopicObject) {
        return lessonTopicObject != null;
    }

    public static ObjectMetadata objectToS3ObjectMetadataForInvalidFile(ZoomObjectData zoomObjectData, RecordingFile recordingFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        Map<String, String> userMedata = Map.of(
                "host", zoomObjectData.getHostEmail(),
                "uuid", zoomObjectData.getUuid(),
                "topic", Base64.getEncoder().encodeToString(zoomObjectData.getTopic().getBytes()),
                "topicOrigin", zoomObjectData.getTopic(),
                "start_time", zoomObjectData.getStartTime(),
                "meeting_id", recordingFile.getMeetingId(),
                "meeting_file_id", recordingFile.getId()
        );
        objectMetadata.setUserMetadata(userMedata);
        return objectMetadata;
    }

    public static ObjectMetadata objectToS3ObjectMetadataForValidFile(ZoomObjectData zoomObjectData, LessonTopicObject lessonTopicObject) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        Map<String, String> userMedata = new HashMap<>();
        userMedata.put("host", zoomObjectData.getHostEmail());
        userMedata.put("uuid", zoomObjectData.getUuid());
        userMedata.put("topic", Base64.getEncoder().encodeToString(zoomObjectData.getTopic().getBytes()));
        userMedata.put("topicOrigin", zoomObjectData.getTopic());
        userMedata.put("start_time", zoomObjectData.getStartTime());

        if (null == lessonTopicObject) {
            objectMetadata.setUserMetadata(userMedata);
            return objectMetadata;
        }
        if (null != lessonTopicObject.getCohort()) {
            userMedata.put("cohorts", String.join(", ", lessonTopicObject.getCohort()));
        } else {
            userMedata.put("cohorts", Strings.EMPTY);
        }
        if (null != lessonTopicObject.getModule()) {
            userMedata.put("lesson_modul", lessonTopicObject.getModule());
        } else {
            userMedata.put("lesson_modul", Strings.EMPTY);
        }
        if (null != lessonTopicObject.getType()) {
            userMedata.put("lesson_type", lessonTopicObject.getType());
        } else {
            userMedata.put("lesson_type", Strings.EMPTY);
        }
        if (null != lessonTopicObject.getLesson()) {
            userMedata.put("lesson_nr", lessonTopicObject.getLesson());
        } else {
            userMedata.put("lesson_nr", Strings.EMPTY);
        }
        objectMetadata.setUserMetadata(userMedata);
        return objectMetadata;
    }
}
