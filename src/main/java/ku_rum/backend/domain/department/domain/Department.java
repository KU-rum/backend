package ku_rum.backend.domain.department.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import ku_rum.backend.domain.college.domain.College;
import ku_rum.backend.global.support.type.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    private College college;

    private String url;

    @Builder
    private Department(String name, College college, String url) {
        this.name = name;
        this.college = college;
        this.url = url;
    }

    public static Department of(String name, College college, String url) {
        return Department.builder()
                .name(name)
                .college(college)
                .url(url)
                .build();
    }
}
