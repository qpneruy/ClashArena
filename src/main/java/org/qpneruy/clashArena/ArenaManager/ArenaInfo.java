package org.qpneruy.clashArena.ArenaManager;

import lombok.Data;
import org.bukkit.World;
import java.util.Objects;

@Data
public class ArenaInfo {
    private volatile ArenaState state;
    private volatile long lastUsedTimestamp;
    private final World world;

    ArenaInfo(World world, ArenaState initialState) {
        this.world = Objects.requireNonNull(world, "World cannot be null for ArenaInfo");
        this.state = initialState;
        this.lastUsedTimestamp = System.currentTimeMillis();
    }

    void updateLastUsed() {
        this.lastUsedTimestamp = System.currentTimeMillis();
    }
}
