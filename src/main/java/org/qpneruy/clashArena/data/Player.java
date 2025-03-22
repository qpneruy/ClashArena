package org.qpneruy.clashArena.data;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Player {
    @Getter @Setter private String name;
    @Getter @Setter
    private UUID UniqueId;

    public Player(String name, UUID UniqueId) {
        this.name = name;
        this.UniqueId = UniqueId;
    }
}
