package ku_rum.backend.global.config.dataInsert;

import ku_rum.backend.domain.building.domain.Building;
import ku_rum.backend.domain.building.domain.BuildingCategory;
import ku_rum.backend.domain.category.domain.Category;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

@Component
public class BuildingCategoryInitializer {
  public static ArrayList<BuildingCategory> initialize(ArrayList<Building> buildings, ArrayList<Category> categories) {
    ArrayList<BuildingCategory> buildingCategories = new ArrayList<>();

    buildingCategories.add(
            BuildingCategory.of(buildings.get(0), categories.get(1)) //Category, Building을 매핑시키는 더 나은방법..?
    );
    buildingCategories.add(
            BuildingCategory.of(buildings.get(0), categories.get(2))
    );
    buildingCategories.add(
            BuildingCategory.of(buildings.get(0), categories.get(2))
    );

    //더 추가 예정

    return buildingCategories;


  }
}
