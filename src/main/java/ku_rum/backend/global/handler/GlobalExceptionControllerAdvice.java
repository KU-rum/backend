package ku_rum.backend.global.handler;

import com.github.dockerjava.api.exception.BadRequestException;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import jakarta.validation.ConstraintViolationException;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.support.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.*;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public BaseErrorResponse handleServerException(final Exception e) {
        log.error("[handle InternalServerException]", e);
        return new BaseErrorResponse(SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, NoHandlerFoundException.class, TypeMismatchException.class})
    public BaseErrorResponse handleBadRequest(final Exception e) {
        log.error("[handle BadRequest]", e);
        return new BaseErrorResponse(URL_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseErrorResponse handle_HttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.error("[handle_HttpRequestMethodNotSupportedException]", e);
        return new BaseErrorResponse(METHOD_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseErrorResponse handle_ConstraintViolationException(final ConstraintViolationException e) {
        log.error("[handle_ConstraintViolationException]", e);
        return new BaseErrorResponse(BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerErrorException.class)
    public BaseErrorResponse handle_InternalServerError(final InternalServerErrorException e) {
        log.error("[handle_InternalServerError]", e);
        return new BaseErrorResponse(INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public BaseErrorResponse handle_AccessDeniedException(final AccessDeniedException e) {
        log.error("[handle_AccessDeniedException]", e);
        return new BaseErrorResponse(ACCESS_DENIED);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public BaseErrorResponse handle_RuntimeException(final Exception e) {
        log.error("[handle_RuntimeException]", e);
        return new BaseErrorResponse(INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<BaseErrorResponse> MethodArgumentNotValidExceptionHandler(final MethodArgumentNotValidException e) {
        List<BaseErrorResponse> responses = new ArrayList<>();
        for (FieldError error : e.getFieldErrors()) {
            log.error("errorFieldName : {}, error message : {}", error.getField(), error.getDefaultMessage());
            BaseErrorResponse baseErrorResponse = new BaseErrorResponse(FIELD_ERROR.getStatus(), error.getDefaultMessage());
            responses.add(baseErrorResponse);
        }
        return responses;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public BaseErrorResponse MethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        String errorMessage = METHOD_ARGUMENT_ERROR.getMessage();
        log.error("errorFieldName : {}, errorMessage : {}", errorMessage, errorMessage);

        return new BaseErrorResponse(METHOD_ARGUMENT_ERROR.getStatus(), errorMessage);
    }

    @ExceptionHandler(GlobalException.class)
    public BaseErrorResponse handleGlobalException(final GlobalException e) {
        log.error("errorCode : {}, errorMessage : {}", e.getStatus().getCode(), e.getStatus().getMessage());

        return new BaseErrorResponse(e.getStatus());
    }
}
