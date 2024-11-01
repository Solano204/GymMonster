package application.Ports.Drivers.IServices;

import java.io.File;

import reactor.core.publisher.Mono;

public interface IEmailService {

    Mono <Boolean> sendEmail(String[] email, String username, String password);

    Mono<Boolean> sendEmailWithFile(String[] toUser, String subject, String message, File file);
}
