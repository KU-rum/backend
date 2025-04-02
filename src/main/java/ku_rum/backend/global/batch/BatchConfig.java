package ku_rum.backend.global.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final CrawlingStep crawlingStep;
    private final SetRedisKeyStep setRedisKeyStep;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job job() {
        return new JobBuilder("noticeCrawlingJob", jobRepository)
                .start(crawlingStep.crawling(jobRepository, platformTransactionManager))
                .next(setRedisKeyStep.setRedisKey(jobRepository, platformTransactionManager))
                .build();
    }


}
