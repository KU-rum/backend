package ku_rum.backend.domain.alarm.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.FCM_SEND_ERROR;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import java.util.List;
import ku_rum.backend.domain.alarm.domain.UserFcmToken;
import ku_rum.backend.domain.alarm.domain.repository.UserFcmTokenRepository;
import ku_rum.backend.domain.alarm.dto.request.DirectNotificationRequest;
import ku_rum.backend.domain.alarm.dto.request.TopicNotificationRequest;
import ku_rum.backend.domain.user.application.UserQueryService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
import org.springframework.stereotype.Service;

@Service

public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final UserQueryService userQueryService;

    public FcmService(UserFcmTokenRepository userFcmTokenRepository,
                      UserQueryService userQueryService) {
        this.userFcmTokenRepository = userFcmTokenRepository;
        this.userQueryService = userQueryService;
        this.firebaseMessaging = FirebaseMessaging.getInstance();
    }

    public void sendToUsers(DirectNotificationRequest request) {
        List<User> users = request.userIds().stream()
                .map(userQueryService::getUserById)
                .toList();

        List<UserFcmToken> userFcmTokens = userFcmTokenRepository.findByUserIn(users);
        List<String> tokens = userFcmTokens.stream()
                .map(UserFcmToken::getToken)
                .toList();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putData("title", request.title())
                .putData("body", request.body())
                .build();

        try {
            firebaseMessaging.sendMulticast(message);
        } catch (FirebaseMessagingException e) {
            throw new GlobalException(FCM_SEND_ERROR);
        }

    }

    public void sendToTopic(TopicNotificationRequest request) {

        Message message = Message.builder()
                .setTopic(request.topic())
                .putData("title", request.title())
                .putData("body", request.body())
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new GlobalException(FCM_SEND_ERROR);
        }
    }
}
