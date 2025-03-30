package ku_rum.backend.domain.notice.domain.repository;

import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepositoryCustom {

    List<Notice> searchNoticesByTitleWithPaging(String searchTerm, int page, int pageSize);

    void updateViewCount(String url, long count);

    String findTitleByUrl(String url);
}