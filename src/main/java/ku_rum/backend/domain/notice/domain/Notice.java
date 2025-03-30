package ku_rum.backend.domain.notice.domain;

import jakarta.persistence.*;
import ku_rum.backend.domain.notice.domain.value.ViewCount;
import ku_rum.backend.domain.notice.domain.value.converter.ViewCountConverter;
import ku_rum.backend.global.exception.notice.InvalidViewCountException;
import ku_rum.backend.global.support.type.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.BELOW_ZERO;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "notice")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 300, nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String date; // 공지사항 작성일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeCategory noticeCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeStatus noticeStatus;

    @Convert(converter = ViewCountConverter.class)
    @Column(name = "view_count")
    private ViewCount viewCount;

    @Builder
    private Notice(String url, String title, String date, NoticeCategory noticeCategory, NoticeStatus noticeStatus) {
        this.url = url;
        this.title = title;
        this.date = date;
        this.noticeCategory = noticeCategory;
        this.noticeStatus = noticeStatus;
        this.viewCount = new ViewCount(0);
    }

    public static Notice of(String title, String url, String date, NoticeCategory noticeCategory, NoticeStatus noticeStatus) {
        return Notice.builder()
                .title(title)
                .url(url)
                .date(date)
                .noticeCategory(noticeCategory)
                .noticeStatus(noticeStatus)
                .build();
    }

    public void incrementViewCount(){
        this.viewCount = this.viewCount.increment();
    }

}
