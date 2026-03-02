package ku_rum.backend.domain.department.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DepartmentType {

    //자유전공학부
    자유전공학부("Liberal Arts and Sciences", "https://www.konkuk.ac.kr/sites/kusls/index.do"),


    //공과대학 (College of Engineering)
    컴퓨터공학부("Computer Science Engineering", "https://cse.konkuk.ac.kr/cse/index.do"),
    사회환경공학부("Civil and Environmental Engineering", "https://cee.konkuk.ac.kr/cee/index.do"),
    기계공학부("Mechanical Engineering", "https://me.konkuk.ac.kr/me/index.do"),
    전기전자공학부("Electrical and Electronic Engineering", "https://ee.konkuk.ac.kr/ee/index.do"),
    화학공학부("Chemical Engineering", "https://chemeng.konkuk.ac.kr/chemeng/index.do"),
    신산업융합학과("New Industry Convergence", "https://aif.konkuk.ac.kr/aif/index.do"),
    K뷰티산업융합학과("K-Beauty Industry Convergence", "https://kbeauty.konkuk.ac.kr/kbeauty/index.do"),
    항공우주정보시스템공학과("Aerospace Information Systems Engineering", "https://aeroeng.konkuk.ac.kr/aeroeng/index.do"),
    생물공학과("Biotechnology Engineering", "https://www.konkuk.ac.kr/sites/microbio/index.do"),
    산업공학과("Industrial Engineering", "https://kies.konkuk.ac.kr/kies/index.do"),

    //문과대학 (College of Liberal Arts)
    국어국문학과("Korean Language and Literature", "https://korea.konkuk.ac.kr/korea/index.do"),
    영어영문학과("English Language and Literature", "https://english.konkuk.ac.kr/english/index.do"),
    중어중문학과("Chinese Language and Literature", "https://china.konkuk.ac.kr/china/index.do"),
    철학과("Philosophy", "https://philo.konkuk.ac.kr/philo/index.do"),
    사학과("History", "https://khistory.konkuk.ac.kr/khistory/index.do"),
    지리학과("Geography", "https://www.konkuk.ac.kr/sites/kugeo/index.do"),
    미디어커뮤니케이션학과("Media and Communication", "https://comm.konkuk.ac.kr/comm/index.do"),
    문화콘텐츠학과("Culture and Contents", "https://culturecontents.konkuk.ac.kr/culturecontents/index.do"),
    휴먼ICT연계전공("Human ICT Interdisciplinary Major", "https://www.konkuk.ac.kr/kuinterdept/9541/subview.do"),
    글로벌MICE연계전공("Global MICE Interdisciplinary Major", "https://www.konkuk.ac.kr/bulletins24/32430/subview.do"),
    인문상담치유연계전공("Humanities Counseling and Healing Interdisciplinary Major",
            "https://www.konkuk.ac.kr/kuinterdept/9569/subview.do"),
    통일인문교육연계전공("Unification Humanities Education Interdisciplinary Major",
            "https://www.konkuk.ac.kr/bulletins22/33717/subview.do"),

    //사회과학대학 (College of Social Sciences)
    정치외교학과("Political Science and Diplomacy", "https://kupol.konkuk.ac.kr/kupol/index.do"),
    경제학과("Economics", "https://econ.konkuk.ac.kr/econ/index.do"),
    행정학과("Public Administration", "https://kupa.konkuk.ac.kr/kupa/index.do"),
    국제무역학과("International Trade", "https://itrade.konkuk.ac.kr/itrade/index.do"),
    응용통계학과("Applied Statistics", "https://www.konkuk.ac.kr/sites/stat/index.do"),
    융합인재학과("Convergence Human Resources", "https://www.konkuk.ac.kr/konkuk/2517/subview.do"),
    글로벌비즈니스학과("Global Business", "https://dois.konkuk.ac.kr/dois/index.do"),

    //경영대학 (College of Business)
    경영학과("Business Administration", "https://kbs.konkuk.ac.kr/kbs/index.do"),
    기술경영학과("Management of Technology", "https://www.konkuk.ac.kr/sites/mot/index.do"),
    부동산학과("Real Estate Studies", "https://www.konkuk.ac.kr/kure/index.do"),

    //KU 융합기술원 (KU Institute of Convergence Technology)
    미래에너지공학과("Future Energy Engineering", "https://energy.konkuk.ac.kr/energy/index.do"),
    화장품공학과("Cosmetic Engineering", "https://www.konkuk.ac.kr/sites/cosmetics/index.do"),
    줄기세포재생공학과("Stem Cell and Regenerative Engineering", "https://scrb.konkuk.ac.kr/scrb/index.do"),
    의생명공학과("Biomedical Engineering", "https://bmse.konkuk.ac.kr/bmse/index.do"),
    시스템생명공학과("Systems Biotechnology", "https://kusysbt.konkuk.ac.kr/kusysbt/index.do"),
    융합생명공학과("Convergence Biotechnology", "https://ibb.konkuk.ac.kr/ibb/index.do"),

    //상허생명과학대학 (Sanghuh College of Life Sciences)
    생명과학특성학과("Biological Sciences", "https://biology.konkuk.ac.kr/biology/index.do"),
    동물자원과학과("Animal Science and Technology", "https://www.konkuk.ac.kr/konkuk/2314/subview.do"),
    식량자원과학과("Crop Science", "https://cropscience.konkuk.ac.kr/cropscience/index.do"),
    축산식품생명공학과("Food Science and Biotechnology of Animal Resources", "https://www.konkuk.ac.kr/konkuk/2316/subview.do"),
    식품유통공학과("Food Marketing and Technology", "https://kufsm.konkuk.ac.kr/kufsm/index.do"),
    환경보건과학과("Environmental Health Science", "https://ehs.konkuk.ac.kr/ehs/index.do"),
    산림조경학과("Forest and Landscape Architecture", "https://fla.konkuk.ac.kr/fla/index.do");

    private final String text;
    private final String url;
}