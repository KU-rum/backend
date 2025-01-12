package ku_rum.backend.domain.menu.domain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ku_rum.backend.domain.menu.response.MenuSimpleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MenuQueryRepository {

  @PersistenceContext
  EntityManager entityManager;

  public Optional<List<MenuSimpleResponse>> findAllByCategoryId(Long id) {
    String query = "SELECT new ku_rum.backend.domain.menu.response.MenuSimpleResponse(" +
            "m.name, m.price, m.imageUrl) " +
            "FROM Menu m " +
            "JOIN m.category c " +
            "WHERE c.id = :id";

    return Optional.ofNullable(entityManager.createQuery(query, MenuSimpleResponse.class)
            .setParameter("id", id)
            .getResultList());
  }
}
