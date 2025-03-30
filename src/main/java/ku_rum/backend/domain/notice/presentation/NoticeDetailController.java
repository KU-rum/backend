package ku_rum.backend.domain.notice.presentation;

import ku_rum.backend.domain.notice.application.NoticeService;
import ku_rum.backend.domain.notice.dto.response.NoticeSimpleResponse;
import ku_rum.backend.domain.notice.dto.response.ViewCountResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeDetailController {
    private final NoticeService noticeService;

    /**
     * 공지사항 내용 조회
     * @param noticeId
     * @return
     */
    @GetMapping("/{id}")
    public BaseResponse<ViewCountResponse> searchNotices(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @PathVariable(name = "id") long noticeId) {
        return BaseResponse.ok(noticeService.increaseViewCount(noticeId));
    }

}
