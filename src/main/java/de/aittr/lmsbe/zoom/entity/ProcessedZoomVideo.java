package de.aittr.lmsbe.zoom.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * @author Andrej Reutow
 * created on 15.11.2023
 */
@Entity
@Getter
@Setter
public class ProcessedZoomVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(nullable = false)
    private String uuid;

    @Column(length = 2, nullable = false)
    private Integer part;

    @NotNull
    @Column(nullable = false)
    private String videoId;

    @NotNull
    @Column(nullable = false)
    private String recordingStart;
    @NotNull
    @Column(nullable = false)
    private String recordingEnd;

    @NotNull
    @Column(nullable = false)
    private String shareUrl;

    @Column(nullable = false)
    private String recordingPassword;
    @NotNull
    @Column(nullable = false)
    private String hostEmail;

    private String s3Bucket;
    private String s3Path;

    private boolean isTopicValid;

    @CreationTimestamp
    private Instant createdOn;

    @ManyToOne
    @JoinColumn(name = "zoom_meeting_id")
    private ZoomMeeting zoomMeeting;

    public ProcessedZoomVideo(String uuid,
                              Integer part,
                              String videoId,
                              String recordingStart,
                              String recordingEnd,
                              String shareUrl,
                              String recordingPassword,
                              String hostEmail,
                              String s3Bucket,
                              String s3Path,
                              boolean isTopicValid) {
        this.uuid = uuid;
        this.part = part;
        this.videoId = videoId;
        this.recordingStart = recordingStart;
        this.recordingEnd = recordingEnd;
        this.shareUrl = shareUrl;
        this.recordingPassword = recordingPassword;
        this.hostEmail = hostEmail;
        this.s3Bucket = s3Bucket;
        this.s3Path = s3Path;
        this.isTopicValid = isTopicValid;
    }

    public ProcessedZoomVideo() {
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ProcessedZoomVideo that = (ProcessedZoomVideo) object;

        if (isTopicValid != that.isTopicValid) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(uuid, that.uuid)) return false;
        if (!Objects.equals(part, that.part)) return false;
        if (!Objects.equals(videoId, that.videoId)) return false;
        if (!Objects.equals(recordingStart, that.recordingStart))
            return false;
        if (!Objects.equals(recordingEnd, that.recordingEnd)) return false;
        if (!Objects.equals(shareUrl, that.shareUrl)) return false;
        if (!Objects.equals(recordingPassword, that.recordingPassword))
            return false;
        if (!Objects.equals(hostEmail, that.hostEmail)) return false;
        if (!Objects.equals(s3Bucket, that.s3Bucket)) return false;
        if (!Objects.equals(s3Path, that.s3Path)) return false;
        if (!Objects.equals(createdOn, that.createdOn)) return false;
        return Objects.equals(zoomMeeting, that.zoomMeeting);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (part != null ? part.hashCode() : 0);
        result = 31 * result + (videoId != null ? videoId.hashCode() : 0);
        result = 31 * result + (recordingStart != null ? recordingStart.hashCode() : 0);
        result = 31 * result + (recordingEnd != null ? recordingEnd.hashCode() : 0);
        result = 31 * result + (shareUrl != null ? shareUrl.hashCode() : 0);
        result = 31 * result + (recordingPassword != null ? recordingPassword.hashCode() : 0);
        result = 31 * result + (hostEmail != null ? hostEmail.hashCode() : 0);
        result = 31 * result + (s3Bucket != null ? s3Bucket.hashCode() : 0);
        result = 31 * result + (s3Path != null ? s3Path.hashCode() : 0);
        result = 31 * result + (isTopicValid ? 1 : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (zoomMeeting != null ? zoomMeeting.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProcessedZoomVideo{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", part=" + part +
                ", videoId='" + videoId + '\'' +
                ", recordingStart='" + recordingStart + '\'' +
                ", recordingEnd='" + recordingEnd + '\'' +
                ", shareUrl='" + shareUrl + '\'' +
                ", recordingPassword='" + recordingPassword + '\'' +
                ", hostEmail='" + hostEmail + '\'' +
                ", s3Bucket='" + s3Bucket + '\'' +
                ", s3Path='" + s3Path + '\'' +
                ", isTopicValid=" + isTopicValid +
                ", createdOn=" + createdOn +
                ", zoomMeetingId=" + (zoomMeeting == null ? "null" : zoomMeeting.getUuid()) +
                '}';
    }
}
