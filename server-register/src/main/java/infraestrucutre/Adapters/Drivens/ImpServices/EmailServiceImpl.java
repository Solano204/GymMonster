package infraestrucutre.Adapters.Drivens.ImpServices;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import infraestrucutre.Adapters.Drivens.IServices.IEmailService;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements IEmailService{

    @Value("${email.sender}")
    private String emailCompany;

    private String MESSSAGE_WELCOME= "WELCOME TO THE GYM MONSTERS ";
    private String MESSSAGE_LOGIN= "Now you can see your information with this link: 'PageGym' using your username and password.";
    
    @Autowired
    private JavaMailSender mailSender;

@Override
public Mono<Boolean> sendEmail(String[] email, String username, String password) {
    return Mono.fromRunnable(() -> {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailCompany);
        mailMessage.setTo(email);
        mailMessage.setSubject(MESSSAGE_WELCOME);
        mailMessage.setText(MESSSAGE_LOGIN 
        + "/nUsername: " + username + 
        "/nPassword: " + password +
        "/n We're glad to have you!" );
        mailSender.send(mailMessage);
    })
    .thenReturn(true) // Return true if the email is sent without exception
    .onErrorReturn(false); // Return false in case of any exception
}

@Override
public Mono<Boolean> sendEmailWithFile(String[] toUser, String subject, String message, File file) {
    return Mono.fromRunnable(() -> {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(emailCompany);
            mimeMessageHelper.setTo(toUser);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.addAttachment(file.getName(), file);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    })
    .thenReturn(true) // Return true if the email is sent successfully
    .onErrorReturn(false); // Return false if an error occurs
}

}
