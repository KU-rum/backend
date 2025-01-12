package ku_rum.backend.global.config.dataInsert;

import ku_rum.backend.domain.category.domain.Category;
import ku_rum.backend.domain.category.domain.CategoryType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CategoryInitializer {
  public static ArrayList<Category> initialize() {
    ArrayList<Category> categories = new ArrayList<>();

    categories.add(
            Category.of(CategoryType.CAFE_1894.getText())
    );
    categories.add(
            Category.of(CategoryType.RESTIO.getText())
    );
    categories.add(
            Category.of(CategoryType.CU.getText())
    );
    categories.add(
            Category.of(CategoryType.KCUBE.getText())
    );
    categories.add(
            Category.of(CategoryType.STUDENT_CAFETERIA.getText())
    );
    return categories;
  }
}
