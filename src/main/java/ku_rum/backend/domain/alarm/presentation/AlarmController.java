package ku_rum.backend.domain.alarm.presentation;

import jakarta.validation.Valid;
import ku_rum.backend.domain.alarm.application.AlarmService;
import ku_rum.backend.domain.alarm.dto.request.PatchAlarmRequest;
import ku_rum.backend.domain.alarm.dto.response.AlarmPaginationRequest;
import ku_rum.backend.domain.alarm.dto.response.GetAlarmResponse;
import ku_rum.backend.domain.alarm.dto.response.GetAlarmUnreadResponse;
import ku_rum.backend.domain.alarm.dto.response.PatchAlarmResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public BaseResponse<GetAlarmResponse> getAlarm(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @ModelAttribute AlarmPaginationRequest request) {
        GetAlarmResponse response = alarmService.getAlarmResponse(userDetails, request);
        return BaseResponse.ok(response);
    }

    @PatchMapping
    public BaseResponse<PatchAlarmResponse> patchAlarm(
            @RequestBody PatchAlarmRequest request,
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        PatchAlarmResponse response = alarmService.patchUserAlarm(userDetails, request);
        return BaseResponse.ok(response);
    }

    @GetMapping("/unread")
    public BaseResponse<GetAlarmUnreadResponse> getUnreadAlarm(
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        GetAlarmUnreadResponse response = alarmService.getAlarmUnreadResponse(userDetails);
        return BaseResponse.ok(response);
    }
}
