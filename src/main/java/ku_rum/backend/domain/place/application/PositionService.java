package ku_rum.backend.domain.place.application;

import static java.time.Duration.between;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_DEPARTMENT;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.PLACE_NOT_FOUND;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import ku_rum.backend.domain.place.application.response.CurrentPositionConfirmResponse;
import ku_rum.backend.domain.place.application.response.CurrentPositionResponse;
import ku_rum.backend.domain.place.application.response.CurrentPositionStatusResponse;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.Position;
import ku_rum.backend.domain.place.domain.repository.PlaceRepository;
import ku_rum.backend.domain.place.domain.repository.PositionRepository;
import ku_rum.backend.domain.place.dto.request.CurrentPositionConfirmRequest;
import ku_rum.backend.domain.place.dto.request.CurrentPositionRequest;
import ku_rum.backend.domain.rank.application.RankService;
import ku_rum.backend.domain.rank.domain.PlaceRank;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;
    private final PlaceRepository placeRepository;
    private final UserService userService;
    private final RankService rankService;

    public static final long CRITERION_TIME = 3600L;

    /**
     * 사용자 위치 공유 여부 확인
     *
     * @param userDetails 사용자 인증 정보
     * @return response 객체
     */
    public CurrentPositionStatusResponse getCurrentPositionStatus(CustomUserDetails userDetails) {
        User user = userService.getUser();
        Optional<Position> positionOptional = positionRepository.findPositionByUser(user);

        if (positionOptional.isEmpty()) {
            return new CurrentPositionStatusResponse(false, null);
        }
        String placeName = positionOptional.get().getPlace().getName();
        return new CurrentPositionStatusResponse(true, placeName);
    }

    /**
     * 사용자 위치값 반환 가장 근접한 건물 반환
     *
     * @param userDetails 사용자 인증정보
     * @param request     request 객체
     * @return response 객체
     */
    public CurrentPositionResponse getCurrentPosition(CustomUserDetails userDetails,
                                                      CurrentPositionRequest request) {
        Place findPlace = placeRepository.findNearestPlace(request.latitude(),
                request.longitude());
        return new CurrentPositionResponse(findPlace.getName());
    }

    /**
     * 사용자 위치 값 저장 이미 있는 경우 업데이트
     *
     * @param userDetails 사용자 인증정보
     * @param request     request 객체
     * @return response 객체
     */
    @Transactional
    public CurrentPositionConfirmResponse confirmCurrentPosition(CustomUserDetails userDetails,
                                                                 CurrentPositionConfirmRequest request) {
        User user = userService.getUser();
        Optional<Position> positionOptional = positionRepository.findPositionByUser(user);
        Place place = findOneByName(request.placeName());

        if (positionOptional.isPresent()) {
            return updatePosition(positionOptional.get(), place);
        }

        Position position = Position.of(user, place);
        Position savePosition = positionRepository.save(position);

        return new CurrentPositionConfirmResponse(savePosition.getPlace().getName());
    }

    /**
     * 공유 취소
     *
     * @param userDetails 사용자 인증정보
     */
    public void disableSharingPosition(CustomUserDetails userDetails) {
        User user = userService.getUser();
        Position position = positionRepository.findPositionByUser(user)
                .orElseThrow(() -> new GlobalException(NO_SUCH_DEPARTMENT));
        positionRepository.deleteByUser(user);

        Duration minusTime = between(position.getCreatedAt(), LocalDateTime.now());
        boolean isUpperBound = minusTime.getSeconds() >= CRITERION_TIME;

        try {
            PlaceRank userPlaceRank = rankService.getUserPlaceRank(user, position.getPlace());
            if (isUpperBound) {
                userPlaceRank.increaseCount();
            }
        } catch (Exception ignored) {

        }
    }

    /**
     * place 조회
     *
     * @param name 이름(건물, k-cube ...)
     * @return place 엔티티
     */
    private Place findOneByName(String name) {
        return placeRepository.findOneByName(name)
                .orElseThrow(() -> new GlobalException(PLACE_NOT_FOUND));
    }

    /**
     * 위치값 수정
     *
     * @param position
     * @param place
     * @return
     */
    private CurrentPositionConfirmResponse updatePosition(Position position, Place place) {
        position.update(place);
        return new CurrentPositionConfirmResponse(position.getPlace().getName());
    }
}