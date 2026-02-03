package com.backend.talk_nest.services;

import com.backend.talk_nest.dtos.friends.requests.SendFriendRequestRequest;
import com.backend.talk_nest.dtos.friends.responses.FriendRequestResponse;
import com.backend.talk_nest.dtos.users.responses.UserResponse;
import com.backend.talk_nest.entities.FriendList;
import com.backend.talk_nest.entities.ids.FriendListId;
import com.backend.talk_nest.entities.User;
import com.backend.talk_nest.exceptions.AppException;
import com.backend.talk_nest.mappers.UserMapper;
import com.backend.talk_nest.repositories.FriendListRepository;
import com.backend.talk_nest.repositories.UserRepository;
import com.backend.talk_nest.utils.enums.ErrorCode;
import com.backend.talk_nest.utils.enums.FriendStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendListService {
    private final FriendListRepository friendListRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void sendFriendRequest(SendFriendRequestRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().toString().equals(request.getFriendId())) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        User friend = userRepository.findById(UUID.fromString(request.getFriendId())).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FriendListId id = new FriendListId(currentUser.getId(), friend.getId());
        if (friendListRepository.existsById(id)) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        FriendList fl = FriendList.builder()
                .id(id)
                .user(currentUser)
                .friend(friend)
                .status(FriendStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .createdBy(currentUser)
                .build();
        friendListRepository.save(fl);
    }

    public List<FriendRequestResponse> getIncomingRequestsByUserId(String userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().toString().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        UUID uid = UUID.fromString(userId);
        List<FriendList> incoming = friendListRepository.findByFriend_IdAndStatus(uid, FriendStatus.PENDING);

        return incoming.stream().map(fl -> {
            FriendRequestResponse resp = new FriendRequestResponse();
            UserResponse requester = userMapper.toResponse(fl.getUser());
            resp.setRequester(requester);
            resp.setStatus(fl.getStatus());
            resp.setCreatedAt(fl.getCreatedAt());
            return resp;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void approveRequest(String senderId) {
        User currentUser = getCurrentUser();
        UUID senderUuid = UUID.fromString(senderId);
        FriendListId id = new FriendListId(senderUuid, currentUser.getId());
        FriendList fl = friendListRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));
        if (!FriendStatus.PENDING.equals(fl.getStatus())) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        fl.setStatus(FriendStatus.ACCEPTED);
        fl.setUpdatedBy(currentUser);
        friendListRepository.save(fl);

        FriendListId reciprocalId = new FriendListId(currentUser.getId(), senderUuid);
        if (!friendListRepository.existsById(reciprocalId)) {
            User sender = userRepository.findById(senderUuid).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            FriendList reciprocal = FriendList.builder()
                    .id(reciprocalId)
                    .user(currentUser)
                    .friend(sender)
                    .status(FriendStatus.ACCEPTED)
                    .createdBy(currentUser)
                    .createdAt(OffsetDateTime.now())
                    .build();
            friendListRepository.save(reciprocal);
        }
    }

    @Transactional
    public void rejectRequest(String senderId) {
        User currentUser = getCurrentUser();
        UUID senderUuid = UUID.fromString(senderId);
        FriendListId id = new FriendListId(senderUuid, currentUser.getId());
        FriendList fl = friendListRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));
        if (!FriendStatus.PENDING.equals(fl.getStatus())) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        fl.setStatus(FriendStatus.DECLINED);
        fl.setUpdatedBy(currentUser);
        friendListRepository.save(fl);
    }
}
