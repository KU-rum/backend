package ku_rum.backend.integration.domain.position.data;

import java.math.BigDecimal;
import ku_rum.backend.domain.place.domain.CategoryChip;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.repository.PlaceRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.transaction.annotation.Transactional;

public class PositionData {

    PlaceRepository placeRepository;

    public PositionData(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;

    }

    @Transactional
    public void saveGeometryPlaceData() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(127.078731, 37.542141),
                new Coordinate(127.079081, 37.542090),
                new Coordinate(127.078873, 37.541181),
                new Coordinate(127.078584, 37.541170),
                new Coordinate(127.078731, 37.542141)
        };

        LinearRing shell = geometryFactory.createLinearRing(coordinates);
        Polygon polygon = geometryFactory.createPolygon(shell, null);

        Place place1 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("공학관")
                .subName("공학관입니다.")
                .abbreviation("공학관 A,B,C,D동이 있습니다.")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .boundary(polygon)
                .build();

        placeRepository.save(place1);
    }

    @Transactional
    public void afterEach() {
        placeRepository.deleteAll();
        placeRepository.flush();
    }
}
