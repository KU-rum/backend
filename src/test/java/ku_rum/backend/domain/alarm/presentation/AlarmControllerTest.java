package ku_rum.backend.domain.alarm.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.alarm.application.AlarmService;
import ku_rum.backend.domain.alarm.domain.AlarmCategory;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.dto.request.PatchAlarmRequest;
import ku_rum.backend.domain.alarm.dto.response.AlarmPaginationRequest;
import ku_rum.backend.domain.alarm.dto.response.GetAlarmDto;
import ku_rum.backend.domain.alarm.dto.response.GetAlarmResponse;
import ku_rum.backend.domain.alarm.dto.response.PatchAlarmResponse;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(AlarmController.class)
@ActiveProfiles("test")
public class AlarmControllerTest extends RestDocsTestSupport {

    @MockBean
    AlarmService alarmService;

    @MockBean
    private ApiLogRepository apiLogRepository;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @DisplayName("알림을 조회한다")
    @Test
    void getAlarms() throws Exception {

        // given
        List<GetAlarmDto> getAlarmDtos = List.of(
                new GetAlarmDto(1L, AlarmType.NEW_NOTICE, AlarmCategory.ALARM, "새로운 알람이 도착했습니다.", false, "1L",
                        LocalDateTime.now()));
        GetAlarmResponse response = new GetAlarmResponse(getAlarmDtos, false, "13");
        AlarmPaginationRequest request = new AlarmPaginationRequest("12", 1);
        given(alarmService.getAlarmResponse(any(), eq(request)))
                .willReturn(response);

        // when
        mockMvc.perform(get("/api/v1/alarm")
                        .header("Authorization", "Bearer test-access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .param("lastKnown", "12")
                        .param("limit", "1"))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.alarms[0].id").value(1L))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("알림 조회 API")
                                .description("알림을 무한 스크롤로 조회한다.")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰입니다.")
                                )
                                .queryParameters(
                                        parameterWithName("lastKnown").description("마지막 알람 ID (커서) 초기 null"),
                                        parameterWithName("limit").description("가져올 데이터 개수")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data.hasNext").description("다음 데이터 존재 유무"),
                                        fieldWithPath("data.nextCursor").description("다음 커서"),
                                        fieldWithPath("data.alarms[].id").description("알림 ID"),
                                        fieldWithPath("data.alarms[].alarmType").description("알림 타입"),
                                        fieldWithPath("data.alarms[].message").description("알림 메세지"),
                                        fieldWithPath("data.alarms[].dataId").description("알림 데이터 ID"),
                                        fieldWithPath("data.alarms[].isChecked").description("알림 확인 여부"),
                                        fieldWithPath("data.alarms[].createdAt").description("알림 시간"),
                                        fieldWithPath("data.alarms[].alarmCategory").description("알림 카테고리")
                                )
                                .build()
                )));
    }

    @DisplayName("알림을 확인한다")
    @Test
    void patchAlarm() throws Exception {

        // given
        PatchAlarmRequest request = new PatchAlarmRequest(1L, AlarmCategory.ALARM);
        PatchAlarmResponse response = new PatchAlarmResponse(1L, AlarmType.NEW_NOTICE, "메세지", "1");
        Long alarmId = 1L;
        given(alarmService.patchUserAlarm(any(), eq(request)))
                .willReturn(response);

        // when
        mockMvc.perform(patch("/api/v1/alarm")
                        .header("Authorization", "Bearer test-access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "alarmId": 1,
                                  "alarmCategory": "ALARM"
                                }
                                """))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("알림 조회 API")
                                .description("알림을 확인 한다.")
                                .requestFields(
                                        fieldWithPath("alarmId").description("알림 ID"),
                                        fieldWithPath("alarmCategory").description("알림 타입")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data").description("알림 확인 응답 데이터"),
                                        fieldWithPath("data.id").description("알림 ID"),
                                        fieldWithPath("data.alarmType").description("알림 타입"),
                                        fieldWithPath("data.message").description("알림 메세지"),
                                        fieldWithPath("data.dataId").description("알림 데이터 ID")
                                )
                                .build()
                )));
    }
}
