package de.aittr.lmsbe.mail.service;

import de.aittr.lmsbe.exception.EmailSendingException;
import de.aittr.lmsbe.mail.dto.UserData;
import de.aittr.lmsbe.mail.sender.LmsMailSender;
import de.aittr.lmsbe.model.UserConfirmationCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 10/9/2023
 * lms-be
 *
 * @author Marsel Sidikov (AIT TR)
 */
@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailService {

    private final LmsMailSender lmsMailSender;


    @Value("${confirm-link-expired-days}")
    int confirmLinkExpiredDays;


    @Async
    public void sendRegistrationEmail(UserConfirmationCode userConfirmationCode) throws EmailSendingException {
        try {
            ZonedDateTime expiryDate = calculateExpiryDate(confirmLinkExpiredDays);

            UserData dataForConfirmation = UserData.builder()
                    .firstName(userConfirmationCode.getUser().getFirstName())
                    .lastName(userConfirmationCode.getUser().getLastName())
                    .email(userConfirmationCode.getUser().getEmail())
                    .confirmationUUID(userConfirmationCode.getUuid())
                    .expiryDate(expiryDate)
                    .build();

            lmsMailSender.sendMailForConfirmation(dataForConfirmation);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send registration email", e);
        }
    }

    private ZonedDateTime calculateExpiryDate(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusDays(days).toLocalDate().atStartOfDay().plusDays(1).minusSeconds(1);
        return expiryDate.atZone(ZoneId.systemDefault());
    }

    @Async
    public void sendPasswordResetEmail(UserConfirmationCode userConfirmationCode) throws EmailSendingException {
        UserData dataForPasswordReset = UserData.builder()
                .email(userConfirmationCode.getUser().getEmail())
                .confirmationUUID(userConfirmationCode.getUuid())
                .build();

        lmsMailSender.sendMailForPasswordReset(dataForPasswordReset);
    }

    @Async
    public void sendChangePasswordNotification(String email) {
        lmsMailSender.sendPasswordChangeNotification(email);
    }
}
