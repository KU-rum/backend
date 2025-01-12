package ku_rum.backend.domain.user.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ku_rum.backend.domain.user.dto.request.mail.MailSendRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MailSendRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest
    @CsvSource(value = {"kmw10693@naver.com:true", "kmw10693@konkuk.ac.kr:false", "kmw10693:true", "kmw10693@naver.com:true"}, delimiter = ':')
    @DisplayName("이메일이 @konkuk.ac.kr로 끝나는지 검증한다.")
    void blankEmail(String email, boolean flag) {
        MailSendRequest mailSendRequest = new MailSendRequest(email);

        assertThat(validator.validate(mailSendRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals("이메일의 끝자리는 @konkuk.ac.kr로 끝나야 합니다.")))
                .isEqualTo(flag);
    }
}