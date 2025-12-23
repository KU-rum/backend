package ku_rum.backend.domain.search.presentation;

import ku_rum.backend.domain.search.application.RecentSearchService;
import ku_rum.backend.domain.search.domain.RecentSearch;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recent-searches")
@RequiredArgsConstructor
public class RecentSearchController {

    private final RecentSearchService recentSearchService;

    @GetMapping
    public BaseResponse<List<RecentSearch>> list(
            @RequestParam(defaultValue = "20") int limit) {
        return BaseResponse.ok(recentSearchService.list(limit));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable Long id) {
        recentSearchService.delete(id);
        return BaseResponse.ok(null);
    }

    @DeleteMapping("/all")
    public BaseResponse<Void> deleteAll() {
        recentSearchService.deleteAll();
        return BaseResponse.ok(null);
    }
}
