package ku_rum.backend.domain.place.application;

import static java.time.Duration.between;
import static ku_rum.backend.domain.place.application.PositionService.CRITERION_TIME;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import ku_rum.backend.domain.place.domain.Position;
import ku_rum.backend.domain.place.domain.repository.PositionRepository;
import ku_rum.backend.domain.rank.domain.repository.PlaceRankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PositionScheduler {

    private final PositionRepository positionRepository;
    private final PlaceRankRepository placeRankRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetPosition() {
        List<Long> positions = positionRepository.findAll().stream()
                .filter(this::isUpperBound)
                .map(Position::getPositionId)
                .toList();
        placeRankRepository.updatePlaceRankByPositions(positions);

        positionRepository.deleteAll();
    }

    boolean isUpperBound(Position position) {
        Duration minusTime = between(LocalDateTime.now(), position.getCreatedAt());
        return minusTime.getSeconds() >= CRITERION_TIME;
    }
}
