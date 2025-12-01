package ku_rum.backend.domain.alarm.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.FCM_SEND_ERROR;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.alarm.domain.repository.UserFcmTokenRepository;
import ku_rum.backend.domain.alarm.dto.request.DirectNotificationRequest;
import ku_rum.backend.domain.alarm.dto.request.TopicNotificationRequest;
import ku_rum.backend.domain.user.application.UserQueryService;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.UserFcmToken;
import ku_rum.backend.domain.user.dto.request.UserFcmRequest;
import ku_rum.backend.domain.user.dto.response.UserFcmResponse;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.security.CustomUserDetails;
import org.springframework.stereotype.Service;

@Service
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final UserQueryService userQueryService;
    private final UserService userService;

    public FcmService(UserFcmTokenRepository userFcmTokenRepository,
                      UserQueryService userQueryService, UserService userService) {
        this.userFcmTokenRepository = userFcmTokenRepository;
        this.userQueryService = userQueryService;
        this.userService = userService;
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

    public UserFcmResponse createFcmToken(CustomUserDetails userDetails, UserFcmRequest request) {
        User user = userService.getUser();
        Optional<UserFcmToken> fcmTokenOptional = userFcmTokenRepository.findByUser(user);

        UserFcmToken userFcmToken = UserFcmToken.builder()
                .token(request.token())
                .user(user)
                .deviceType(request.deviceType())
                .build();

        if (fcmTokenOptional.isPresent()) {
            UserFcmToken findUserFcmToken = fcmTokenOptional.get();
            findUserFcmToken.update(userFcmToken);
            return UserFcmResponse.from(findUserFcmToken);
        }

        UserFcmToken saveUserFcmToken = userFcmTokenRepository.save(userFcmToken);
        return UserFcmResponse.from(saveUserFcmToken);
    }
}
