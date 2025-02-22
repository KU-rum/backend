package ku_rum.backend.domain.building.service;

import jakarta.persistence.EntityManager;
import ku_rum.backend.domain.building.application.BuildingSearchService;
import ku_rum.backend.domain.building.domain.repository.BuildingQueryRepository;
import ku_rum.backend.domain.building.domain.repository.BuildingRepository;
import ku_rum.backend.domain.building.dto.response.BuildingResponse;
import ku_rum.backend.domain.category.dto.response.CategoryDetailResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@WebAppConfiguration
@SpringBootTest
@Transactional
public class BuildingCategoryInfoServiceTest {

    @Autowired
    BuildingSearchService buildingSearchService;
    @Autowired
    EntityManager em;
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private BuildingQueryRepository buildingQueryRepository;

    @DisplayName("특정카테고리_전체조회_성공")
    @Test
    public void 특정카테고리_전체조회_성공() throws Exception {
        // given
        List<BuildingResponse> responses = buildingSearchService.viewBuildingByCategory("학생 식당");

        // then
        Assertions.assertEquals(0, responses.size());
    }

//    @Test
//    public void 특정_학생식당_카테고리_디테일_조회_성공() throws Exception {
//        // given
//        CategoryDetailResponse response =  buildingSearchService.viewBuildingDetailByCategory("학생 식당", 16L);
//        // when
//        // then
//        Assertions.assertEquals(0, response.detailList().size());
//    }

// 아직 자동으로 데이터를 넣지 않아서 추후 넣을 예정
//    @Test
//    public void 특정_카페_카테고리_디테일_조회_성공() throws Exception {
//        // given
//        CategoryDetailFloorAndMenusProviding response = (CategoryDetailFloorAndMenusProviding) buildingSearchService.viewBuildingDetailByCategory("레스티오", buildingQueryRepository.findBuildingByNumber_test(5L));
//        // when
//        // then
//        Assertions.assertEquals(1, response.getMenus().size());
//        Assertions.assertEquals(1L, response.getFloor());
//    }
//
//    @Test
//    public void 특정_케이큐브_카테고리_디테일_조회_성공() throws Exception {
//        // given
//        CategoryDetailFloorAndMenusProviding response = (CategoryDetailFloorAndMenusProviding) buildingSearchService.viewBuildingDetailByCategory("케이큐브", buildingQueryRepository.findBuildingByNumber_test(4L));
//        // when
//        // then
//        Assertions.assertEquals(1L, response.getFloor());
//        Assertions.assertEquals(1, response.getMenus().size());
//    }

}