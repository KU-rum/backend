package ku_rum.backend.domain.friend.application;

import static ku_rum.backend.domain.friend.domain.vo.FriendStatus.PENDING;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.DUPLICATE_FRIENDS;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.DUPLICATE_RESPONSE;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NOT_EQUAL_TO_USER;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_FRIEND_REQUEST;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_PENDING_LIST;

import ku_rum.backend.domain.alarm.application.AlarmService;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.friend.domain.Friend;
import ku_rum.backend.domain.friend.domain.repository.FriendRepository;
import ku_rum.backend.domain.friend.domain.vo.FriendStatus;
import ku_rum.backend.domain.friend.dto.request.FriendRequest;
import ku_rum.backend.domain.user.application.UserQueryService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.utill.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendManageService {

    private final FriendRepository friendRepository;
    private final UserUtil userUtil;
    private final UserQueryService userQueryService;
    private final AlarmService alarmService;

    // 친구 요청
    public void requestFriend(final FriendRequest friendSendRequest) {
        User fromUser = userUtil.getUser();
        User toUser = userQueryService.getUserById(friendSendRequest.receiverId());

        if (friendRepository.findByFromUserAndToUser(fromUser, toUser).isPresent()) {
            throw new GlobalException(DUPLICATE_FRIENDS);
        }
        friendRepository.save(Friend.of(fromUser, toUser, PENDING));

        alarmService.notifyAlarm(AlarmType.NEW_FRIEND_REQUEST, toUser);
    }

    // 친구 수락 및 거절
    public void respondToFriend(final FriendRequest friendSendRequest, boolean accept) {
        User currentUser = userUtil.getUser();
        User toUser = userQueryService.getUserById(friendSendRequest.receiverId());

        Friend friend = friendRepository.findByFromUserAndToUserAndStatus(toUser, currentUser, PENDING)
                .orElseThrow(() -> new GlobalException(NO_PENDING_LIST));

        if (!friend.getToUser().equals(currentUser)) {
            throw new GlobalException(NOT_EQUAL_TO_USER);
        }
        friend.setStatus(accept ? FriendStatus.ACCEPT : FriendStatus.REJECT);
    }

    // 보낸 친구 요청 삭제
    public void deleteSentRequest(final FriendRequest friendSendRequest) {
        User currentUser = userUtil.getUser();
        User toUser = userQueryService.getUserById(friendSendRequest.receiverId());

        Friend friend = friendRepository.findByFromUserAndToUserAndStatus(toUser, currentUser, PENDING)
                .orElseThrow(() -> new GlobalException(NO_PENDING_LIST));

        if (!friend.getFromUser().equals(currentUser)) {
            throw new GlobalException(NOT_EQUAL_TO_USER);
        }

        if (friend.getStatus() != PENDING) {
            throw new GlobalException(DUPLICATE_RESPONSE);
        }

        friendRepository.delete(friend);
    }

    public void deleteFriend(Long targetUserId) {
        User currentUser = userUtil.getUser();
        User targetUser = userQueryService.getUserById(targetUserId);

        boolean isFriend =
                friendRepository.existsByFromUserAndToUserAndStatus(currentUser, targetUser, FriendStatus.ACCEPT) ||
                        friendRepository.existsByFromUserAndToUserAndStatus(targetUser, currentUser,
                                FriendStatus.ACCEPT);

        if (!isFriend) {
            throw new GlobalException(NO_FRIEND_REQUEST);
        }

        friendRepository.deleteByFromUserAndToUser(currentUser, targetUser);
        friendRepository.deleteByFromUserAndToUser(targetUser, currentUser);
    }

}