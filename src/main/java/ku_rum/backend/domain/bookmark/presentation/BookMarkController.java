package ku_rum.backend.domain.bookmark.presentation;

import ku_rum.backend.global.support.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookmark")
public class BookMarkController {

    @PostMapping
    public BaseResponse<Void> createBookMark() {

        return BaseResponse.ok();
    }

    @GetMapping
    public BaseResponse<Void> getBookMark() {

        return BaseResponse.ok();
    }
}
