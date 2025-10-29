package ku_rum.backend.domain.notice.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_NOTICE;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_NOTICE_DETAIL;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeDetail;
import ku_rum.backend.domain.notice.domain.PublishStatus;
import ku_rum.backend.domain.notice.domain.repository.NoticeDetailRepository;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import ku_rum.backend.domain.notice.dto.response.NoticeDetailResponse;
import ku_rum.backend.domain.notice.dto.response.NoticeResponse;
import ku_rum.backend.global.exception.global.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeDetailRepository noticeDetailRepository;

    public Page<NoticeResponse> findByCategory(Long categoryId, Pageable pageable) {
        return noticeRepository.findByCategoryIdAndPublishStatus(categoryId, PublishStatus.SUCCESS_CRAWLING, pageable)
                .map(NoticeResponse::from);
    }

    public NoticeDetailResponse findByNoticeId(Long noticeId) {
        Notice notice = findNoticeByNoticeId(noticeId);
        NoticeDetail noticeDetail = noticeDetailRepository.findByNotice(notice)
                .orElseThrow(() -> new GlobalException(NO_SUCH_NOTICE_DETAIL));
        String encodedHtml = noticeDetail.getHtml_content();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedHtml);
        String htmlContent = new String(decodedBytes, StandardCharsets.UTF_8);
        return new NoticeDetailResponse(noticeDetail.getNotice().getId(), htmlContent);
    }

    public Notice findNoticeByNoticeId(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> new GlobalException(NO_SUCH_NOTICE));
    }
}

