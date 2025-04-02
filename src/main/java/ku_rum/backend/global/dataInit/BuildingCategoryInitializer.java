package ku_rum.backend.global.dataInit;

import ku_rum.backend.domain.building.domain.Building;
import ku_rum.backend.domain.category.domain.BuildingCategory;
import ku_rum.backend.domain.category.domain.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class BuildingCategoryInitializer {
    public static ArrayList<BuildingCategory> initialize(ArrayList<Building> buildings, ArrayList<Category> categories) {
        ArrayList<BuildingCategory> buildingCategories = new ArrayList<>();

        //빌딩
        Building 레스티오_동생대점 = buildings.get(27 - 1);
        Building 레스티오_공대점 = buildings.get(28 - 1);
        Building 카페_1984_학생회관점 = buildings.get(29 - 1);
        Building 카페_1984_도서관점 = buildings.get(30 - 1);
        Building CU_학생회관점 = buildings.get(31 - 1);
        Building CU_도서관점 = buildings.get(32 - 1);
        Building KCUBE_공학관 = buildings.get(36 - 1);


        //카테고리
        Category 레스티오 = categories.get(2 - 1);
        Category 카페_1984 = categories.get(1 - 1);
        Category cu = categories.get(3 - 1);
        Category KCUBE = categories.get(4 - 1);
        Category KHUB = categories.get(5 - 1);
        Category 학생식당 = categories.get(6 - 1);

        //레스티오 추가
        buildingCategories.add(
                BuildingCategory.of(레스티오_동생대점, 레스티오)
        );
        buildingCategories.add(
                BuildingCategory.of(레스티오_공대점, 레스티오)
        );

        //카페_1984 추가
        buildingCategories.add(
                BuildingCategory.of(카페_1984_학생회관점, 카페_1984)
        );
        buildingCategories.add(
                BuildingCategory.of(카페_1984_도서관점, 카페_1984)
        );

        //cu 추가
        buildingCategories.add(
                BuildingCategory.of(CU_학생회관점, cu)
        );
        buildingCategories.add(
                BuildingCategory.of(CU_도서관점, cu)
        );

        //KCUBE 추가
        buildingCategories.add(
                BuildingCategory.of(KCUBE_공학관, KCUBE)
        );


        return buildingCategories;
    }
}