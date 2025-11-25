package ku_rum.backend.domain.notice.presentation;

import ku_rum.backend.domain.notice.application.SearchKeywordService;
import ku_rum.backend.domain.notice.dto.request.SaveKeywordRequest;
import ku_rum.backend.domain.notice.dto.response.GetKeywordResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class KeywordController {
    private final SearchKeywordService searchKeywordService;

    @GetMapping("/keyword")
    public BaseResponse<GetKeywordResponse> getKeywords(
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        return BaseResponse.ok(searchKeywordService.getKeyword(userDetails));
    }

    @PostMapping("/keyword")
    public BaseResponse<Void> updateKeyword(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @RequestBody SaveKeywordRequest request) {
        searchKeywordService.update(request, userDetails);
        return BaseResponse.ok();
    }
}
