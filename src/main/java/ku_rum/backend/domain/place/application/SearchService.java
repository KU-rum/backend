package ku_rum.backend.domain.place.application;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.place.application.response.SearchPlaceResponse;
import ku_rum.backend.domain.place.domain.CategoryChip;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.PlaceAlias;
import ku_rum.backend.domain.place.domain.repository.PlaceAliasRepository;
import ku_rum.backend.domain.place.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchService {
    private final PlaceRepository placeRepository;
    private final PlaceAliasRepository placeAliasRepository;

    /**
     * 장소 검색(비회원 로직)
     *
     * @param query 검색어
     * @return
     */
    public List<SearchPlaceResponse> searchPlace(String query) {
        query = refineQuery(query);
        Optional<CategoryChip> categoryChipOptional = CategoryChip.from(query);
        if (categoryChipOptional.isPresent()) {
            return searchPlaceByCategory(categoryChipOptional.get());
        }

        List<Place> places = placeRepository.findByNameContaining(query);
        if (places.isEmpty()) {
            return placeRepository.findByNameContaining(query).stream()
                    .map(SearchPlaceResponse::from)
                    .toList();
        }
        return places.stream()
                .map(SearchPlaceResponse::from)
                .toList();
    }


    /**
     * 카테고리에 해당하는 장소 검색
     *
     * @return
     */
    private List<SearchPlaceResponse> searchPlaceByCategory(CategoryChip categoryChip) {
        return placeRepository.findByCategoryChip(categoryChip).stream()
                .map(SearchPlaceResponse::from)
                .toList();
    }

    /**
     * 검색어 수정
     *
     * @param query
     * @return
     */
    private String refineQuery(String query) {
        query = query.replaceAll("[0-9]", "");

        Optional<PlaceAlias> placeAliasOptional = placeAliasRepository.findPlaceAliasByOriginal(query);
        if (placeAliasOptional.isPresent()) {
            String replacement = placeAliasOptional.get().getReplacement();
            return query.replace(query, replacement);
        }
        return query;
    }

    private boolean isCategoryChip(String query) {
        List<CategoryChip> categoryChips = Arrays.stream(CategoryChip.values()).toList();
        return categoryChips.contains(query);
    }
}
