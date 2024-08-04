package de.aittr.lmsbe.zoom.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ZoomObjectData {
    /**
     * The universally unique identifier (UUID) of the recorded meeting or webinar instance.
     */
    private String uuid;
    /**
     * The ID of the recorded meeting (meetingId) or webinar (webinarId).
     */
    private Long id;
    /**
     * The account ID of the user who completed the meeting or webinar recording.
     */
    private String accountId;
    /**
     * The ID of the user set as the host of the meeting or webinar.
     */
    private String hostId;
    /**
     * The meeting or webinar topic.
     */
    private String topic;
    /**
     * The type of recorded meeting or webinar.
     * <p>
     * If the recording is of a meeting:
     * <p>
     * 1 — Instant meeting.
     * 2 — Scheduled meeting.
     * 3 — A recurring meeting with no fixed time.
     * 4 — A meeting created via PMI (Personal Meeting ID).
     * 7 — A Personal Audio Conference (PAC).
     * 8 - Recurring meeting with a fixed time.
     * If the recording is of a webinar:
     * <p>
     * 5 — A webinar.
     * 6 — A recurring webinar without a fixed time.
     * 9 — A recurring webinar with a fixed time.
     * If the recording is not from a meeting or webinar:
     * <p>
     * 99 — A recording uploaded via the Recordings interface on the Zoom Web Portal.Allowed: 1┃2┃3┃4┃5┃6┃7┃8┃9┃99
     */
    private Integer type;
    /**
     * The meeting's or webinar's start time.
     */
    private String startTime;
    /**
     * The meeting's or webinar's timezone.
     */
    private String timezone;
    /**
     * The host's email address.
     */
    private String hostEmail;
    /**
     * The recording's duration.
     */
    private Integer duration;
    /**
     * The recording file's total size, in bytes.
     */
    private Long totalSize;
    /**
     * The number of completed recording files.
     */
    private Long recordingCount;
    /**
     * The URL where approved users can view the recording.
     */
    private String shareUrl;
    /**
     *
     Information about the completed recording files.
     */
    @JsonProperty("recording_files")
    private List<RecordingFile> recordingFiles;
    /**
     * Whether the recording is an on-premise recording.
     */
    private Boolean onPrem;
    /**
     * The meeting's or webinar's password.
     */
    private String password;
    /**
     * The cloud recording's password to be used in the URL.
     * This recording's password can be directly spliced in play_url or share_url with ?pwd= to access and play,
     * such as in 'https://zoom.us/rec/share/**************?pwd=yNYIS408EJygs7rE5vVsJwXIz4-VW7MH'.
     * See Embedding meeting passcode in invite link for details.
     *
     * If you want to use this field, please contact Zoom support.
     */
    @JsonProperty("recording_play_passcode")
    private String recordingPlayPasscode;
}
