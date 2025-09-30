package ku_rum.backend.domain.place.application;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ku_rum.backend.domain.place.application.response.GetPlaceResponse;
import ku_rum.backend.domain.place.application.response.SearchPlaceResponse;
import ku_rum.backend.domain.place.application.response.SelectPlaceChipFriendListResponse;
import ku_rum.backend.domain.place.application.response.SelectPlaceChipResponse;
import ku_rum.backend.domain.place.domain.CategoryChip;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.PlaceImage;
import ku_rum.backend.domain.place.domain.repository.PlaceImageRepository;
import ku_rum.backend.domain.place.domain.repository.PlaceRepository;
import ku_rum.backend.domain.place.domain.repository.PositionRepository;
import ku_rum.backend.domain.place.dto.FriendUserDto;
import ku_rum.backend.domain.rank.application.RankService;
import ku_rum.backend.domain.rank.application.response.PlaceUserRankResponse;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PositionRepository positionRepository;
    private final PlaceImageRepository placeImageRepository;
    private final PlaceHistoryService placeHistoryService;
    private final SearchService searchService;
    private final RankService rankService;
    private final UserService userService;

    /**
     * 지도 칩 조회(회원 로직)
     *
     * @param userDetails
     * @return response 객체
     */
    public List<SelectPlaceChipResponse> selectChipWithUser(
            final CustomUserDetails userDetails,
            final CategoryChip categoryChip) {

        if (categoryChip.equals(CategoryChip.FRIEND)) {
            List<Place> places = placeRepository.findByCategoryChip(CategoryChip.BUILDING);
            return selectFriendListWithUser(userDetails, places);
        }

        List<Place> places = placeRepository.findByCategoryChip(categoryChip);
        if (categoryChip.equals(CategoryChip.BUILDING)) {
            return selectBuildingChipWithUser(userDetails, places);
        }

        return findPlacesWithImages(places);
    }

    /**
     * 지도 칩 조회(비회원 로직)
     *
     * @return
     */
    public List<SelectPlaceChipResponse> selectChip(final CategoryChip categoryChip) {
        if (categoryChip.equals(CategoryChip.FRIEND)) {
            throw new GlobalException(BaseExceptionResponseStatus.UNSUPPORTED_FRIEND_CHIP_ERROR);
        }

        List<Place> places = placeRepository.findByCategoryChip(categoryChip);

        return places.stream()
                .map(SelectPlaceChipResponse::from)
                .toList();
    }


    /**
     * 장소 조회(회원 로직)
     *
     * @param userDetails 인증 객체
     * @param placeId     장소 PK
     * @return
     */
    public GetPlaceResponse getPlaceWithUser(
            final CustomUserDetails userDetails,
            final Long placeId) {
        Place place = findPlace(placeId);
        List<PlaceImage> placeImages = placeImageRepository.findByPlace(place);
        List<FriendUserDto> friendUserDtos = positionRepository.findPositionByFriendAndPlace(userDetails.getUserId(),
                place);
        User user = userService.getUser();
        List<PlaceUserRankResponse> placeRanks = rankService.getPlaceRanks(user, placeId);

        return GetPlaceResponse.of(place, friendUserDtos, placeImages, placeRanks);
    }

    /**
     * 장소 조회(비회원 로직)
     *
     * @param placeId 장소 PK
     * @return
     */
    public GetPlaceResponse getPlace(
            final Long placeId) {
        Place place = findPlace(placeId);
        List<PlaceImage> placeImages = placeImageRepository.findByPlace(place);

        return GetPlaceResponse.of(place, Collections.emptyList(), placeImages, Collections.emptyList());
    }

    /**
     * 장소 검색(비회원 로직)
     *
     * @param query 검색어
     * @return
     */
    public List<SearchPlaceResponse> searchPlace(String query) {
        return searchService.searchPlace(query);
    }

    /**
     * 장소 검색(회원 로직)
     *
     * @param userDetails
     * @param query
     * @return
     */
    @Transactional
    public List<SearchPlaceResponse> searchPlaceWithUser(CustomUserDetails userDetails, String query) {
        placeHistoryService.updatePlaceHistory(query, userDetails);
        return searchPlace(query);
    }

    /**
     * place 조회
     *
     * @param placeId
     * @return Place 엔티티
     */
    private Place findPlace(Long placeId) {
        return placeRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new GlobalException(BaseExceptionResponseStatus.PLACE_NOT_FOUND));
    }

    /**
     * 빌딩 칩 조회(회원 로직), 빌딩 정보와 친구의 공유 정보를 함께 반환
     *
     * @param userDetails
     * @return response 객체
     */
    private List<SelectPlaceChipResponse> selectBuildingChipWithUser(
            final CustomUserDetails userDetails, final List<Place> places) {
        List<FriendUserDto> friendUserDtos = positionRepository.findPlaceByFriend(userDetails.getUserId());

        return mapPlacesWithFriends(places, friendUserDtos);
    }

    /**
     * 친구 칩 조회(회원 로직), 친구의 공유 정보를 위치값과 함께 반환
     *
     * @return response 객체
     */
    private List<SelectPlaceChipResponse> selectFriendListWithUser(
            final CustomUserDetails userDetails, final List<Place> places) {
        List<FriendUserDto> friendUserDtos = positionRepository.findPlaceByFriend(userDetails.getUserId());

        Set<Long> placeIdSet = friendUserDtos.stream()
                .map(friendUserDto -> friendUserDto.place().getPlaceId())
                .collect(Collectors.toSet());

        List<Place> filteredPlaces = places.stream()
                .filter(place -> placeIdSet.contains(place.getPlaceId()))
                .toList();

        return mapPlacesWithFriends(filteredPlaces, friendUserDtos);
    }

    /**
     * 장소와 공유 친구, 이미지 함께 조회
     *
     * @param places         장소 리스트
     * @param friendUserDtos 친구 DTO
     * @return
     */
    private List<SelectPlaceChipResponse> mapPlacesWithFriends(final List<Place> places,
                                                               final List<FriendUserDto> friendUserDtos) {
        return places.stream()
                .map(place -> {
                    List<SelectPlaceChipFriendListResponse> matchedFriends = friendUserDtos.stream()
                            .filter(friend -> friend.place().equals(place))
                            .map(SelectPlaceChipFriendListResponse::from)
                            .toList();

                    return SelectPlaceChipResponse.from(place, matchedFriends);
                })
                .toList();
    }

    /**
     * 장소 이미지와 함께 조회
     *
     * @param places 장소 리스트
     * @return
     */
    private List<SelectPlaceChipResponse> findPlacesWithImages(final List<Place> places) {
        return places.stream()
                .map(SelectPlaceChipResponse::from)
                .toList();
    }
}
