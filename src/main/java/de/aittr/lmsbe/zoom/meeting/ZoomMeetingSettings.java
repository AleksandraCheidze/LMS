package de.aittr.lmsbe.zoom.meeting;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.aittr.lmsbe.zoom.entity.ZoomMeetingType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ZoomMeetingSettings {
    @NotNull
    @NotBlank
    @Schema(description = "Meetings topic", example = "Java lesson #7")
    private String topic;

    @Schema(hidden = true)
    @JsonProperty("default_password")
    private boolean defaultPassword = false;

    @Schema(hidden = true)
    @JsonProperty("start_time")
    private LocalDateTime startTime;


    @Schema(description = "Meeting start date", example = "2024-03-31")
    private LocalDate dateToStart;

    @Schema(description = "Meeting start time", example = "18:00")
    private LocalTime timeToStart;

    @NotNull
    @Min(value = 5)
    @JsonProperty("duration")
    @Schema(description = "The meeting's scheduled duration, in minutes. This field is only used for scheduled meetings (2).", example = "300")
    private Integer duration;

    @JsonProperty("password")
    @Schema(hidden = true, description = "The passcode required to join the meeting. By default, a passcode can only have a maximum length of 10 characters and only contain alphanumeric characters and the @, -, _, and * characters.", example = "12354")
    private String password;

//    @NotNull
//    @Schema(description = "Whether to start meetings with the host video on.", example = "false")
//    private boolean host_video;

    @JsonProperty("mute_upon_entry")
    @Schema(hidden = true, description = "Whether to mute participants upon entry.Default: false", example = "true")
    private boolean muteUponEntry = true;

    @JsonProperty("participant_video")
    @Schema(hidden = true, description = "Whether to start meetings with the participant video on", example = "false")
    private boolean participantVideo = false;

    @JsonProperty("waiting_room")
    @Schema(hidden = true, description = "Whether to enable the Waiting Room feature.", example = "false")
    private boolean waitingRoom = false;

    @JsonProperty("host_save_video_order")
    @Schema(hidden = true, description = "Whether the Allow host to save video order feature is enabled.", example = "true")
    private boolean hostSaveVideoOrder = true;

    @JsonProperty("timezone")
    @Schema(hidden = true, description = "Timezone to format start_time.", example = "Europe/Berlin")
    private String timeZone = "Europe/Berlin";

    @JsonProperty("type")
    @Schema(hidden = true, description = "The type of meeting 1 - An instant meeting. 2 - A scheduled meeting. 3 - A recurring meeting with no fixed time. 8 - A recurring meeting with fixed time.", example = "2")
    private Integer meetingType = ZoomMeetingType.SCHEDULED.getKey();

    @Schema(hidden = true)
    @JsonProperty("agenda")
    private String agenda;
}
