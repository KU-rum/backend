package ku_rum.backend.domain.notice.application;

import ku_rum.backend.domain.notice.domain.PublishStatus;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import ku_rum.backend.domain.notice.dto.response.NoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Page<NoticeResponse> findByCategory(Integer categoryId, Pageable pageable) {
        return noticeRepository.findByCategoryIdAndPublishStatus(categoryId, PublishStatus.SUCCESS_CRAWLING, pageable)
                .map(NoticeResponse::from);
    }
}

