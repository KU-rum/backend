package ku_rum.backend.domain.bookmark.presentation;

import ku_rum.backend.global.support.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookmark")
public class BookmarkController {

    @PostMapping
    public BaseResponse<Void> createBookmark() {

        return BaseResponse.ok();
    }

    @GetMapping
    public BaseResponse<Void> getBookmark() {

        return BaseResponse.ok();
    }
}
