package ku_rum.backend.domain.friend.application;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ku_rum.backend.domain.friend.domain.Friend;
import ku_rum.backend.domain.friend.domain.repository.FriendRepository;
import ku_rum.backend.domain.friend.domain.vo.FriendStatus;
import ku_rum.backend.domain.friend.dto.response.FriendListResponse;
import ku_rum.backend.domain.friend.dto.response.FriendSearchResponse;
import ku_rum.backend.domain.friend.dto.response.ReceivedFriendResponse;
import ku_rum.backend.domain.friend.dto.response.SentFriendResponse;
import ku_rum.backend.domain.user.application.UserQueryService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import ku_rum.backend.global.utill.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendQueryService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final UserQueryService userQueryService;

    // 친구 목록 조회
    public List<FriendListResponse> getFriendList() {
        User currentUser = userUtil.getUser();

        List<Friend> sent = friendRepository.findByFromUserAndStatus(currentUser, FriendStatus.ACCEPT);
        List<Friend> received = friendRepository.findByToUserAndStatus(currentUser, FriendStatus.ACCEPT);

        return Stream.concat(
                sent.stream().map(f -> FriendListResponse.from(f.getToUser())),
                received.stream().map(f -> FriendListResponse.from(f.getFromUser()))
        ).collect(Collectors.toList());
    }

    // 받은 요청 목록 조회
    public List<ReceivedFriendResponse> getReceivedPendingRequests() {
        User currentUser = userUtil.getUser();

        return friendRepository.findByToUserAndStatus(currentUser, FriendStatus.PENDING)
                .stream()
                .map(ReceivedFriendResponse::from)
                .collect(Collectors.toList());
    }

    public List<SentFriendResponse> getSentPendingRequests() {
        User currentUser = userUtil.getUser();
        List<Friend> sentRequests = friendRepository.findByFromUserAndStatus(currentUser, FriendStatus.PENDING);

        return sentRequests.stream()
                .map(req -> new SentFriendResponse(
                        req.getId(),
                        req.getToUser().getId(),
                        req.getToUser().getNickname(),
                        req.getToUser().getImageUrl()
                ))
                .toList();
    }

    public List<FriendSearchResponse> searchByNickname(final String nickname) {
        User currentUser = userUtil.getUser();
        List<User> matchedUsers = userRepository.findByNicknameContainingIgnoreCase(nickname).stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .toList();

        List<Long> targetUserIds = matchedUsers.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // 1. 친구 요청을 보낸 사용자 ID 리스트 (PENDING)
        List<Long> sentRequestUserIds = friendRepository.findToUserIdsByFromUserAndStatus(currentUser.getId(),
                targetUserIds, FriendStatus.PENDING);

        // 2. 친구인 사용자 ID 리스트 (ACCEPTED 양방향)
        List<Long> friendUserIds = friendRepository.findFriendUserIds(currentUser.getId(), targetUserIds,
                FriendStatus.ACCEPT);

        return matchedUsers.stream()
                .map(user -> new FriendSearchResponse(
                        user.getId(),
                        user.getNickname(),
                        user.getImageUrl(),
                        sentRequestUserIds.contains(user.getId()),
                        friendUserIds.contains(user.getId())
                ))
                .collect(Collectors.toList());
    }

    public void validateFriend(CustomUserDetails userDetails, Long friendId) {
        User currentUser = userUtil.getUser();
        User targetUser = userQueryService.getUserById(friendId);

        boolean isFriend =
                friendRepository.existsByFromUserAndToUserAndStatus(currentUser, targetUser, FriendStatus.ACCEPT) ||
                        friendRepository.existsByFromUserAndToUserAndStatus(targetUser, currentUser,
                                FriendStatus.ACCEPT);
        if (!isFriend) {
            throw new GlobalException(BaseExceptionResponseStatus.NO_FRIEND_REQUEST);
        }
    }
}
