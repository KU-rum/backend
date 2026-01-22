package ku_rum.backend.domain.department.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_DEPARTMENT;

import jakarta.transaction.Transactional;
import java.util.List;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.department.domain.UserDepartment;
import ku_rum.backend.domain.department.domain.repository.UserDepartmentRepository;
import ku_rum.backend.domain.department.dto.DepartmentUrlResponse;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.department.NoSuchDepartmentException;
import ku_rum.backend.global.utill.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDepartmentService {
    private final UserDepartmentRepository userDepartmentRepository;
    private final UserUtil userUtil;

    @Transactional
    public void addDeptToUser(User user, Department dept) {
        userDepartmentRepository.save(UserDepartment.of(user, dept));
    }

    @Transactional
    public void deleteDeptFromUser(User user, Department dept) {
        int deleted = userDepartmentRepository.deleteByUserIdAndDepartmentId(user.getId(), dept.getId());
        if (deleted == 0) {
            throw new NoSuchDepartmentException(NO_SUCH_DEPARTMENT);
        }
    }


    public List<DepartmentUrlResponse> getDepartmentUrl() {
        User user = userUtil.getUser();
        List<UserDepartment> userDepartments = userDepartmentRepository.findByUserId(user.getId());

        return userDepartments.stream()
                .map(userDepartment -> {
                    Department department = userDepartment.getDepartment();
                    return new DepartmentUrlResponse(department.getName(), department.getUrl());
                }).toList();
    }
}
