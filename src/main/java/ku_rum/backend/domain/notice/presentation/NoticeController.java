package ku_rum.backend.domain.notice.presentation;

import ku_rum.backend.domain.notice.application.NoticeService;
import ku_rum.backend.domain.notice.dto.response.NoticeDetailResponse;
import ku_rum.backend.domain.notice.dto.response.NoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            @RequestParam("category") Integer categoryId,
            Pageable pageable
    ) {
        return noticeService.findByCategory(categoryId, pageable);
    }

    @GetMapping("{Id}")
    public NoticeDetailResponse getNoticeDetailById(@PathVariable Long noticeId) {
        return noticeService.findByNoticeId(noticeId);
    }
}
