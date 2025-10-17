package ku_rum.backend.domain.notice.dto.response;


import ku_rum.backend.domain.notice.domain.Notice;

public record NoticeResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String title,
        String link,
        String pubDate,
        String author,
        String description
) {
    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getCategoryId(),
                notice.getCategoryName(),
                notice.getTitle(),
                notice.getLink(),
                notice.getPubDate(),
                notice.getAuthor(),
                notice.getDescription()
        );
    }
}

