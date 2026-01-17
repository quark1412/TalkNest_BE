package com.backend.talk_nest.entities.ids;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class MemberId implements Serializable {
    private UUID conversationId;
    private UUID userId;
}
