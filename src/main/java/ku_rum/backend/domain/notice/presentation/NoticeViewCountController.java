package ku_rum.backend.domain.notice.presentation;

import ku_rum.backend.domain.notice.application.ViewCountService;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeViewCountController {
    private final ViewCountService viewCountService;

    /**
     * 공지사항 조회수 증가
     * @param url
     * @return
     */
    @GetMapping("/url")
    public BaseResponse<String> increaseViewCount(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestParam(name = "url") String url) {
        viewCountService.enqueueLockRequest(url);
        return BaseResponse.ok(url + "에 대해 조회수가 증가되었습니다.");
    }

    @GetMapping("/popular/url")
    public BaseResponse<List<String>> mostViewedNotices(@AuthenticationPrincipal CustomUserDetails userDetails){
        return BaseResponse.ok(viewCountService.mostViewedNotices());
    }

}
