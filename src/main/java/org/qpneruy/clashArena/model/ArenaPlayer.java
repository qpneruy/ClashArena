package org.qpneruy.clashArena.model;

import com.alessiodp.parties.api.Parties;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.qpneruy.clashArena.model.Rank.UNRANKED;
import static org.qpneruy.clashArena.model.RankOperation.ADVANCE;
import static org.qpneruy.clashArena.model.RankOperation.DEMOTE;

public class ArenaPlayer {
    @Getter private final UUID UniqueId;
    @Getter private final String Name;
    @Getter @Setter private Rank playerRank = UNRANKED;
    @Getter @Setter private int Stars = 0;
    @Getter @Setter Parties parties = null;

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
    public void changeStatus() {
        this.Status = (this.Status == ReadyStatus.READY) ? ReadyStatus.NOT_READY : ReadyStatus.READY;
    }
    /**
     * Updates the player's star count by the specified amount.
     * Positive values add stars, negative values remove stars.
     *
     * @param amount The amount to change stars by (typically +x or -x)
     */
    public void updateStars(int amount) {
        if (amount > 0) {
            this.Stars += amount;
            if (this.Stars >= 5 && playerRank != Rank.CHAMPION) {
                updateRank(ADVANCE);
                this.Stars = 0;
            }
        } else if (amount < 0) {
            int starsToRemove = Math.abs(amount);

            while (starsToRemove > 0) {
                if (this.Stars > 0) {
                    this.Stars--;
                } else {
                    updateRank(DEMOTE);
                    if (playerRank != UNRANKED) {
                        this.Stars = 4;
                    }
                }
                starsToRemove--;
            }
        }
    }

    /**
     * Updates the player's rank based on the specified operation.
     *
     * @param operation The rank operation to perform (ADVANCE or DEMOTE)
     */
    public void updateRank(RankOperation operation) {
        int currentIndex = playerRank.ordinal();
        Rank[] ranks = Rank.values();

        if (operation == RankOperation.ADVANCE) {
            if (currentIndex < ranks.length - 1) playerRank = ranks[currentIndex + 1];

        } else if (operation == DEMOTE) {
            if (currentIndex > 0) {
                playerRank = ranks[currentIndex - 1];
            } else if (playerRank != UNRANKED) {
                playerRank = UNRANKED;
            }
        }
    }

}