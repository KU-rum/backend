package ku_rum.backend.global.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final BatchConfig batchConfig;
    private final JobExplorer jobExplorer;

    @PostConstruct // 애플리케이션 실행 후 한 번 실행
    public void runJobOnStartup() {
        log.info("🚀 애플리케이션 실행 직후 배치 실행을 시도합니다.");

        // 기존 Job 실행 중인지 확인
        if (isJobRunning("noticeCrawlingJob")) {
            log.warn("⚠️ noticeCrawlingJob이 이미 실행 중이므로 실행하지 않습니다.");
            return;
        }

        runJob();
    }

    @Async
    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시마다 실행
    public void runJob() {
        if (isJobRunning("noticeCrawlingJob")) {
            log.warn("⚠️ noticeCrawlingJob이 이미 실행 중이므로 실행하지 않습니다.");
            return;
        }

        Map<String, JobParameter<?>> confMap = new HashMap<>();
        confMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class)); // ✅ 고유한 값 추가
        confMap.put("runId", new JobParameter<>(UUID.randomUUID().toString(), String.class)); // ✅ 고유한 값 추가

        JobParameters jobParameters = new JobParameters(confMap);
        try {
            jobLauncher.run(batchConfig.job(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException | org.springframework.batch.core.repository.JobRestartException e) {
            log.error("❌ Job 실행 중 오류 발생: {}", e.getMessage());
        }
    }

    private boolean isJobRunning(String jobName) {
        Collection<JobExecution> runningExecutions = jobExplorer.findRunningJobExecutions(jobName);

        for (JobExecution execution : runningExecutions) {
            if (execution.getStatus() == BatchStatus.STARTED || execution.getStatus() == BatchStatus.STARTING) {
                return true;
            }
        }
        return false;
    }



}
