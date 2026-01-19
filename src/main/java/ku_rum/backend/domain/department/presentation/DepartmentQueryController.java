package ku_rum.backend.domain.department.presentation;

import java.util.List;
import ku_rum.backend.domain.department.application.DepartmentQueryService;
import ku_rum.backend.domain.department.dto.CollegeDepartmentResponse;
import ku_rum.backend.domain.department.dto.SearchDepartmentResponse;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentQueryController {

    private final DepartmentQueryService departmentQueryService;

    @GetMapping
    public BaseResponse<CollegeDepartmentResponse> getDepartment(@RequestParam String collegeName) {
        return BaseResponse.ok(departmentQueryService.getDepartmentsByCollege(collegeName));
    }

    /**
     * 학과 검색
     */
    @GetMapping("/search")
    public BaseResponse<List<SearchDepartmentResponse>> searchDepartment(@RequestParam("query") String query) {
        List<SearchDepartmentResponse> response = departmentQueryService.search(query);
        return BaseResponse.ok(response);
    }
}
