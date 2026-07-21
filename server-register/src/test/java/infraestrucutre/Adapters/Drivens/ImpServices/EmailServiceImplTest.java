package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSenderImpl mailSender;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl();
        ReflectionTestUtils.setField(emailService, "emailCompany", "no-reply@gymmonster.test");
        ReflectionTestUtils.setField(emailService, "mailSender", mailSender);
    }

    @Test
    void sendEmail_returnsTrue_whenMailSenderSucceeds() {
        StepVerifier.create(emailService.sendEmail(new String[]{"jdoe@test.com"}, "jdoe", "pw"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void sendEmail_returnsFalse_whenMailSenderThrows() {
        doThrow(new MailSendException("SMTP down")).when(mailSender).send(any(SimpleMailMessage.class));

        StepVerifier.create(emailService.sendEmail(new String[]{"jdoe@test.com"}, "jdoe", "pw"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void sendEmailWithFile_returnsTrue_whenMailSenderSucceeds() throws IOException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        File tempFile = File.createTempFile("attachment", ".txt");
        tempFile.deleteOnExit();

        StepVerifier.create(emailService.sendEmailWithFile(
                        new String[]{"jdoe@test.com"}, "Subject", "Body", tempFile))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void sendEmailWithFile_returnsFalse_whenAttachmentFileDoesNotExist() {
        // No createMimeMessage() stub: the file-existence check now runs before that call, so
        // it's never invoked for a missing attachment.
        File missingFile = new File("this-file-does-not-exist.txt");

        StepVerifier.create(emailService.sendEmailWithFile(
                        new String[]{"jdoe@test.com"}, "Subject", "Body", missingFile))
                .expectNext(false)
                .verifyComplete();
    }
}
