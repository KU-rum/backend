package ku_rum.backend.domain.alarm.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsUnitTestSupport;
import ku_rum.backend.domain.alarm.application.AlarmService;
import ku_rum.backend.domain.alarm.application.FcmService;
import ku_rum.backend.domain.alarm.dto.FcmDirectDto;
import ku_rum.backend.domain.alarm.dto.FcmTopicDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(AlarmController.class)
@ActiveProfiles("test")
public class FcmAlarmControllerTest extends RestDocsUnitTestSupport {

    @MockBean
    AlarmService alarmService;

    @MockBean
    FcmService fcmService;

    @DisplayName("특정 사용자에게 푸시 알림을 보낸다")
    @Test
    void sendToUsers() throws Exception {

        // given
        doNothing().when(fcmService).sendToUsers(any(FcmDirectDto.class));

        // when
        mockMvc.perform(post("/api/v1/alarm/direct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userIds": [1],
                                  "title": "알림 제목",
                                  "body": "알림 내용"
                                }
                                """))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("알림 FCM API")
                                .description("특정 사용자에게 푸시 알림을 보낸다")
                                .requestFields(
                                        fieldWithPath("userIds").description("유저 ID"),
                                        fieldWithPath("title").description("알림 제목"),
                                        fieldWithPath("body").description("알림 내용")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지")
                                )
                                .build()
                )));
    }

    @DisplayName("모든 사용자에게 푸시 알림을 보낸다")
    @Test
    void sendToTopic() throws Exception {

        // given
        doNothing().when(fcmService).sendToTopic(any(FcmTopicDto.class));

        // when
        mockMvc.perform(post("/api/v1/alarm/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "topic": "kuroom",
                                  "title": "알림 제목",
                                  "body": "알림 내용"
                                }
                                """))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("알림 FCM API")
                                .description("모든 사용자에게 푸시 알림을 보낸다")
                                .requestFields(
                                        fieldWithPath("topic").description("토픽 (kuroom)"),
                                        fieldWithPath("title").description("알림 제목"),
                                        fieldWithPath("body").description("알림 내용")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지")
                                )
                                .build()
                )));
    }
}
