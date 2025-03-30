package ku_rum.backend.global.exception.notice;

import ku_rum.backend.global.support.status.ResponseStatus;
import lombok.Getter;

@Getter
public class InvalidViewCountException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public InvalidViewCountException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public InvalidViewCountException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}
