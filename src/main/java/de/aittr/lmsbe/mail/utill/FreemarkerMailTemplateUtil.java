package de.aittr.lmsbe.mail.utill;

import de.aittr.lmsbe.exception.EmailSendingException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FreemarkerMailTemplateUtil {
    Configuration freemarkerConfig;

    String baseUrl;

    public FreemarkerMailTemplateUtil(@Qualifier("freeMarkerConfiguration") Configuration freemarkerConfig, @Value("${base.url}") String baseUrl) {
        this.freemarkerConfig = freemarkerConfig;
        this.baseUrl = baseUrl;
    }

    public String getMailForConfirmation(String firstName, String lastName, String email, String UUID, ZonedDateTime expiryDate) { //,ZonedDateTime expiryDate
        try {
            Template template = freemarkerConfig.getTemplate("confirmation_mail.ftlh");
            Map<String, Object> model = new HashMap<>();
            model.put("firstName", firstName);
            model.put("lastName", lastName);
            model.put("email", email);
            model.put("confirmationUUID", UUID);
            model.put("baseUrl", baseUrl);

            model.put("expiryDate", expiryDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            throw new EmailSendingException("Error processing mail template", e);
        }
    }

    public String getMailForPasswordReset(String resetToken) {
        try {
            Template template = freemarkerConfig.getTemplate("password_reset_mail.ftlh");
            Map<String, Object> model = new HashMap<>();
            model.put("confirmationUUID", resetToken);
            model.put("baseUrl", baseUrl);

            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            throw new EmailSendingException("Error processing mail template", e);
        }
    }
}


