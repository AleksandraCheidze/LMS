package de.aittr.lmsbe.zoom.model.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * A class representing information about a recording file.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RecordingFile {
    /**
     * Unique identifier of the recording file.
     */
    private String id;

    /**
     * Identifier of the meeting associated with the recording.
     */
    private String meetingId;

    /**
     * Date and time when the recording started.
     */
    private String recordingStart;

    /**
     * Date and time when the recording ended.
     */
    private String recordingEnd;

    /**
     * Type of the recording file.
     * Allowed values: MP4, M4A, CHAT, TRANSCRIPT, CSV, TB, CC, CHAT_MESSAGE, SUMMARY, TIMELINE.
     */
    private String fileType;

    /**
     * File extension type of the recording file.
     * Allowed values: MP4, M4A, TXT, VTT, CSV, JSON, JPG.
     */
    private String fileExtension;

    /**
     * Size of the recording file in bytes.
     */
    private Long fileSize;

    /**
     * URL where the recording file can be played.
     */
    private String playUrl;

    /**
     * URL to download the recording file.
     * Example: https://{{base-domain}}/rec/webhook_download/download/xxx
     * Header 'Authorization: Bearer {{your-download_token-value}}
     */
    private String downloadUrl;

    /**
     * File path to the on-premise account recording.
     * Note: This field is returned for Zoom On-Premise accounts and not for download_url.
     */
    private String filePath;

    /**
     * Processing status of the recording file.
     * Allowed values: completed, processing.
     */
    private String status;

    /**
     * Type of the recording file.
     * Allowed values: shared_screen_with_speaker_view(CC), shared_screen_with_speaker_view, shared_screen_with_gallery_view,
     * gallery_view, shared_screen, audio_only, audio_transcript, chat_file, active_speaker, host_video, audio_only_each_participant,
     * cc_transcript, closed_caption, poll, timeline, thumbnail, audio_interpretation, summary, summary_next_steps, summary_smart_chapters,
     * sign_interpretation, production_studio.
     */
    private String recordingType;
}

