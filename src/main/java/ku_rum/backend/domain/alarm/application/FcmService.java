package ku_rum.backend.domain.alarm.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.FCM_SEND_ERROR;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.INVALID_USER_TOKEN;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.alarm.domain.repository.UserFcmTokenRepository;
import ku_rum.backend.domain.alarm.dto.FcmDirectDto;
import ku_rum.backend.domain.alarm.dto.FcmTopicDto;
import ku_rum.backend.domain.user.application.UserQueryService;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.UserFcmToken;
import ku_rum.backend.domain.user.dto.request.UserFcmRequest;
import ku_rum.backend.domain.user.dto.response.UserFcmResponse;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final UserQueryService userQueryService;
    private final UserService userService;

    public void sendToUsers(FcmDirectDto request) {
        List<User> users = userQueryService.getUsersByIds(request.userIds());

        List<UserFcmToken> userFcmTokens = userFcmTokenRepository.findByUserIn(users);
        List<String> tokens = userFcmTokens.stream()
                .map(UserFcmToken::getToken)
                .toList();
        validateUserToken(users, tokens);

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(
                        Notification.builder()
                                .setTitle(request.title())
                                .setBody(request.body())
                                .build()
                )
                .putData("title", request.title())
                .putData("body", request.body())
                .build();

        try {
            firebaseMessaging.sendEachForMulticast(message);
        } catch (FirebaseMessagingException e) {
            log.error("FCM error code: {}", e.getErrorCode());
            log.error("FCM message: {}", e.getMessage(), e);
            e.printStackTrace();
            throw new GlobalException(FCM_SEND_ERROR);
        }

    }

    public void sendToTopic(FcmTopicDto request) {

        Message message = Message.builder()
                .setTopic(request.topic())
                .setNotification(
                        Notification.builder()
                                .setTitle(request.title())
                                .setBody(request.body())
                                .build()
                )
                .putData("title", request.title())
                .putData("body", request.body())
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("FCM error code: {}", e.getErrorCode());
            log.error("FCM message: {}", e.getMessage(), e);
            e.printStackTrace();
            throw new GlobalException(FCM_SEND_ERROR);
        }
    }

    @Transactional
    public UserFcmResponse createFcmToken(CustomUserDetails userDetails, UserFcmRequest request) {
        User user = userService.getUser();
        Optional<UserFcmToken> existingToken = userFcmTokenRepository.findByUser(user);

        UserFcmToken result = existingToken
                .map(token -> updateToken(token, request))
                .orElseGet(() -> createToken(user, request));

        return UserFcmResponse.from(result);
    }


    private void validateUserToken(List<User> users, List<String> tokens) {
        if (users.size() > tokens.size()) {
            throw new GlobalException(INVALID_USER_TOKEN);
        }
    }

    private UserFcmToken createToken(User user, UserFcmRequest request) {
        UserFcmToken newToken = UserFcmToken.builder()
                .token(request.token())
                .user(user)
                .deviceType(request.deviceType())
                .build();

        return userFcmTokenRepository.save(newToken);
    }

    private UserFcmToken updateToken(UserFcmToken existingToken, UserFcmRequest request) {
        existingToken.update(
                UserFcmToken.builder()
                        .token(request.token())
                        .user(existingToken.getUser())
                        .deviceType(request.deviceType())
                        .build()
        );
        return existingToken;
    }
}
