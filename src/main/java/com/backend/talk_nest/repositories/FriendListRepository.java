package com.backend.talk_nest.repositories;

import com.backend.talk_nest.entities.FriendList;
import com.backend.talk_nest.entities.ids.FriendListId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendListRepository extends JpaRepository<FriendList, FriendListId> {
}
