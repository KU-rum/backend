package ku_rum.backend.domain.notice.application;

import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeCategory;
import ku_rum.backend.domain.notice.domain.NoticeStatus;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ku_rum.backend.domain.notice.application.NoticeRedisKey.NOTICE_REDIS_KEY_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

    @Qualifier("urlRedisTemplate")
    private final RedisTemplate<String, String> urlRedisTemplate;

    @Transactional
    public List<Notice> crawlAndSaveKonkukNotices() {
        WebDriver driver = null;
        List<Notice> notices = new ArrayList<>();
        final int MAX_NOTICE_COUNT = 10;

        try {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--no-sandbox");
            driver = new ChromeDriver(chromeOptions);

            for (NoticeCategory category : NoticeCategory.values()) {
                String url = category.getUrl();
                driver.get(url);
                log.info("크롤링 시작: {}", category.getUrl());

                boolean continueCrawling = true;
                while (continueCrawling) {
                    if (notices.size() >= MAX_NOTICE_COUNT) {  // ✅ 최대 공지 개수 초과하면 중단
                        log.info("🔹 최대 공지 개수({}) 초과, 크롤링 중단", MAX_NOTICE_COUNT);
                        return notices;
                    }

                    continueCrawling = goToNextButton(category, driver, crawlAndSave(category, driver, notices));
                }
            }
        } finally {
            driver.quit();
        }

        return notices;
    }

    private boolean crawlAndSave(NoticeCategory category, WebDriver driver, List<Notice> notices) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 페이지가 완전히 로드될 때까지 대기
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(category.getSelector())));

        List<WebElement> noticeList = driver.findElements(By.cssSelector(category.getSelector()));

        if (noticeList.isEmpty()) {
            log.warn("공지사항을 찾지 못했습니다: {}", category.getUrl());
            return false;
        }

        for (WebElement noticeElement : noticeList) {
            try {
                String title = noticeElement.findElement(By.cssSelector("td.td-subject > a > strong")).getText();
                String link = noticeElement.findElement(By.cssSelector("td.td-subject a")).getAttribute("href");
                String date = noticeElement.findElement(By.cssSelector("td.td-date")).getText();

                //취창업 탭에 한하여 URL 파싱
                //javascript:jf_combBbs_view('konkuk','5','4214','1141055'); -> https://www.konkuk.ac.kr/bbs/job/4214/1141055/artclView.do
                if (category == NoticeCategory.STARTUP && link.startsWith("javascript")) {
                    String[] split = link.split(",");
                    String s = split[2].replace("'", "") + "/" + split[3].replace("'", "").replace(");", "");

                    link = "https://www.konkuk.ac.kr/bbs/job/" + s + "/artclView.do";
                }

                // Redis에 이미 저장된 URL인지 확인
                String redisKey = NOTICE_REDIS_KEY_PREFIX.getPrefix() + link;
                if (Boolean.TRUE.equals(urlRedisTemplate.hasKey(redisKey))) {
//                    log.info("이미 저장된 공지사항: {}", link);
                    continue;
                }

                // 새 공지사항이면 데이터베이스에 저장
                Notice notice = Notice.of(title, link, date, category, isImportantNotice(noticeElement) ? NoticeStatus.IMPORTANT : NoticeStatus.GENERAL);

                log.info("새로운 공지사항: {}", title);
                notices.add(notice);

            } catch (Exception e) {
                log.error("공지사항 저장 중 오류 발생", e);
            }
        }
        return true;
    }



    private boolean goToNextButton(NoticeCategory category, WebDriver driver, boolean continueCrawling) {
        try {
            WebElement nextButton = driver.findElements(By.cssSelector(category.getNextButtonSelector()))
                    .stream()
                    .filter(WebElement::isDisplayed)
                    .filter(WebElement::isEnabled)
                    .findFirst()
                    .orElse(null);

            if (nextButton != null) {
                nextButton.click();
                // 페이지 로드 대기
                Thread.sleep(800);
            } else {
                continueCrawling = false;
            }
        } catch (Exception e) {
            continueCrawling = false;
            e.printStackTrace();
        }
        return continueCrawling;
    }

    private boolean isImportantNotice(WebElement noticeElement) {
        try {
            WebElement importantSpan = noticeElement.findElement(By.cssSelector("td.td-num > span"));
            log.info(importantSpan.getText());
            return importantSpan != null;
        } catch (Exception e) {
            return false; // span이 없으면 일반 공지로 간주
        }
    }
}
