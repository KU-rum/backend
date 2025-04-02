package ku_rum.backend.global.batch;

import ku_rum.backend.domain.notice.application.ViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final ViewCountService viewCountService;

    @Scheduled(fixedRate = 90000) // 1.5분마다 실행
    public void syncViewCountsToDatabaseScheduled() {
        viewCountService.syncViewCountsToDatabase();
    }
}

