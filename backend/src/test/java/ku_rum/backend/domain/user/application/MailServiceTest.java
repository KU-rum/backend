package ku_rum.backend.domain.user.application;

import ku_rum.backend.domain.user.application.mail.MailService;
import ku_rum.backend.domain.user.dto.request.mail.MailSendRequest;
import ku_rum.backend.domain.user.dto.request.mail.MailVerificationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static ku_rum.backend.domain.user.domain.MailSendSetting.MAIL_SEND_INFO;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MailServiceTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MailService mailService;

    @Test
    @DisplayName("회원에게 정상적으로 인증 이메일을 전송한다.")
    void sendCodeToEmail() {
        //given
        MailSendRequest mailSendRequest = new MailSendRequest("kmw10693@konkuk.ac.kr");
        String key = generateKeyByEmail(mailSendRequest.email());
        String email = mailSendRequest.email();

        //when
        mailService.sendCodeToEmail(mailSendRequest);

        //then
        assertThat(getRedisAuthCode(generateKeyByEmail(email))
                .equals(redisTemplate.opsForValue().get(key)));

    }

    @Test
    @DisplayName("회원이 올바른 인증번호를 인증했을 시, true를 반환한다.")
    void verifiedCode() {
        MailSendRequest mailSendRequest = new MailSendRequest("kmw10693@konkuk.ac.kr");
        mailService.sendCodeToEmail(mailSendRequest);

        String authCode = getRedisAuthCode(generateKeyByEmail("kmw10693@konkuk.ac.kr"));

        MailVerificationRequest mailVerificationRequest =
                new MailVerificationRequest("kmw10693@konkuk.ac.kr", authCode);

        //when then
        assertThat(mailService.verifiedCode(mailVerificationRequest))
                .extracting("verified")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("회원이 올바르지 않는 인증번호를 인증했을시, false를 반환한다.")
    void nonVerifiedCode() {
        MailSendRequest mailSendRequest = new MailSendRequest("kmw10693@konkuk.ac.kr");
        mailService.sendCodeToEmail(mailSendRequest);

        String authCode = getRedisAuthCode(generateKeyByEmail("testtest1234@konkuk.ac.kr"));

        MailVerificationRequest mailVerificationRequest =
                new MailVerificationRequest("kmw10693@konkuk.ac.kr", authCode);

        //when then
        assertThat(mailService.verifiedCode(mailVerificationRequest))
                .extracting("verified")
                .isEqualTo(false);
    }


    private String generateKeyByEmail(String email) {
        return MAIL_SEND_INFO.getAUTH_CODE_PREFIX() + email;
    }

    private String getRedisAuthCode(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}