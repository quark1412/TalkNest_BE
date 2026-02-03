package com.backend.talk_nest.repositories;

import com.backend.talk_nest.entities.FriendList;
import com.backend.talk_nest.entities.ids.FriendListId;
import com.backend.talk_nest.utils.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FriendListRepository extends JpaRepository<FriendList, FriendListId> {
    List<FriendList> findByFriend_IdAndStatus(UUID friendId, FriendStatus status);
}
