package ku_rum.backend.domain.category.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ku_rum.backend.domain.category.domain.BuildingCategory;
import ku_rum.backend.domain.category.domain.Category;
import ku_rum.backend.domain.category.domain.QBuildingCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static ku_rum.backend.domain.category.domain.QBuildingCategory.buildingCategory;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BuildingCategoryQueryRepository {
  private final JPAQueryFactory queryFactory;
  @PersistenceContext
  EntityManager entityManager;

  public List<Long> findBuildingCategoryIds(List<Long> categoryIds) {
    QBuildingCategory qBuildingCategory = QBuildingCategory.buildingCategory;

    // categoryIds에 포함된 category_id에 해당하는 building_id를 조회
    return queryFactory
            .select(qBuildingCategory.id)  // building_id를 선택
            .from(qBuildingCategory)
            .where(qBuildingCategory.category.id.in(categoryIds))  // category_id가 categoryIds 목록에 포함된 경우
            .fetch();  // 결과를 List<Long>로 반환
  }

  public List<Long> findBuildingIdsByCategoryIds(List<Long> buildingCategoryIds) {
    return entityManager.createQuery(
                    "SELECT bc.building.id " +
                            "FROM BuildingCategory bc " +
                            "WHERE bc.id IN :buildingCategoryIds", Long.class)
            .setParameter("buildingCategoryIds", buildingCategoryIds)
            .getResultList();
  }

  public List<Long> findByBuildingIds(List<Long> buildingIds) {
    QBuildingCategory qBuildingCategory = buildingCategory;
    List<Long> result = queryFactory
            .select(qBuildingCategory.id)
            .from(qBuildingCategory)
            .where(qBuildingCategory.building.id.in(buildingIds))
            .fetch();
    log.info("result size : {}", result.size());
    return result;
  }

  public Optional<BuildingCategory> findByBuildingId(Long id) {
    String query = "SELECT m FROM BuildingCategory m " +
            "where m.building.id = :id";
    return Optional.ofNullable(entityManager.createQuery(query, BuildingCategory.class)
            .setParameter("id", id)
            .getSingleResult());
  }

  public Optional<BuildingCategory> findByBuildingAndCategoryId(Long buildingId, Long categoryId) {
    return null;
  }

    public List<Category> searchCategoryByNgram(String searchText) {
      return null;
    }
}
