package com.backend.talk_nest.repositories;

import com.backend.talk_nest.entities.InvalidatedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends CrudRepository<InvalidatedToken, String> {
}
