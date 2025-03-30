package ku_rum.backend.global.handler;

import jakarta.annotation.Priority;
import ku_rum.backend.global.exception.notice.InvalidPageException;
import ku_rum.backend.global.exception.notice.InvalidViewCountException;
import ku_rum.backend.global.exception.notice.NoSuchNoticeException;
import ku_rum.backend.global.support.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.INVALID_PAGE;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_NOTICE;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class NoticeExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchNoticeException.class)
    public BaseErrorResponse handleNoSuchNoticeException(final NoSuchNoticeException e) {
        log.error("[NoSuchNoticeException]");
        return new BaseErrorResponse(NO_SUCH_NOTICE);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPageException.class)
    public BaseErrorResponse handleInvalidPageException(final InvalidPageException e) {
        log.error("[InvalidPageException]");
        return new BaseErrorResponse(INVALID_PAGE);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidViewCountException.class)
    public BaseErrorResponse handleInvalidViewCountException(final InvalidPageException e) {
        log.error("[InvalidViewCountException]");
        return new BaseErrorResponse(INVALID_PAGE);
    }
}
