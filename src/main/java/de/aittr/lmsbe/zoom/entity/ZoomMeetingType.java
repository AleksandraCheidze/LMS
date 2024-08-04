package de.aittr.lmsbe.zoom.entity;

import lombok.Getter;

/**
 * @author Andrej Reutow
 * created on 17.11.2023
 */
@Getter
public enum ZoomMeetingType {
    INSTANT(1, "Instant Meeting"),
    SCHEDULED(2, "Scheduled Meeting"),
    RECURRING_NO_FIXED_TIME(3, "Recurring Meeting with No Fixed Time"),
    PERONAL(4, "A meeting created via PMI (Personal Meeting ID)."),

    WEBINAR(5, "A webinar"),
    WEBINAR_NO_FIXED_TIME(6, " A recurring webinar without a fixed time"),

    PAC(7, "A Personal Audio Conference (PAC)"),
    RECURRING_FIXED_TIME(8, "Recurring Meeting with Fixed Time"),
    WEBINAR_FIXED_TIME(9, "A recurring webinar with a fixed time");

    private final int key;
    private final String description;

    ZoomMeetingType(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public static ZoomMeetingType getByCode(Integer code) {
        if (code != null && code >= 0) {
            for (ZoomMeetingType type : values()) {
                if (type.getKey() == code) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException("Invalid meeting type code: " + code);
    }
}

