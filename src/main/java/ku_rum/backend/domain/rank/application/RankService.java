package ku_rum.backend.domain.rank.application;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import ku_rum.backend.domain.alarm.application.AlarmService;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.friend.application.FriendQueryService;
import ku_rum.backend.domain.place.application.RankingChangeDto;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.rank.application.response.GetPlaceRankPaginationResponse;
import ku_rum.backend.domain.rank.application.response.GetPlaceRankResponse;
import ku_rum.backend.domain.rank.application.response.GetPlaceTopRankResponse;
import ku_rum.backend.domain.rank.application.response.GetPlaceUserRankResponse;
import ku_rum.backend.domain.rank.application.response.PlaceUserRankResponse;
import ku_rum.backend.domain.rank.domain.PlaceRank;
import ku_rum.backend.domain.rank.domain.repository.PlaceRankRepository;
import ku_rum.backend.domain.rank.dto.PlaceRankWithRankingProjection;
import ku_rum.backend.domain.rank.dto.PlaceRankingLastKnownCursor;
import ku_rum.backend.domain.rank.dto.request.PlaceRankPaginationRequest;
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
    private final FriendQueryService friendQueryService;
    private final AlarmService alarmService;

    private static final int MIN_RANK = 1;
    private static final int TOP_3_START = 1;
    private static final int TOP_3_END = 3;

    /**
     * 유저 장소 공유 랭킹 조회(3개)
     *
     * @param userDetails
     * @return
     */
    public List<GetPlaceUserRankResponse> getPlaceUserRank(final CustomUserDetails userDetails) {
        User user = userService.getUser();
        List<PlaceRank> PlaceRanks = placeRankRepository.findTop3RanksWithTiesByUser(user.getId());

        Map<Integer, List<PlaceRank>> placeRanksGroupedByCount = PlaceRanks.stream()
                .collect(Collectors.groupingBy(
                        PlaceRank::getCount,
                        () -> new TreeMap<>(Comparator.reverseOrder()),
                        toList())
                );

        return placeRanksGroupedByCount.values()
                .stream()
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

    public GetPlaceRankResponse getUserPlaceRank(Long userId, Long placeId) {
        Optional<PlaceRankWithRankingProjection> rankByPlaceAndUser = placeRankRepository.findRankByPlaceAndUser(
                placeId,
                userId);
        if (rankByPlaceAndUser.isEmpty()) {
            User user = userService.getUser();
            return GetPlaceRankResponse.emptyFrom(user);
        }
        return GetPlaceRankResponse.from(rankByPlaceAndUser.get());
    }

    /**
     * 장소 전체 유저공유 랭킹 조회(3개)
     *
     * @return
     */
    public List<PlaceUserRankResponse> getPlaceRanks(User user, Long placeId) {
        List<PlaceRankWithRankingProjection> placeRankWithRankings = placeRankRepository.findTop3RanksWithTies(
                user.getId(), placeId);

        Map<Integer, List<PlaceRankWithRankingProjection>> placeRanksGroupedByCount = placeRankWithRankings.stream()
                .collect(Collectors.groupingBy(
                        PlaceRankWithRankingProjection::getRanking,
                        TreeMap::new,
                        toList())
                );

        return placeRanksGroupedByCount.values()
                .stream()
                .map(placeRanks -> PlaceUserRankResponse.from(placeRanks, user))
                .toList();
    }

    public List<GetPlaceUserRankResponse> getPlaceFriendRank(CustomUserDetails userDetails, Long friendId) {
        friendQueryService.validateFriend(userDetails, friendId);
        List<PlaceRank> PlaceRanks = placeRankRepository.findTop3RanksWithTiesByUser(friendId);

        Map<Integer, List<PlaceRank>> placeRanksGroupedByCount = PlaceRanks.stream()
                .collect(Collectors.groupingBy(
                        PlaceRank::getCount,
                        () -> new TreeMap<>(Comparator.reverseOrder()),
                        toList())
                );

        return placeRanksGroupedByCount.values()
                .stream()
                .map(GetPlaceUserRankResponse::from)
                .toList();
    }

    public GetPlaceRankPaginationResponse getPlaceRanks(Long placeId,
                                                        PlaceRankPaginationRequest request) {
        PlaceRankingLastKnownCursor page = PlaceRankingLastKnownCursor.from(request.lastKnown());

        List<PlaceRankWithRankingProjection> placeRankWithRankings = placeRankRepository.findRankByRange(placeId,
                page.lastRank(),
                page.lastRankId(),
                request.limit() + 1);

        List<PlaceRankWithRankingProjection> placeRankWithRankingProjections = placeRankWithRankings.stream()
                .limit(request.limit())
                .toList();

        List<GetPlaceRankResponse> response = placeRankWithRankingProjections
                .stream()
                .map(GetPlaceRankResponse::from)
                .toList();

        boolean hasNext = placeRankWithRankings.size() > request.limit();
        String nextCursor = null;
        if (hasNext) {
            PlaceRankWithRankingProjection lastItem = placeRankWithRankingProjections.get(
                    placeRankWithRankingProjections.size() - 1);
            nextCursor = PlaceRankingLastKnownCursor.of(lastItem.getRanking(), lastItem.getRankId())
                    .toCursorString();
        }
        return GetPlaceRankPaginationResponse.of(response, hasNext, nextCursor);
    }

    public List<GetPlaceTopRankResponse> getPlaceTopRank(Long placeId) {
        List<PlaceRankWithRankingProjection> placeRankWithRankingProjections = placeRankRepository.findRankByRange(
                placeId, TOP_3_START, TOP_3_END);

        Map<Integer, List<PlaceRankWithRankingProjection>> placeRanksGroupedByCount = placeRankWithRankingProjections.stream()
                .collect(Collectors.groupingBy(
                        PlaceRankWithRankingProjection::getRanking,
                        TreeMap::new,
                        Collectors.toList())
                );

        return placeRanksGroupedByCount
                .values()
                .stream()
                .map(PlaceRankWithRankingProjection -> GetPlaceTopRankResponse.from(PlaceRankWithRankingProjection))
                .toList();
    }

    public void checkoutRankChange(RankingChangeDto rankingChangeDto, CustomUserDetails userDetails) {
        User user = userService.getUser();
        if (rankingChangeDto.beforeRank() > rankingChangeDto.afterRank()) {
            if (rankingChangeDto.afterRank() == 1) {
                alarmService.notifyAlarm(AlarmType.RENEW_TOP_RANK_PLACE, rankingChangeDto, user);
                return;
            }
            alarmService.notifyAlarm(AlarmType.RENEW_RANK_PLACE, rankingChangeDto, user);
        }
    }
}
