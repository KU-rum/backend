package ku_rum.backend.domain.common.mail.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import ku_rum.backend.global.exception.user.MailSendException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.MAIL_SEND_EXCEPTION;

@Service
@RequiredArgsConstructor
public class MailSenderService {
    private final JavaMailSender emailSender;

    public void sendHtml(String toEmail, String title, String htmlContent) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(htmlContent, true);

            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException(MAIL_SEND_EXCEPTION);
        }
    }
}