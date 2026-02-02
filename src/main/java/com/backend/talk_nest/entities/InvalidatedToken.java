package com.backend.talk_nest.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
@RedisHash("InvalidatedToken")
public class InvalidatedToken {
    @Id
    private String id;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;
}
