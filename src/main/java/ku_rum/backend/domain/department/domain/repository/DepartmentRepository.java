package ku_rum.backend.domain.department.domain.repository;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.department.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);

    Optional<Department> findFirstByName(String name);

    List<Department> findAllByCollege_Name(String college);

    List<Department> searchDepartmentByName(String name);
}