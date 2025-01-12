package ku_rum.backend.global.config.dataInsert;

import ku_rum.backend.domain.category.domain.Category;
import ku_rum.backend.domain.menu.domain.Menu;

import java.util.ArrayList;

public class MenuInitializer {
  public static ArrayList<Menu> initializer(ArrayList<Category> categories) {
    ArrayList<Menu> menus = new ArrayList<>();;

    //학생식당 메뉴
    menus.add(
            Menu.of("마라탕",4000L,"NONE",categories.get(4))
    );
    menus.add(
            Menu.of("쌀국수",4500L,"NONE",categories.get(4))
    );
    menus.add(
            Menu.of("순대",5000L,"NONE",categories.get(4))
    );
    menus.add(
            Menu.of("소금삼겹",4500L,"NONE",categories.get(4))
    );
    menus.add(
            Menu.of("삼겹 양념구이",4500L,"NONE",categories.get(4))
    );
    menus.add(
            Menu.of("참치마요",3000L,"NONE",categories.get(4))
    );
    menus.add(
            Menu.of("황소라면",3000L,"NONE",categories.get(4))
    );
    menus.add(
            Menu.of("건대 떡볶이",4300L,"NONE",categories.get(4))
    );
    menus.add(
            Menu.of("카레라이스",4600L,"NONE",categories.get(4))
    );

    return menus;
  }
}
