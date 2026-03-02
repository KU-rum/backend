package ku_rum.backend.domain.place.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import ku_rum.backend.domain.place.domain.Position;
import ku_rum.backend.domain.place.domain.repository.PositionRepository;
import ku_rum.backend.domain.rank.domain.PlaceRank;
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

    private static final long CRITERION_TIME = 3600L;//1시간

    private final PositionRepository positionRepository;
    private final PlaceRankRepository placeRankRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetPosition() {
        positionRepository.deleteAll();
        log.info("모든 Position 삭제 완료");
    }

    @Scheduled(fixedRate = 120000)//2분
    @Transactional
    public void updatePlaceRanks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusSeconds(CRITERION_TIME);
        LocalDate today = now.toLocalDate();

        List<Position> positions = positionRepository.findByCreatedAtBefore(oneHourAgo);
        for (Position position : positions) {
            updateRank(position, today);
        }
    }

    private void updateRank(Position position, LocalDate today) {

        PlaceRank placeRank = placeRankRepository
                .findByUserAndPlace(position.getUser(), position.getPlace())
                .orElseGet(() -> createNewRank(position, today));

        if (placeRank.canUpdateToday()) {
            placeRank.increaseCount();
            log.info("랭크 증가: userId={}, placeId={}",
                    position.getUser().getId(),
                    position.getPlace().getPlaceId());
        }
    }

    private PlaceRank createNewRank(Position position, LocalDate today) {
        PlaceRank newRank = PlaceRank.builder()
                .count(1)
                .user(position.getUser())
                .place(position.getPlace())
                .lastUpdatedDate(today)
                .build();

        return placeRankRepository.save(newRank);
    }
}
