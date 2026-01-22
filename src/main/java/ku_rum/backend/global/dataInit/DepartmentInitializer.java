package ku_rum.backend.global.dataInit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import ku_rum.backend.domain.college.domain.College;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.department.domain.DepartmentType;
import org.springframework.stereotype.Component;

@Component
public class DepartmentInitializer {
    public static List<Department> initialize(List<College> colleges) {
        Map<String, College> collegeMap = colleges.stream()
                .collect(Collectors.toMap(College::getName, Function.identity()));

        List<Department> departments = new ArrayList<>();

        // 공과대학
        College 공과대학 = collegeMap.get("공과대학");
        addDepartmentsToBuilding(departments, 공과대학,
                DepartmentType.컴퓨터공학부,
                DepartmentType.사회환경공학부,
                DepartmentType.기계공학부,
                DepartmentType.전기전자공학부,
                DepartmentType.화학공학부,
                DepartmentType.신산업융합학과,
                DepartmentType.K뷰티산업융합학과,
                DepartmentType.항공우주정보시스템공학과,
                DepartmentType.생물공학과,
                DepartmentType.산업공학과
        );

        // 문과대학
        College 문과대학 = collegeMap.get("문과대학");
        addDepartmentsToBuilding(departments, 문과대학,
                DepartmentType.국어국문학과,
                DepartmentType.영어영문학과,
                DepartmentType.중어중문학과,
                DepartmentType.철학과,
                DepartmentType.사학과,
                DepartmentType.지리학과,
                DepartmentType.미디어커뮤니케이션학과,
                DepartmentType.문화콘텐츠학과,
                DepartmentType.휴먼ICT연계전공,
                DepartmentType.글로벌MICE연계전공,
                DepartmentType.인문상담치유연계전공,
                DepartmentType.통일인문교육연계전공
        );

        // 사회과학대학	
        College 사회과학대학 = collegeMap.get("사회과학대학");
        addDepartmentsToBuilding(departments, 사회과학대학,
                DepartmentType.정치외교학과,
                DepartmentType.경제학과,
                DepartmentType.행정학과,
                DepartmentType.국제무역학과,
                DepartmentType.응용통계학과,
                DepartmentType.융합인재학과,
                DepartmentType.글로벌비즈니스학과
        );

        // 경영대학 & 부동산과학원
        College 경영대학 = collegeMap.get("경영대학");
        addDepartmentsToBuilding(departments, 경영대학,
                DepartmentType.경영학과,
                DepartmentType.기술경영학과
        );
        College 부동산과학원 = collegeMap.get("부동산과학원");
        addDepartmentsToBuilding(departments, 부동산과학원,
                DepartmentType.부동산학과
        );

        // KU융합과학기술원 (새천년관)	

        College KU융합과학기술원 = collegeMap.get("KU융합과학기술원");
        addDepartmentsToBuilding(departments, KU융합과학기술원,
                DepartmentType.자유전공학부,
                DepartmentType.미래에너지공학과,
                DepartmentType.화장품공학과,
                DepartmentType.줄기세포재생공학과,
                DepartmentType.의생명공학과,
                DepartmentType.시스템생명공학과,
                DepartmentType.융합생명공학과
        );

        // 상허생명과학대학 (생명과학관)
        College 상허생명과학대학 = collegeMap.get("상허생명과학대학");
        addDepartmentsToBuilding(departments, 상허생명과학대학,
                DepartmentType.생명과학특성학과,
                DepartmentType.식량자원과학과,
                DepartmentType.식품유통공학과,
                DepartmentType.환경보건과학과,
                DepartmentType.산림조경학과
        );

        // 수의과대학 (동물생명과학관)
        College 수의과대학 = collegeMap.get("수의과대학");
        addDepartmentsToBuilding(departments, 수의과대학,
                DepartmentType.동물자원과학과,
                DepartmentType.축산식품생명공학과
        );

        return departments;
    }

    /**
     * 여러 학과를 같은 건물, 같은 단과대학으로 한 번에 추가
     */
    private static void addDepartmentsToBuilding(List<Department> departments,
                                                 College college,
                                                 DepartmentType... departmentTypes) {
        Arrays.stream(departmentTypes)
                .forEach(type -> departments.add(
                        // 한글명으로 Department.name 설정	
                        Department.of(type.name(), college, type.getUrl())
                ));
    }
}	
