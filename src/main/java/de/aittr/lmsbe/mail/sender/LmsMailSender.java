package de.aittr.lmsbe.mail.sender;

import de.aittr.lmsbe.mail.dto.UserData;
import de.aittr.lmsbe.mail.utill.FreemarkerMailTemplateUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LmsMailSender {

    JavaMailSender mailSender;
    FreemarkerMailTemplateUtil mailTemplateUtil;

    @SneakyThrows
    public void sendMailForConfirmation(UserData dataForConfirmation) {
        String emailBody = mailTemplateUtil.getMailForConfirmation(
                dataForConfirmation.getFirstName(),
                dataForConfirmation.getLastName(),
                dataForConfirmation.getEmail(),
                dataForConfirmation.getConfirmationUUID(),
                dataForConfirmation.getExpiryDate()

        );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        if (dataForConfirmation.getEmail() == null) {
            throw new IllegalArgumentException("Recipient email address is null.");
        }

        helper.setTo(dataForConfirmation.getEmail());
        helper.setSubject("AIT-TR LMS Account Activation – Welcome Aboard!");
        helper.setText(emailBody, true);

        mailSender.send(message);

    }

    @SneakyThrows
    public void sendMailForPasswordReset(UserData dataForConfirmation) {
        String emailBody = mailTemplateUtil.getMailForPasswordReset(dataForConfirmation.getConfirmationUUID());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setTo(dataForConfirmation.getEmail());
        helper.setSubject("Password Reset Email");
        helper.setText(emailBody, true);

        mailSender.send(message);
    }

    @SneakyThrows
    public void sendPasswordChangeNotification(String email) {
        String emailBody = "Your password has been successfully changed";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Notification of password change");
        message.setText(emailBody);

        mailSender.send(message);
    }
}
