package ku_rum.backend.domain.user.application.mail;

import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.domain.user.dto.request.mail.MailSendRequest;
import ku_rum.backend.domain.user.dto.request.mail.MailVerificationRequest;
import ku_rum.backend.domain.user.dto.response.mail.MailVerificationResponse;
import ku_rum.backend.global.exception.user.DuplicateEmailException;
import ku_rum.backend.global.exception.user.MailSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

import static ku_rum.backend.domain.user.domain.MailSendSetting.MAIL_SEND_INFO;
import static ku_rum.backend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;

    @Qualifier("stringRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    public void sendEmail(String toEmail,
                          String title,
                          String text) {
        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
        try {
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            log.debug("메일 서버에서 메일 전송 중, 오류가 발생했습니다. toEmail: {}, " +
                    "title: {}, text: {}", toEmail, title, text);
            throw new MailSendException(MAIL_SEND_EXCEPTION, e.getMessage());
        }
    }

    public void sendCodeToEmail(final MailSendRequest mailSendRequest) {
        validateDuplicateEmail(mailSendRequest.email());

        String title = MAIL_SEND_INFO.getTitle();
        String authCode = this.createCode();

        sendEmail(mailSendRequest.email(), title, authCode);
        storeAuthCode(mailSendRequest.email(), authCode);
    }

    public MailVerificationResponse verifiedCode(final MailVerificationRequest mailVerificationRequest) {
        String email = mailVerificationRequest.email();
        String authCode = mailVerificationRequest.code();

        validateDuplicateEmail(email);
        return MailVerificationResponse.of(isValidAuthCode(generateKeyByEmail(email), authCode));
    }

    private void storeAuthCode(String email, String authCode) {
        redisTemplate.opsForValue().set(generateKeyByEmail(email),
                authCode, getAuthCodeExpirationMills());
    }

    private Duration getAuthCodeExpirationMills() {
        return Duration.ofMillis(MAIL_SEND_INFO.getAuthCodeExpirationMillis());
    }

    private String generateKeyByEmail(String email) {
        return MAIL_SEND_INFO.getAUTH_CODE_PREFIX() + email;
    }

    private String getRedisAuthCode(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    private boolean checkExistsValue(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private boolean isValidAuthCode(String key, String authCode) {
        return checkExistsValue(key) && getRedisAuthCode(key).equals(authCode);
    }

    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < MAIL_SEND_INFO.getCodeLength(); i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("네자리 난수 생성 시 예외가 발생했습니다.");
            throw new MailSendException(INVALID_AUTH_CODE_GENERATION);
        }
    }

    private SimpleMailMessage createEmailForm(String toEmail,
                                              String title,
                                              String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }

    private void validateDuplicateEmail(final String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(DUPLICATE_EMAIL);
        }
    }

}