package ku_rum.backend.domain.user.presentation.mail;

import jakarta.validation.Valid;
import ku_rum.backend.domain.user.application.mail.MailService;
import ku_rum.backend.domain.user.dto.request.mail.MailSendRequest;
import ku_rum.backend.domain.user.dto.request.mail.MailVerificationRequest;
import ku_rum.backend.domain.user.dto.response.mail.MailVerificationResponse;
import ku_rum.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mails")
@RequiredArgsConstructor
@Validated
public class MailController {
    private final MailService mailService;

    @PostMapping
    public BaseResponse<String> sendMail(@RequestBody @Valid final MailSendRequest mailSendRequest) {
        mailService.sendCodeToEmail(mailSendRequest);
        return BaseResponse.ok("메일이 성공적으로 전송되었습니다.");
    }

    @GetMapping("/mail_verifications")
    public BaseResponse<MailVerificationResponse> verificationEmail(@RequestBody @Valid final MailVerificationRequest mailVerificationRequest) {
        return BaseResponse.ok(mailService.verifiedCode(mailVerificationRequest));
    }
}
