package ku_rum.backend.domain.bookmark.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ku_rum.backend.domain.college.domain.College;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.user.domain.AgreementStatus;
import ku_rum.backend.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookmarkTest {

    @DisplayName("북마크 생성시, 유저와 공지사항을 넣어준다.")
    @Test
    void saveBookmarkWithUserAndNotice() {
        //given
        User user = createUser("사용자1", "202112322");
        Notice notice = Notice.builder()
                .id(1L)
                .categoryId(Long.valueOf(234))
                .categoryName("학사")
                .title("타이틀")
                .pubDate("2024-01-03")
                .author("교무팀")
                .description("교무 관련 공지사항")
                .build();

        //when
        Bookmark bookmark = Bookmark.of(user, notice);

        //then
        assertThat(bookmark.getUser()).isEqualTo(user);
        assertThat(bookmark.getNotice()).isEqualTo(notice);
    }

    private User createUser(String username, String studentID) {
        College college = College.of("공과대학");
        Department department = Department.of("컴퓨터공학부", college);
        return User.of(username, "kmw106933@konkuk.ac.kr", studentID, "password123", "202112322", department,
                AgreementStatus.AGREED, null);
    }
}