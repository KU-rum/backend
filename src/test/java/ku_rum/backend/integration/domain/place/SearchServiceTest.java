package ku_rum.backend.integration.domain.place;

import java.util.List;
import ku_rum.backend.domain.place.application.SearchService;
import ku_rum.backend.domain.place.application.response.SearchPlaceResponse;
import ku_rum.backend.domain.place.domain.repository.PlaceRepository;
import ku_rum.backend.integration.domain.place.config.PlaceTestConfig;
import ku_rum.backend.integration.domain.place.data.PlaceData;
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
@Import({PlaceTestConfig.class})
@DisplayName("검색 서비스 통합 테스트")
@ActiveProfiles("test")
public class SearchServiceTest {

    @Autowired
    SearchService searchService;

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    PlaceData placeData;

    @BeforeEach
    void init() {
        placeData.savePlaceData();
    }

    @AfterEach
    void afterEach() {
        placeData.afterEach();
    }

    @Test
    @DisplayName("정확한 장소를 입력하면 해당 장소를 결과로 반환한다")
    void searchCase1() {
        //given
        String query = "공학관B";

        //when
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(response.size()).isEqualTo(1);
        Assertions.assertThat(response.get(0).name()).isEqualTo(query);
    }

    @Test
    @DisplayName("공B102를 검색하면 공학관B를 검색한 것과 같은 결과를 반환한다")
    void searchCase2() {
        //given
        String preQuery = "공학관B";
        List<SearchPlaceResponse> preResponse = searchService.searchPlace(preQuery);

        //when
        String query = "공B102";
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(preResponse).isEqualTo(response);
    }

    @Test
    @DisplayName("공B를 검색하면 공학관B를 검색한 것과 같은 결과를 반환한다")
    void searchCase3() {
        //given
        String preQuery = "공학관B";
        List<SearchPlaceResponse> preResponse = searchService.searchPlace(preQuery);

        //when
        String query = "공B";
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(preResponse).isEqualTo(response);
    }

    @Test
    @DisplayName("공학관B를 검색하면 케이큐브가 나오지 않는다")
    void searchCase4() {
        //given
        String query = "공학관B";
        String kCube = "공학관 케이큐브";

        //when
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(response.stream().noneMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals(kCube)
        )).isTrue();
    }

    @Test
    @DisplayName("공학관을 검색하면 공학관 케이큐브를 확인할 수 있다")
    void searchCase5() {
        //given
        String query = "공학관";
        String kCube = "공학관 케이큐브";

        //when
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(response.stream().anyMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals(kCube)
        )).isTrue();
    }

    @Test
    @DisplayName("건물 카테고리를 검색시 건물을 모두 검색한다.")
    void searchCase6() {
        //given
        String query = "건물";

        //when
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(response.stream().anyMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals("공학관")
        )).isTrue();
        Assertions.assertThat(response.stream().anyMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals("공학관A")
        )).isTrue();
        Assertions.assertThat(response.stream().anyMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals("공학관B")
        )).isTrue();
        Assertions.assertThat(response.stream().anyMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals("공학관C")
        )).isTrue();
        Assertions.assertThat(response.stream().anyMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals("공학관D")
        )).isTrue();
    }

    @Test
    @DisplayName("컴퓨터공학부를 검색시 컴퓨터공학부 학과 사무실을 검색할 수 있다")
    void searchCase7() {
        //given
        String query = "컴퓨터공학부";

        //when
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(response.stream().anyMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals("컴퓨터공학부")
        )).isTrue();
    }

    @Test
    @DisplayName("경영관을 검색할 수 있고, 경영206은 경영관과 같은 검색 결과를 반환해야 한다")
    void searchTest8() {
        //given
        String query = "경영관";
        String aliasQuery = "경영206";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("상허연구관을 검색할 수 있고, 상허관197은 상허연구관과 같은 검색 결과를 반환해야 한다")
    void searchTest9() {
        //given
        String query = "상허연구관";
        String aliasQuery = "상허관197";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("교육과학관을 검색할 수 있고, 사117은 교육과학관과 같은 검색 결과를 반환해야 한다")
    void searchTest10() {
        //given
        String query = "교육과학관";
        String aliasQuery = "사117";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("예술문화관을 검색할 수 있고, 예1007은 예술문화관과 같은 검색 결과를 반환해야 한다")
    void searchTest11() {
        //given
        String query = "예술문화관";
        String aliasQuery = "예1007";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("언어교육관을 검색할 수 있고, 언어원은 언어교육관과 같은 검색 결과를 반환해야 한다")
    void searchTest12() {
        //given
        String query = "언어교육관";
        String aliasQuery = "언어원";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("법학관을 검색할 수 있고, 법102와 종강102은 법학관과 같은 검색 결과를 반환해야 한다")
    void searchTest13() {
        //given
        String query = "법학관";
        String aliasQuery1 = "법102";
        String aliasQuery2 = "종강102";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery1);
        assertSearchResultMatches(query, aliasQuery2);
    }

    @Test
    @DisplayName("의생명과학연구관을 검색할 수 있고, 의102은 의생명과학연구관과 같은 검색 결과를 반환해야 한다")
    void searchTest14() {
        //given
        String query = "의생명과학연구관";
        String aliasQuery = "의102";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("생명과학관을 검색할 수 있고, 생302은 생명과학관과 같은 검색 결과를 반환해야 한다")
    void searchTest15() {
        //given
        String query = "생명과학관";
        String aliasQuery = "생302";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("동물생명과학관을 검색할 수 있고, 동302은 동물생명과학관과 같은 검색 결과를 반환해야 한다")
    void searchTest16() {
        //given
        String query = "동물생명과학관";
        String aliasQuery = "동302";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("산학협동관을 검색할 수 있고, 산학302은 산학협동관과 같은 검색 결과를 반환해야 한다")
    void searchTest17() {
        //given
        String query = "산학협동관";
        String aliasQuery = "산학302";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("수의학관을 검색할 수 있고, 수302은 수의학관과 같은 검색 결과를 반환해야 한다")
    void searchTest18() {
        //given
        String query = "수의학관";
        String aliasQuery = "수302";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("수의학관을 검색할 수 있고, 수302은 수의학관과 같은 검색 결과를 반환해야 한다")
    void searchTest19() {
        //given
        String query = "새천년관";
        String aliasQuery = "새302";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("건축관을 검색할 수 있고, 건302은 건축관과 같은 검색 결과를 반환해야 한다")
    void searchTest20() {
        //given
        String query = "건축관";
        String aliasQuery = "건302";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("해봉부동산학관을 검색할 수 있고, 부302은 해봉부동산학관과 같은 검색 결과를 반환해야 한다")
    void searchTest21() {
        //given
        String query = "해봉부동산학관";
        String aliasQuery = "부302";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("인문학관을 검색할 수 있고, 문302은 인문학관과 같은 검색 결과를 반환해야 한다")
    void searchTest22() {
        //given
        String query = "인문학관";
        String aliasQuery = "문302";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("신공학관을 검색할 수 있고, 신공1016은 인문학관과 같은 검색 결과를 반환해야 한다")
    void searchTest23() {
        //given
        String query = "신공학관";
        String aliasQuery = "신공1016";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("과학관을 검색할 수 있고, 이1016은 과학관과 같은 검색 결과를 반환해야 한다")
    void searchTest24() {
        //given
        String query = "과학관";
        String aliasQuery = "이1016";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("창의관을 검색할 수 있고, 창1016은 창의관과 같은 검색 결과를 반환해야 한다")
    void searchTest25() {
        //given
        String query = "창의관";
        String aliasQuery = "창1016";

        //when

        //then
        assertSearchResultExist(query);
        assertSearchResultMatches(query, aliasQuery);
    }

    @Test
    @DisplayName("산학협동관 이마트24를 검색할 수 있다")
    void searchCase26() {
        //given
        String query = "이마트24";
        String eMart = "산학협동관 이마트24";

        //when
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(response.stream().anyMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals(eMart)
        )).isTrue();
    }

    @Test
    @DisplayName("학생회관 CU를 검색할 수 있다")
    void searchCase27() {
        //given
        String query = "학생회관 CU";

        //when
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(response.stream().anyMatch(
                searchPlaceResponse -> searchPlaceResponse.name().equals(query)
        )).isTrue();
    }

    @Test
    @DisplayName("공A를 검색하면 공학관A를 검색한 것과 같은 결과를 반환한다")
    void searchCase28() {
        //given
        String preQuery = "공학관A";
        List<SearchPlaceResponse> preResponse = searchService.searchPlace(preQuery);

        //when
        String query = "공A";
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(preResponse).isEqualTo(response);
    }

    @Test
    @DisplayName("공C를 검색하면 공학관C를 검색한 것과 같은 결과를 반환한다")
    void searchCase29() {
        //given
        String preQuery = "공학관C";
        List<SearchPlaceResponse> preResponse = searchService.searchPlace(preQuery);

        //when
        String query = "공C";
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        //then
        Assertions.assertThat(preResponse).isEqualTo(response);
    }

    private void assertSearchResultMatches(String expectedQuery, String aliasQuery) {
        List<SearchPlaceResponse> expected = searchService.searchPlace(expectedQuery);
        List<SearchPlaceResponse> actual = searchService.searchPlace(aliasQuery);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    private void assertSearchResultExist(String query) {
        List<SearchPlaceResponse> response = searchService.searchPlace(query);

        Assertions.assertThat(response.stream().anyMatch(
                res -> res.name().equals(query)
        )).isTrue();
    }
}