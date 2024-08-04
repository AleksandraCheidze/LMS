package de.aittr.lmsbe.zoom.utils;

import de.aittr.lmsbe.zoom.model.LessonTopicObject;
import de.aittr.lmsbe.zoom.model.json.ZoomObjectData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.List;

import static de.aittr.lmsbe.utils.AppUtils.localDateTimeToFilePathSafeString;
import static de.aittr.lmsbe.utils.AppUtils.parseZoomDateTime;

public class S3FileNameUtils {

    private S3FileNameUtils() {
        throw new IllegalArgumentException("This is Utility class");
    }

    public static String createFileNamePrefixForS3StorageValidTopic(LessonTopicObject lessonTopicObject, String startTime) {
        String result = Strings.join(Arrays.asList(
                localDateTimeToFilePathSafeString(parseZoomDateTime(startTime)),
                lessonTopicObject.getTopic() == null ? "" : replaceWith(lessonTopicObject.getTopic())), '_');
        return StringUtils.stripEnd(result, "_");
    }

    public static String createFileNamePrefixForS3StorageInvalidTopic(String startTime,
                                                                      String hostEmail,
                                                                      Long zoomMeetingId,
                                                                      String zoomMeetingTopic) {
        // to_process_manually/20231120/host.email@example.com/
        String directoryPrefix = createDirectoryPrefixForInvalidTopic(startTime, hostEmail);
        String fileNameOnS3 = Strings.join(Arrays.asList(
                localDateTimeToFilePathSafeString(parseZoomDateTime(startTime)), // 20231120T162630
                zoomMeetingId,                  // 20231120T162630_123456789
                replaceWith(zoomMeetingTopic)   // 20231120T162630_1234_topic
        ), '_');

        // to_process_manually/20231120/host.email@example.com/20231120T162630_123456789_topic
        return StringUtils.stripEnd(directoryPrefix + fileNameOnS3, "_");
    }

    private static String replaceWith(String source) {
        return source.replaceAll("[^a-zA-Z0-9._*'()!\\-]", "_");
    }

    public static String createDirectoryPrefixForInvalidTopic(String startTime, String hostEmail) {
        return "to_process_manually"    // to_process_manually
                + "/"   // to_process_manually/
                + parseZoomDateTime(startTime).toLocalDate().toString() // to_process_manually/20231120
                + "/"   // to_process_manually/20231120/
                + hostEmail.toLowerCase()   // to_process_manually/20231120/host.email@example.com
                + "/";  // to_process_manually/20231120/host.email@example.com/
    }

    public static String createDirectoryPrefixForValidTopic(String cohort,
                                                            String module,
                                                            String lessonType,
                                                            String lesson,
                                                            String startTime) {
        String directoryPrefix = Strings.join(Arrays.asList(
                "cohort_" + cohort,                                 // /cohort_26
                module,                                             // /cohort_26/basic_programming
                lessonType                                          // /cohort_26/basic_programming/lecture
        ), '/') + "/";                                      // /cohort_26/basic_programming/lecture/
        if (Strings.isEmpty(lesson)) {
            directoryPrefix += parseZoomDateTime(startTime).toLocalDate().toString();
        } else {
            directoryPrefix += lesson;
        }
        return directoryPrefix + "/";
    }


    public static String getFirstCohortFileName(List<String> firstCohortVideoFileNames,
                                                int part) {
        return firstCohortVideoFileNames.get(part);
    }

    public static String getDirectoryPrefixForValidTopic(LessonTopicObject lessonTopicObject,
                                                         ZoomObjectData zoomObjectData,
                                                         int cohort) {
        return createDirectoryPrefixForValidTopic(
                lessonTopicObject.getCohort().get(cohort),
                lessonTopicObject.getModule(),
                lessonTopicObject.getType(),
                lessonTopicObject.getLesson(),
                zoomObjectData.getStartTime()
        );
    }
}
