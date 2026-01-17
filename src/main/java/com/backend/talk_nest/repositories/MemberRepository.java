package com.backend.talk_nest.repositories;

import com.backend.talk_nest.entities.Member;
import com.backend.talk_nest.entities.ids.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, MemberId> {
}
