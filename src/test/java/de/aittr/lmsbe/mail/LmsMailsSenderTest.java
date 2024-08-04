//package de.aittr.lmsbe.mail;
//
//
//import de.aittr.lmsbe.mail.utill.MailTemplateUtil;
//import de.aittr.lmsbe.mail.utill.impl.FreemarkerMailTemplateUtilImpl;
//import freemarker.template.Configuration;
//import freemarker.template.Template;
//import freemarker.template.TemplateException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//
//@ActiveProfiles("test")
//public class LmsMailsSenderTest {
//    private MailTemplateUtil mailTemplateUtil;
//    private Configuration freemarkerConfig;
//
//    @BeforeEach
//    public void setUp() {
//        freemarkerConfig = mock(Configuration.class);
//        mailTemplateUtil = new FreemarkerMailTemplateUtilImpl(freemarkerConfig);
//    }
//
//    @Test
//    public void testGetMailForConfirmation() throws IOException, TemplateException {
//        String firstName = "John";
//        String lastName = "Doe";
//        String UUID = "123456";
//        String expectedMailContent = "Hello John Doe, Please confirm your email using the code: 123456";
//        Template template = mock(Template.class);
//        when(freemarkerConfig.getTemplate("confirmation_mail.ftl")).thenReturn(template);
//        doReturn(expectedMailContent).when(template).toString();
//
//        String actualMailContent = mailTemplateUtil.getMailForConfirmation(firstName, lastName, UUID);
//        verify(freemarkerConfig).getTemplate("confirmation_mail.ftl");
//
//        assertEquals(expectedMailContent, actualMailContent);
//    }
//}
//
