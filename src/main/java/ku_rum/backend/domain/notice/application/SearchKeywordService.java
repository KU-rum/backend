package ku_rum.backend.domain.notice.application;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.notice.domain.SearchKeyword;
import ku_rum.backend.domain.notice.domain.repository.SearchKeywordRepository;
import ku_rum.backend.domain.notice.dto.request.SaveKeywordRequest;
import ku_rum.backend.domain.notice.dto.response.GetKeywordResponse;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchKeywordService {

    private final SearchKeywordRepository searchKeywordRepository;
    private final UserService userService;

    public GetKeywordResponse getKeyword(CustomUserDetails userDetail) {
        User user = userService.getUser();
        List<SearchKeyword> searchKeywords = searchKeywordRepository.findByUser(user);
        List<String> keywords = searchKeywords.stream()
                .map(SearchKeyword::getKeyword)
                .toList();
        return new GetKeywordResponse(keywords);
    }

    public void save(SaveKeywordRequest request, CustomUserDetails userDetail) {
        User user = userService.getUser();
        String keyword = request.keyword();
        Optional<SearchKeyword> searchKeywordOptional = searchKeywordRepository.findByUserAndKeyword(user, keyword);

        if(searchKeywordOptional.isEmpty()){
            SearchKeyword searchKeyword = SearchKeyword.builder()
                    .keyword(keyword)
                    .user(user)
                    .build();
            searchKeywordRepository.save(searchKeyword);
            return
        }

        SearchKeyword searchKeyword = searchKeywordOptional.get();
        searchKeywordRepository.delete(searchKeyword);
    }
}
