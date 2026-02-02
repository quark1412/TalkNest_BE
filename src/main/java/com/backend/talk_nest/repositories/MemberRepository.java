package com.backend.talk_nest.repositories;

import com.backend.talk_nest.entities.Member;
import com.backend.talk_nest.entities.ids.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, MemberId> {

    List<Member> findMemberByConversation_Id(UUID conversation_id);
}
