package ku_rum.backend.integration.domain.position.config;

import ku_rum.backend.domain.place.domain.repository.PlaceRepository;
import ku_rum.backend.integration.domain.position.data.PositionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PositionDataConfig {

    @Autowired
    PlaceRepository placeRepository;

    @Bean
    public PositionData positionData() {
        return new PositionData(placeRepository);
    }
}
