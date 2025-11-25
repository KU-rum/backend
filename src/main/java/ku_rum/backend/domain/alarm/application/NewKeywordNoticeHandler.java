package ku_rum.backend.domain.alarm.application;

import java.util.Map.Entry;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.SearchKeyword;
import org.springframework.stereotype.Component;

@Component
public class NewKeywordNoticeHandler implements AlarmMessageHandler {
    @Override
    public String create(Object payload) {
        Entry<SearchKeyword, Notice> entry = (Entry<SearchKeyword, Notice>) payload;
        String keyword = entry.getKey().getKeyword();
        return String.format("%s에 대한 공지가 올라왔어요.", keyword);
    }
}
