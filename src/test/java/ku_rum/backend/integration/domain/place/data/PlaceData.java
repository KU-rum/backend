package ku_rum.backend.integration.domain.place.data;

import java.math.BigDecimal;
import ku_rum.backend.domain.place.domain.CategoryChip;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.PlaceAlias;
import ku_rum.backend.domain.place.domain.repository.PlaceAliasRepository;
import ku_rum.backend.domain.place.domain.repository.PlaceRepository;
import org.springframework.transaction.annotation.Transactional;

public class PlaceData {

    PlaceRepository placeRepository;
    PlaceAliasRepository placeAliasRepository;

    public PlaceData(PlaceRepository placeRepository, PlaceAliasRepository placeAliasRepository) {
        this.placeRepository = placeRepository;
        this.placeAliasRepository = placeAliasRepository;
    }

    @Transactional
    public void savePlaceData() {
        Place place1 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("공학관")
                .subName("공학관입니다.")
                .abbreviation("공학관 A,B,C,D동이 있습니다.")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place1);

        PlaceAlias placeAlias1 = PlaceAlias.builder()
                .original("공B")
                .replacement("공학관B")
                .build();
        placeAliasRepository.save(placeAlias1);

        Place place2 = Place.builder()
                .categoryChip(CategoryChip.K_CUBE)
                .name("공학관 케이큐브")
                .subName("공학관 케이큐브입니다.")
                .abbreviation("건국대학교 위인전에 예약 후 사용할 수 있습니다.")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place2);

        Place place3 = Place.builder()
                .categoryChip(CategoryChip.DEPARTMENT_OFFICE)
                .name("컴퓨터공학부")
                .subName("공과대학 소속")
                .abbreviation("학과 사무실은 공학관 1층에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place3);

        Place place4 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("경영관")
                .subName("경영대학 건물")
                .abbreviation("후문 좌측에 위치입니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place4);

        PlaceAlias placeAlias2 = PlaceAlias.builder()
                .original("경영")
                .replacement("경영관")
                .build();
        placeAliasRepository.save(placeAlias2);

        Place place5 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("상허연구관")
                .subName("기술 경영, ICT")
                .abbreviation("경영관 뒤에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place5);

        PlaceAlias placeAlias3 = PlaceAlias.builder()
                .original("상허관")
                .replacement("상허연구관")
                .build();
        placeAliasRepository.save(placeAlias3);

        Place place6 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("교육과학관")
                .subName("이과대학건물")
                .abbreviation("공학관, 창의관 사이에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place6);

        PlaceAlias placeAlias4 = PlaceAlias.builder()
                .original("사")
                .replacement("교육과학관")
                .build();
        placeAliasRepository.save(placeAlias4);

        Place place7 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("예술문화관")
                .subName("예디대건물")
                .abbreviation("도서관 좌측에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place7);

        Place place8 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("언어교육관")
                .subName("언어원")
                .abbreviation("도서관 3층 맞은 편에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place8);

        PlaceAlias placeAlias5 = PlaceAlias.builder()
                .original("언어원")
                .replacement("언어교육관")
                .build();
        placeAliasRepository.save(placeAlias5);

        Place place9 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("법학관")
                .subName("종강, 법")
                .abbreviation("박물관 좌측에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place9);

        PlaceAlias placeAlias6 = PlaceAlias.builder()
                .original("법")
                .replacement("법학관")
                .build();
        placeAliasRepository.save(placeAlias6);

        PlaceAlias placeAlias7 = PlaceAlias.builder()
                .original("종강")
                .replacement("법학관")
                .build();
        placeAliasRepository.save(placeAlias7);

        Place place10 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("의생명과학연구관")
                .subName("의")
                .abbreviation("도서관 우측에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place10);

        PlaceAlias placeAlias8 = PlaceAlias.builder()
                .original("의")
                .replacement("의생명과학연구관")
                .build();
        placeAliasRepository.save(placeAlias8);

        Place place11 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("생명과학관")
                .subName("생")
                .abbreviation("도서관 우측에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place11);

        PlaceAlias placeAlias9 = PlaceAlias.builder()
                .original("생")
                .replacement("생명과학관")
                .build();
        placeAliasRepository.save(placeAlias9);

        Place place12 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("동물생명과학관")
                .subName("동")
                .abbreviation("산학 뒤에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place12);

        PlaceAlias placeAlias10 = PlaceAlias.builder()
                .original("동")
                .replacement("동물생명과학관")
                .build();
        placeAliasRepository.save(placeAlias10);

        Place place13 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("산학협동관")
                .subName("산학")
                .abbreviation("수의대 옆에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place13);

        PlaceAlias placeAlias11 = PlaceAlias.builder()
                .original("산학")
                .replacement("산학협동관")
                .build();
        placeAliasRepository.save(placeAlias11);

        Place place14 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("수의학관")
                .subName("수")
                .abbreviation("건국대 입구 근처에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place14);

        PlaceAlias placeAlias12 = PlaceAlias.builder()
                .original("수")
                .replacement("수의학관")
                .build();
        placeAliasRepository.save(placeAlias12);

        Place place15 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("새천년관")
                .subName("새")
                .abbreviation("건국대 후문에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place15);

        PlaceAlias placeAlias13 = PlaceAlias.builder()
                .original("새")
                .replacement("새천년관")
                .build();
        placeAliasRepository.save(placeAlias13);

        Place place16 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("건축관")
                .subName("건")
                .abbreviation("건국대 후문에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place16);

        PlaceAlias placeAlias14 = PlaceAlias.builder()
                .original("건")
                .replacement("건축관")
                .build();
        placeAliasRepository.save(placeAlias14);

        Place place17 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("해봉부동산학관")
                .subName("부")
                .abbreviation("새천년돤 옆에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place17);

        PlaceAlias placeAlias15 = PlaceAlias.builder()
                .original("부")
                .replacement("해봉부동산학관")
                .build();
        placeAliasRepository.save(placeAlias15);

        Place place18 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("인문학관")
                .subName("문")
                .abbreviation("공학관 왼쪽에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place18);

        PlaceAlias placeAlias16 = PlaceAlias.builder()
                .original("문")
                .replacement("인문학관")
                .build();
        placeAliasRepository.save(placeAlias16);

        Place place19 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("신공학관")
                .subName("신공")
                .abbreviation("공학관 오른쪽에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place19);

        PlaceAlias placeAlias17 = PlaceAlias.builder()
                .original("신공")
                .replacement("신공학관")
                .build();
        placeAliasRepository.save(placeAlias17);

        Place place20 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("과학관")
                .subName("이")
                .abbreviation("공학관 뒤편에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place20);

        PlaceAlias placeAlias18 = PlaceAlias.builder()
                .original("이")
                .replacement("과학관")
                .build();
        placeAliasRepository.save(placeAlias18);

        Place place21 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("창의관")
                .subName("창")
                .abbreviation("공학관 뒤편에 위치합니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place21);

        PlaceAlias placeAlias19 = PlaceAlias.builder()
                .original("창")
                .replacement("창의관")
                .build();
        placeAliasRepository.save(placeAlias19);

        Place place22 = Place.builder()
                .categoryChip(CategoryChip.CONVENIENCE_STORE)
                .name("산학협동관 이마트24")
                .subName("이마트24")
                .abbreviation("이마트24입니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place22);

        Place place23 = Place.builder()
                .categoryChip(CategoryChip.CONVENIENCE_STORE)
                .name("학생회관 CU")
                .subName("학생회관 CU")
                .abbreviation("학생회관 CU 입니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place23);

        Place place24 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("공학관A")
                .subName("공학관 중앙에 위치한 공학관A동입니다.")
                .abbreviation("공학관 중앙 건물입니다")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place24);

        Place place25 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("공학관B")
                .subName("공학관 좌측에 위치한 공학관B동입니다.")
                .abbreviation("공학관 건물, 중장비 연구실 맞은편에 있습니다.")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place25);

        Place place26 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("공학관C")
                .subName("공학관 우측에 위치한 공학관C동입니다.")
                .abbreviation("신공학관 맞은 편에 있습니다.")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place26);

        Place place27 = Place.builder()
                .categoryChip(CategoryChip.BUILDING)
                .name("공학관D")
                .subName("공학관 건물 안에 있는 공학관D동입니다.")
                .abbreviation("공학관 ㄷ자 안에 있습니다.")
                .content("내용")
                .latitude(BigDecimal.valueOf(12.12))
                .longitude(BigDecimal.valueOf(12.12))
                .build();
        placeRepository.save(place27);

        PlaceAlias placeAlias20 = PlaceAlias.builder()
                .original("공A")
                .replacement("공학관A")
                .build();
        placeAliasRepository.save(placeAlias20);

        PlaceAlias placeAlias21 = PlaceAlias.builder()
                .original("공C")
                .replacement("공학관C")
                .build();
        placeAliasRepository.save(placeAlias21);
    }

    @Transactional
    public void afterEach() {
        placeRepository.deleteAll();
        placeAliasRepository.deleteAll();
    }
}