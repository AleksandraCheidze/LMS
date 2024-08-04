package de.aittr.lmsbe.mail.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class UserData {

    private String firstName;
    private String lastName;
    private String email;
    private String confirmationUUID;
    private ZonedDateTime expiryDate;
}
