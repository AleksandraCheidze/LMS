package de.aittr.lmsbe.zoom.mapper;

import de.aittr.lmsbe.dto.UserDto;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.utils.AppUtils;
import de.aittr.lmsbe.zoom.dto.ZoomMeetingDto;
import de.aittr.lmsbe.zoom.entity.MeetingType;
import de.aittr.lmsbe.zoom.entity.ProcessedZoomVideo;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import de.aittr.lmsbe.zoom.entity.ZoomMeetingType;
import de.aittr.lmsbe.zoom.meeting.ZoomMeetingResponse;
import de.aittr.lmsbe.zoom.model.json.ZoomObjectData;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Andrej Reutow
 * created on 18.11.2023
 */
@Slf4j
public class ZoomMeetingMapper {

    private ZoomMeetingMapper() {
        throw new IllegalArgumentException("This is mapper class");
    }

    public static ZoomMeeting mapTo(final ZoomMeetingResponse dto, final MeetingType meetingType, User user) {
        if (dto == null || meetingType == null) {
            return null;
        }

        final LocalDateTime zoomMeetingStartTime = AppUtils.parseZoomDateTime(dto.getStartTime());
        final ZoomMeetingType zoomMeetingType = ZoomMeetingType.getByCode(dto.getType());

        return ZoomMeeting.builder()
                .uuid(dto.getUuid())
                .meetingType(meetingType)
                .meetingId(String.valueOf(dto.getMeetingId()))
                .hostId(dto.getHostId())
                .zoomMeetingType(zoomMeetingType)
                .meetingTime(zoomMeetingStartTime)
                .duration(dto.getDuration())
                .topic(dto.getTopic())
                .agenda(dto.getAgenda())
                .hostUrl(dto.getHostURL())
                .inviteUrl(dto.getInviteURL())
                .hostEmail(dto.getHostEmail())
                .googleAuthorizationUrl(dto.getGoogleAuthorizationUrl())
                .meetingPassword(dto.getPassword())
                .user(user)
                .build();
    }

    public static ZoomMeetingDto toDto(final ZoomMeeting zoomMeeting) {
        log.debug("Mapping meeting '{}' to ZoomMeetingDto", zoomMeeting);
        if (zoomMeeting == null) {
            return null;
        }
        User meetingUser = zoomMeeting.getUser();
        UserDto userDto = (meetingUser != null) ? UserDto.from(meetingUser) : null;


        ZoomMeetingDto mappedDto = new ZoomMeetingDto(
                zoomMeeting.getUuid(),
                zoomMeeting.getMeetingType(),
                zoomMeeting.getMeetingId(),
                zoomMeeting.getHostId(),
                zoomMeeting.getZoomMeetingType(),
                zoomMeeting.getMeetingTime(),
                zoomMeeting.getDuration(),
                zoomMeeting.getTopic(),
                zoomMeeting.getAgenda(),
                zoomMeeting.getHostUrl(),
                zoomMeeting.getInviteUrl(),
                zoomMeeting.getMeetingPassword(),
                zoomMeeting.getHostEmail(),
                zoomMeeting.getGoogleAuthorizationUrl(),
                userDto
        );

        log.debug("Mapping meeting to MeetingInfoDto, result '{}'", mappedDto);
        return mappedDto;
    }

    public static ZoomMeeting from(final ZoomObjectData zoomObjectData,
                                   final List<ProcessedZoomVideo> processedZoomVideos,
                                   final @NonNull MeetingType meetingType) {

        LocalDateTime zoomMeetingStartTime = AppUtils.parseZoomDateTime(zoomObjectData.getStartTime());
        ZoomMeetingType zoomMeetingType = ZoomMeetingType.getByCode(zoomObjectData.getType());

        ZoomMeeting zoomMeeting = new ZoomMeeting(
                zoomObjectData.getUuid(),
                meetingType,
                String.valueOf(zoomObjectData.getId()),
                zoomObjectData.getHostId(),
                zoomMeetingType,
                zoomMeetingStartTime,
                zoomObjectData.getDuration(),
                zoomObjectData.getTopic(),
                null,
                null,
                null,
                null,
                null,
                zoomObjectData.getHostEmail(),
                null
        );
        zoomMeeting.getVideos().addAll(processedZoomVideos);
        return zoomMeeting;
    }
}
