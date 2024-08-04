package de.aittr.lmsbe.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static de.aittr.lmsbe.service.utils.TestUtils.readFileToString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class AppUtilsTest {

    @SneakyThrows
    @Test
    void groupFilePaths() {
        var filesList = List.of(
                "cohort_25/basic_programming/lecture/25/some_file_name.mp4",
                "cohort_25/basic_programming/lecture/26/some_file_name.mp4",
                "cohort_25/basic_programming/lecture/27/some_file_name.mp4",
                "cohort_25/basic_programming/consultation/2023-05-06/some_file_name.mp4",
                "cohort_25/basic_programming/consultation/2023-05-07/some_file_name_part0.mp4",
                "cohort_25/basic_programming/consultation/2023-05-07/some_file_name_part1.mp4",
                "cohort_25/front_end/lecture/2023-05-07/some_file_name_part1.mp4",
                "cohort_25/front_end/consultation/2023-05-07/some_file_name_part1.mp4"
        );

        var groupedFiles = AppUtils.groupFilePaths(filesList);

        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(groupedFiles);
        String expected = readFileToString("json/video_list.json");
        assertEquals(expected, result);
    }

    @Test
    void parseZoomDateTime() {
        LocalDateTime expectedUtc = LocalDateTime.of(2023, 6, 30, 15, 22, 11);
        LocalDateTime expectedLocal = expectedUtc.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime actual = AppUtils.parseZoomDateTime("2023-06-30T15:22:11Z");
        assertEquals(expectedLocal, actual);
    }

    @Test
    void localDateTimeToFilePathSafeString() {
        LocalDateTime localDateTime = AppUtils.parseZoomDateTime("2023-06-30T15:22:11Z");
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.of("UTC"));
        assertEquals("20230630T152211", formatter.format(zonedDateTime));
    }
}

