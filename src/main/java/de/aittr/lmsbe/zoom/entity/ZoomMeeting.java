package de.aittr.lmsbe.zoom.entity;

import de.aittr.lmsbe.model.Lesson;
import de.aittr.lmsbe.model.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Andrej Reutow
 * created on 17.11.2023
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "zoom_meeting", uniqueConstraints = {
        @UniqueConstraint(name = "unique_combination_uuid_meeting_Id", columnNames = {"uuid", "meeting_Id"})
})
public class ZoomMeeting {

    /**
     * The unique identifier for the Zoom meeting.
     * This is a unique identifier used to identify a specific meeting within the Zoom system.
     * <br>
     * Уникальный идентификатор встречи Zoom.
     * Это уникальный идентификатор, который используется для идентификации конкретной встречи в системе Zoom.
     */
    @Id
    @NotNull
    @Column(name = "uuid", nullable = false)
    private String uuid;

    //TODO add @Enumerated(EnumType.STRING)
    @NotNull
    private MeetingType meetingType;

    /**
     * Meeting ID.
     * This is a unique identifier assigned to a meeting when it is created. It is used by participants to join the meeting.
     * <br>
     * ID встречи.
     * Это уникальный идентификатор, который присваивается встрече при её создании. Он используется участниками для присоединения к встрече.
     */
    @NotNull
    @Column(name = "meeting_Id", nullable = false)
    private String meetingId;

    private String hostId;

    @Enumerated(EnumType.STRING)
    private ZoomMeetingType zoomMeetingType;
    private LocalDateTime meetingTime;
    private Integer duration;

    /**
     * The topic of the meeting.
     * This is a brief description or title of your meeting or webinar, e.g., "Weekly Sales Meeting" or "Product Introduction Webinar".
     * <br>
     * * Тема встречи.
     * * Это краткое описание или название вашего собрания или вебинара, например, "Еженедельное собрание по продажам" или "Вебинар по введению в продукт".
     */
    private String topic;

    /**
     * The agenda of the meeting.
     * This is a more detailed plan or list of topics to be discussed during the meeting or webinar.
     * It could include specific issues, plans, or key points you want to discuss, e.g., "Discussion of next quarter's goals", "Updates from each department", etc.
     * <br>
     * * Повестка дня встречи.
     * * Это более подробный план или список тем, которые будут обсуждаться во время собрания или вебинара.
     * * Это может включать конкретные вопросы, планы или ключевые моменты, которые вы хотите обсудить, например, "Обсуждение целей на следующий квартал", "Обновления от каждого отдела" и т.д.
     */
    private String agenda;

    @Column(length = 1024)
    private String googleAuthorizationUrl;

    @Column(length = 1024)
    private String hostUrl;
    @Column(length = 1024)
    private String inviteUrl;
    private String meetingPassword;

    @NotNull
    @Column(nullable = false)
    private String hostEmail;

    @OneToMany(mappedBy = "zoomMeeting")
    private final List<ProcessedZoomVideo> videos = new ArrayList<>();

    @OneToMany(mappedBy = "zoomMeeting")
    private final List<Lesson> lessons = new ArrayList<>();

    @ManyToOne
    private User user;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ZoomMeeting that = (ZoomMeeting) object;

        if (!Objects.equals(uuid, that.uuid)) return false;
        if (meetingType != that.meetingType) return false;
        if (!Objects.equals(meetingId, that.meetingId)) return false;
        if (!Objects.equals(hostId, that.hostId)) return false;
        if (zoomMeetingType != that.zoomMeetingType) return false;
        if (!Objects.equals(meetingTime, that.meetingTime)) return false;
        if (!Objects.equals(duration, that.duration)) return false;
        if (!Objects.equals(topic, that.topic)) return false;
        if (!Objects.equals(agenda, that.agenda)) return false;
        if (!Objects.equals(hostUrl, that.hostUrl)) return false;
        if (!Objects.equals(inviteUrl, that.inviteUrl)) return false;
        if (!Objects.equals(meetingPassword, that.meetingPassword))
            return false;
        if (!Objects.equals(hostEmail, that.hostEmail)) return false;
        if (!Objects.equals(videos, that.videos)) return false;
        return Objects.equals(lessons, that.lessons);
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (meetingType != null ? meetingType.hashCode() : 0);
        result = 31 * result + (meetingId != null ? meetingId.hashCode() : 0);
        result = 31 * result + (hostId != null ? hostId.hashCode() : 0);
        result = 31 * result + (zoomMeetingType != null ? zoomMeetingType.hashCode() : 0);
        result = 31 * result + (meetingTime != null ? meetingTime.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (agenda != null ? agenda.hashCode() : 0);
        result = 31 * result + (hostUrl != null ? hostUrl.hashCode() : 0);
        result = 31 * result + (inviteUrl != null ? inviteUrl.hashCode() : 0);
        result = 31 * result + (meetingPassword != null ? meetingPassword.hashCode() : 0);
        result = 31 * result + (hostEmail != null ? hostEmail.hashCode() : 0);
        result = 31 * result + (videos != null ? videos.hashCode() : 0);
        result = 31 * result + (lessons != null ? lessons.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ZoomMeeting{" +
                "uuid='" + uuid + '\'' +
                ", meetingType=" + meetingType +
                ", meetingId='" + meetingId + '\'' +
                ", hostId='" + hostId + '\'' +
                ", zoomMeetingType=" + zoomMeetingType +
                ", meetingTime=" + meetingTime +
                ", duration=" + duration +
                ", topic='" + topic + '\'' +
                ", agenda='" + agenda + '\'' +
                ", hostUrl='" + hostUrl + '\'' +
                ", inviteUrl='" + inviteUrl + '\'' +
                ", meetingPassword='" + meetingPassword + '\'' +
                ", hostEmail='" + hostEmail + '\'' +
                ", videos=" + videos +
                ", lesson=" + lessons +
                '}';
    }
}
