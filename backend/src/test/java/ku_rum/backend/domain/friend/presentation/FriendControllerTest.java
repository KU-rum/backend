package ku_rum.backend.domain.friend.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import ku_rum.backend.domain.friend.application.FriendService;
import ku_rum.backend.domain.friend.dto.request.FriendFindRequest;
import ku_rum.backend.domain.friend.dto.request.FriendListRequest;
import ku_rum.backend.global.config.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendController.class)
@Import(SecurityConfig.class)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FriendService friendService;

    @DisplayName("유저의 친구 목록을 성공적으로 불러온다.")
    @Test
    void getFriendLists() throws Exception {
        //given
        FriendListRequest request = FriendListRequest.from(1L);

        // when then
        mockMvc.perform(get("/api/v1/friends")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("기존 친구 목록에서 닉네임으로 친구를 검색한다.")
    @Test
    void findByNameInFriendLists() throws Exception {
        //given
        FriendFindRequest request = FriendFindRequest.of(1L, "nickname");

        // when then
        mockMvc.perform(get("/api/v1/friends/find")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

}