package ku_rum.backend.domain.notice.domain.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.QNotice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QNotice qNotice = QNotice.notice;

    //    @Query("SELECT n FROM Notice n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    //    List<Notice> searchNoticesByTitle(@Param("searchTerm") String searchTerm);
    //를 Querydsl로 리팩토링해보자!
    public List<Notice> searchNoticesByTitle(String searchTerm) {   //해당 검색어로 모든 공지사항 결과 가져오는 쿼리
        return queryFactory
                .selectFrom(qNotice)
                .where(qNotice.title.lower().likeIgnoreCase("%" + searchTerm + "%"))
                .fetch();
    }

    // 페이징 처리
    @Override
    public List<Notice> searchNoticesByTitleWithPaging(String searchTerm, int page, int pageSize) {
        return queryFactory
                .selectFrom(qNotice)
                .where(qNotice.title.lower().likeIgnoreCase("%" + searchTerm + "%"))
                .orderBy(qNotice.date.desc())
                .offset((long) page * pageSize)
                .limit(pageSize)
                .fetch();
    }

    //조회수 db에 동기화
    @Override
    public void updateViewCount(String url, long count) {
        queryFactory
                .update(qNotice)
                .set(qNotice.viewCount.count, qNotice.viewCount.count.add(count))
                .where(qNotice.url.eq(url))
                .execute();
    }


}