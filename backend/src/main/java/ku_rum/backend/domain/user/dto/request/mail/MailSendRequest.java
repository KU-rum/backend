package ku_rum.backend.domain.user.dto.request.mail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MailSendRequest(
        @NotBlank(message = "이메일 입력은 필수입니다.")
        @Pattern(regexp = "[a-zA-Z0-9_.+-]+@konkuk\\.ac\\.kr$", message = "이메일의 끝자리는 @konkuk.ac.kr로 끝나야 합니다.")
        String email) {

    public MailSendRequest(String email) {
        this.email = email;
    }
}
