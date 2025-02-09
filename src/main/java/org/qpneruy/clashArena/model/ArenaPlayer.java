package org.qpneruy.clashArena.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.qpneruy.clashArena.model.Rank.UNRANKED;
//PLayer for plugin
public class ArenaPlayer {
    @Getter private final UUID UniqueId;
    @Getter private final String Name;

    @Getter @Setter private Rank Rank = UNRANKED;
    @Getter @Setter private int Stars;

    public ArenaPlayer(Player player) {
        this.UniqueId = player.getUniqueId();
        this.Name = player.getName();
    }
}
