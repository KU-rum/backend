package ku_rum.backend.integration.domain.rank;

import java.util.List;
import ku_rum.backend.domain.rank.application.RankService;
import ku_rum.backend.domain.rank.application.response.GetPlaceUserRankResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.integration.user.config.UserTestConfig;
import ku_rum.backend.integration.user.data.UserData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({UserTestConfig.class})
@DisplayName("랭킹 서비스 통합 테스트")
@ActiveProfiles("test")
public class RankServiceTest {

    @Autowired
    RankService rankService;

    @Autowired
    UserData userData;

    CustomUserDetails userDetails;

    @BeforeEach
    void beforeAll() {
        userDetails = userData.saveUserData();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void afterEach() {
        userData.deleteUsers();
    }

    @Test
    @DisplayName("아직 공유 횟수가 없다면 빈 리스트를 반환한다")
    void getEmptyRank() {
        //given

        //when
        List<GetPlaceUserRankResponse> response = rankService.getPlaceUserRank(userDetails);

        //then
        Assertions.assertThat(response.size()).isEqualTo(0);

    }
}
