package ku_rum.backend.domain.notice.presentation;

import java.util.List;
import ku_rum.backend.domain.notice.application.NoticeService;
import ku_rum.backend.domain.notice.dto.response.NoticeDetailResponse;
import ku_rum.backend.domain.notice.dto.response.NoticeResponse;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public Page<NoticeResponse> getNoticesByCategory(
            @RequestParam("category") Long categoryId,
            Pageable pageable
    ) {
        return noticeService.findByCategory(categoryId, pageable);
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<String> getNoticeDetailById(@PathVariable("noticeId") Long noticeId) {
        NoticeDetailResponse response = noticeService.findByNoticeId(noticeId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE) // HTML로 전달
                .body(response.content());
    }

    @GetMapping("/popular")
    public BaseResponse<List<NoticeResponse>> getPopularNotices() {
        List<NoticeResponse> response = noticeService.findPopularNotice();
        return BaseResponse.ok(response);
    }
}
