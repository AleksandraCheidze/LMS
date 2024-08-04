package de.aittr.lmsbe.zoom.dto;

import de.aittr.lmsbe.dto.UserDto;
import de.aittr.lmsbe.zoom.entity.MeetingType;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import de.aittr.lmsbe.zoom.entity.ZoomMeetingType;
import de.aittr.lmsbe.zoom.mapper.ZoomMeetingMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ZoomMeeting}
 */
@Value
@Schema(description = "Data Transfer Object for Zoom Meeting")
public class ZoomMeetingDto implements Serializable {

    /**
     * The unique identifier for the Zoom meeting.
     * This is a unique identifier used to identify a specific meeting within the Zoom system.
     * <br>
     * Уникальный идентификатор встречи Zoom.
     * Это уникальный идентификатор, который используется для идентификации конкретной встречи в системе Zoom.
     */
    @Schema(description = "Unique ID for the Zoom meeting", example = "123e4567-e89b-12d3-a456")
    String uuid;

    @Schema(description = "Type of the meeting", example = "INTERN or EXTERN", implementation = MeetingType.class)
    MeetingType meetingType;

    /**
     * Meeting ID.
     * This is a unique identifier assigned to a meeting when it is created. It is used by participants to join the meeting.
     * <br>
     * ID встречи.
     * Это уникальный идентификатор, который присваивается встрече при её создании. Он используется участниками для присоединения к встрече.
     */
    @Schema(description = "Meeting ID", example = "123")
    String meetingId;

    @Schema(description = "ID of the host user", example = "123e4567-e89b-12d3-a456")
    String hostId;

    @Schema(description = "Zoom meeting type", example = "INSTANT\n" +
            "SCHEDULED\n" +
            "RECURRING_NO_FIXED_TIME\n" +
            "PERONAL\n" +
            "WEBINAR\n" +
            "WEBINAR_NO_FIXED_TIME\n" +
            "PAC\n" +
            "RECURRING_FIXED_TIME\n" +
            "WEBINAR_FIXED_TIME",
            implementation = ZoomMeetingType.class)
    ZoomMeetingType zoomMeetingType;

    @Schema(description = "Date and time of the meeting", example = "2023-03-15T13:47:20")
    LocalDateTime meetingTime;

    @Schema(description = "Duration of the meeting in minutes", example = "60")
    Integer duration;

    /**
     * The topic of the meeting.
     * This is a brief description or title of your meeting or webinar, e.g., "Weekly Sales Meeting" or "Product Introduction Webinar".
     * <br>
     * * Тема встречи.
     * * Это краткое описание или название вашего собрания или вебинара, например, "Еженедельное собрание по продажам" или "Вебинар по введению в продукт".
     */
    @Schema(description = "Topic of the meeting", example = "Project discussion")
    String topic;

    /**
     * The agenda of the meeting.
     * This is a more detailed plan or list of topics to be discussed during the meeting or webinar.
     * It could include specific issues, plans, or key points you want to discuss, e.g., "Discussion of next quarter's goals", "Updates from each department", etc.
     * <br>
     * * Повестка дня встречи.
     * * Это более подробный план или список тем, которые будут обсуждаться во время собрания или вебинара.
     * * Это может включать конкретные вопросы, планы или ключевые моменты, которые вы хотите обсудить, например, "Обсуждение целей на следующий квартал", "Обновления от каждого отдела" и т.д.
     */
    @Schema(description = "The agenda of the meeting", example = "Discuss project milestones")
    String agenda;

    @Schema(description = "URL for the host to start the meeting", example = "https://us02web.zoom.us/j/meeting_id")
    String hostUrl;

    @Schema(description = "URL for invitees to join the meeting", example = "https://us02web.zoom.us/j/meeting_id")
    String inviteUrl;

    @Schema(description = "Password for the meeting", example = "password")
    String meetingPassword;

    @Schema(description = "Email of the host user", example = "host@example.com")
    String hostEmail;

    @Schema(description = "Link to the meeting in Google Calendar", example = "https://calendar.google.com/calendar/event?evt=meeting_id")
    String googleCalendarLink;

    @Schema(description = "User data", implementation = UserDto.class)
    UserDto user;

    public static ZoomMeetingDto from(ZoomMeeting zoomMeeting) {
        return ZoomMeetingMapper.toDto(zoomMeeting);
    }
}
