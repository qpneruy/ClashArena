package org.qpneruy.clashArena.model;

import com.alessiodp.parties.api.Parties;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ArenaPlayer {
    @Getter private final UUID UniqueId;
    @Getter private final String Name;
    @Getter private final RankManager PlayerRank;
    @Getter @Setter Parties parties = null;

    public ReadyStatus Status = ReadyStatus.NOT_READY;

    protected ArenaPlayer() {
        this.UniqueId = null;
        this.Name = null;
        this.PlayerRank = new RankManager();
    }

    public ArenaPlayer(Player player) {
        this.UniqueId = player.getUniqueId();
        this.Name = player.getName();
        this.PlayerRank = new RankManager();
    }

    public ArenaPlayer(ResultSet rs) throws SQLException {
        this.UniqueId = UUID.fromString(rs.getString("unique_id"));
        this.Name = rs.getString("name");
        this.PlayerRank = new RankManager(
                rs.getInt("stars"),
                Rank.valueOf(rs.getString("rank")
                ));
    }
    public void changeStatus() {
        this.Status = (this.Status == ReadyStatus.READY) ? ReadyStatus.NOT_READY : ReadyStatus.READY;
    }

    public void updateStars(int amount) {
        this.PlayerRank.updateStars(amount);
    }
}