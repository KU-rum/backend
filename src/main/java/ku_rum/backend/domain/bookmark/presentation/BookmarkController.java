package ku_rum.backend.domain.bookmark.presentation;

import jakarta.validation.Valid;
import java.util.List;
import ku_rum.backend.domain.bookmark.application.BookmarkService;
import ku_rum.backend.domain.bookmark.dto.request.CreateBookmarkRequest;
import ku_rum.backend.domain.bookmark.dto.response.CreateBookmarkResponse;
import ku_rum.backend.domain.bookmark.dto.response.GetBookmarkResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public BaseResponse<CreateBookmarkResponse> createBookmark(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @RequestBody @Valid CreateBookmarkRequest request) {
        CreateBookmarkResponse response = bookmarkService.createBookmark(userDetails, request);
        return BaseResponse.ok(response);
    }

    @GetMapping
    public BaseResponse<List<GetBookmarkResponse>> getBookmark(
            @AuthenticationPrincipal final CustomUserDetails userDetails) {

        List<GetBookmarkResponse> response = bookmarkService.getBookmark(userDetails);
        return BaseResponse.ok(response);
    }

    @DeleteMapping("/{bookmarkId}")
    public BaseResponse<Void> deleteBookmark(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @PathVariable("bookmarkId") Long bookmarkId) {
        bookmarkService.deleteBookmark(userDetails, bookmarkId);
        return BaseResponse.ok();
    }
}
