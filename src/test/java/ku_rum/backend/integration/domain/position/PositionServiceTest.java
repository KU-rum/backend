package ku_rum.backend.integration.domain.position;

import java.math.BigDecimal;
import ku_rum.backend.domain.place.application.PositionService;
import ku_rum.backend.domain.place.application.response.CurrentPositionResponse;
import ku_rum.backend.domain.place.domain.repository.PlaceRepository;
import ku_rum.backend.domain.place.dto.request.CurrentPositionRequest;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import ku_rum.backend.integration.domain.position.config.PositionDataConfig;
import ku_rum.backend.integration.domain.position.data.PositionData;
import ku_rum.backend.integration.domain.user.config.UserDataConfig;
import ku_rum.backend.integration.domain.user.data.UserData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({PositionDataConfig.class, UserDataConfig.class})
@DisplayName("위치 서비스 통합 테스트")
@ActiveProfiles("test")
public class PositionServiceTest {

    @Autowired
    PositionService positionService;

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    PositionData positionData;

    @Autowired
    UserData userData;

    CustomUserDetails userDetails;

    @BeforeEach
    void init() {
        userDetails = userData.saveUser();
        positionData.saveGeometryPlaceData();
    }

    @AfterEach
    void afterEach() {
        positionData.afterEach();
        userData.afterEach();
    }

    @Test
    @DisplayName("공학관 내부에서 공학관를 공유 확인할 수 있다")
    void test1() {
        //given
        String name = "공학관";
        CurrentPositionRequest request = new CurrentPositionRequest(BigDecimal.valueOf(37.541707),
                BigDecimal.valueOf(127.078782));

        //when
        CurrentPositionResponse response = positionService.getCurrentPosition(userDetails, request);

        //then
        Assertions.assertThat(response.placeName()).isEqualTo(name);
    }

    @Test
    @DisplayName("공학관 외부에서 공학관를 공유 확인할 수 없다")
    void test2() {
        //given
        String name = "공학관";
        CurrentPositionRequest request = new CurrentPositionRequest(BigDecimal.valueOf(37.541698),
                BigDecimal.valueOf(127.078424));

        //when, then
        Assertions.assertThatThrownBy(() ->
                        positionService.getCurrentPosition(userDetails, request)
                )
                .isInstanceOf(GlobalException.class) // <- 실제 예외 타입으로 교체
                .hasMessageContaining(BaseExceptionResponseStatus.PLACE_BUILDING_NOT_FOUND.getMessage());

    }
}
