package ku_rum.backend.domain.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import ku_rum.backend.global.support.type.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Polygon;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Place extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Enumerated(value = EnumType.STRING)
    private CategoryChip categoryChip;

    @NotNull
    private String name;

    private String subName;

    private String abbreviation;

    @NotNull
    private String content;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 9)
    private BigDecimal latitude;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 9)
    private BigDecimal longitude;

    @Column(columnDefinition = "POLYGON")
    private Polygon boundary;
}