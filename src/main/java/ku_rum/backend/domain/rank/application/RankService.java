package ku_rum.backend.domain.rank.application;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.rank.application.response.GetPlaceUserRankResponse;
import ku_rum.backend.domain.rank.domain.PlaceRank;
import ku_rum.backend.domain.rank.domain.repository.PlaceRankRepository;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankService {

    private final PlaceRankRepository placeRankRepository;
    private final UserService userService;

    /**
     * 유저 장소 공유 랭킹 조회(3개)
     *
     * @param userDetails
     * @return
     */
    public List<GetPlaceUserRankResponse> getPlaceUserRank(final CustomUserDetails userDetails) {
        User user = userService.getUser();
        return placeRankRepository.findTop3ByUserOrderByCountDesc(user).stream()
                .map(GetPlaceUserRankResponse::from)
                .toList();
    }

    /**
     * 유저 장소 랭킹 업데이트
     *
     * @param user
     * @param place
     */
    @Transactional
    public void updateRank(User user, Place place) {
        Optional<PlaceRank> optionalRank = placeRankRepository.findByUserAndPlace(user, place);
        if (optionalRank.isPresent()) {
            optionalRank.get().increaseCount();
            return;
        }

        PlaceRank rank = PlaceRank.builder()
                .count(1)
                .user(user)
                .place(place)
                .build();
        placeRankRepository.save(rank);
    }

    public PlaceRank getUserPlaceRank(User user, Place place) {
        return placeRankRepository.findByUserAndPlace(user, place).orElseThrow(() -> new GlobalException(
                BaseExceptionResponseStatus.PLACE_RANK_NOT_FOUND));
    }
}
