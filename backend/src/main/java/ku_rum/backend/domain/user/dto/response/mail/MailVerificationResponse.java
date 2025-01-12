package ku_rum.backend.domain.user.dto.response.mail;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailVerificationResponse {
    private boolean verified;

    private MailVerificationResponse(boolean verified) {
        this.verified = verified;
    }

    public static MailVerificationResponse of(boolean verified) {
        return new MailVerificationResponse(verified);
    }
}
