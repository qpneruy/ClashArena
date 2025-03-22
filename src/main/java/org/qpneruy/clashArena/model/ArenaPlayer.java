package org.qpneruy.clashArena.model;

import lombok.Getter;
import lombok.Setter;

import org.qpneruy.clashArena.data.Player;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.qpneruy.clashArena.model.Rank.UNRANKED;

public class ArenaPlayer {
    @Getter private final UUID UniqueId;
    @Getter private final String Name;
    @Getter @Setter private Rank playerRank = UNRANKED;
    @Getter @Setter private int Stars = 0;

    public ReadyStatus Status = ReadyStatus.NOT_READY;

    protected ArenaPlayer() {
        this.UniqueId = null;
        this.Name = null;
    }

    public ArenaPlayer(Player player) {
        this.UniqueId = player.getUniqueId();
        this.Name = player.getName();
    }

    public ArenaPlayer(ResultSet rs) throws SQLException {
        this.UniqueId = UUID.fromString(rs.getString("unique_id"));
        this.Name = rs.getString("name");
        this.playerRank = Rank.valueOf(rs.getString("rank"));
        this.Stars = rs.getInt("stars");
    }

    public void addStar() {
        this.Stars++;
        if (this.Stars >= 5 && playerRank != Rank.CHAMPION) {
            advanceRank();
            this.Stars = 0;
        }
    }

    public void removeStar() {
        if (this.Stars > 0) {
            this.Stars--;
        } else {
            demoteRank();
            if (playerRank != UNRANKED) {
                this.Stars = 4;
            }
        }
    }

    public void advanceRank() {
        int currentIndex = getRankIndex(playerRank);
        Rank[] ranks = Rank.values();

        if (currentIndex < ranks.length - 1) {
            playerRank = ranks[currentIndex + 1];
        }
    }

    public void demoteRank() {
        int currentIndex = getRankIndex(playerRank);
        Rank[] ranks = Rank.values();

        if (currentIndex > 0 && playerRank != UNRANKED) {
            playerRank = ranks[currentIndex - 1];
        } else if (playerRank != UNRANKED && currentIndex == 0) {
            playerRank = UNRANKED;
        }
    }

    private int getRankIndex(Rank rank) {
        Rank[] ranks = Rank.values();
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i] == rank) {
                return i;
            }
        }
        return -1;
    }
}