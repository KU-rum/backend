package ku_rum.backend.domain.place.util;

import java.math.BigDecimal;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class PointParser {

    private static final int SRID = 4326;
    private static final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), SRID);

    /**
     * 위도/경도를 기반으로 JTS Point 객체 생성
     *
     * @param longitude 경도 (x)
     * @param latitude  위도 (y)
     * @return
     */
    public static Point toPoint(double longitude, double latitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(SRID);
        return point;
    }

    /**
     * 경도/위도를 기반으로 WKT 문자열 생성
     *
     * @param longitude 경도 (x)
     * @param latitude  위도 (y)
     * @return
     */
    public static String toPointString(BigDecimal latitude, BigDecimal longitude) {
        return String.format("POINT(%f %f)", latitude, longitude);
    }

}