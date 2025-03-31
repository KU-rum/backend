package ku_rum.backend.domain.notice.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ku_rum.backend.global.exception.notice.InvalidViewCountException;
import lombok.NoArgsConstructor;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.BELOW_ZERO;

@Embeddable
@NoArgsConstructor
public class ViewCount {

    @Column(name = "view_count", nullable = false)
    private long count;

    public ViewCount(long count){
        if (count < 0){
            throw new InvalidViewCountException(BELOW_ZERO);
        }
        this.count = count;
    }

    public ViewCount increment() {
        return new ViewCount(count+1);
    }

    public long getCount(){
        return count;
    }
}
