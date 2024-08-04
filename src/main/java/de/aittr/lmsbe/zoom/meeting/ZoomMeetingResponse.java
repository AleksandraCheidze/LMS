package de.aittr.lmsbe.zoom.meeting;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO fo response")
public class ZoomMeetingResponse {

    @JsonProperty("id")
    Long meetingId;
    @JsonProperty("created_at")
    String createdAt;
    @JsonProperty("duration")
    Integer duration;
    @JsonProperty("host_id")
    String hostId;
    @JsonProperty("host_email")
    String hostEmail;
    @JsonProperty("join_url")
    String inviteURL;
    @JsonProperty("start_time")
    String startTime;
    @JsonProperty("start_url")
    String hostURL;
    @JsonProperty("status")
    String status;
    @JsonProperty("timezone")
    String timezone;
    @JsonProperty("topic")
    String topic;
    @JsonProperty("type")
    Integer type;
    @JsonProperty("uuid")
    String uuid;
    @JsonProperty("agenda")
    private String agenda;
    @JsonProperty("password")
    private String password;
    @JsonProperty("personal_meeting_url")
    private String personalMeetingUrl;

    @JsonProperty("google_authorization_url")
    private String googleAuthorizationUrl;
}
