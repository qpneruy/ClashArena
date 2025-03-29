package org.qpneruy.clashArena.model;

import lombok.Getter;
import lombok.Setter;

import static org.qpneruy.clashArena.model.Rank.UNRANKED;
import static org.qpneruy.clashArena.model.RankOperation.ADVANCE;
import static org.qpneruy.clashArena.model.RankOperation.DEMOTE;

@Setter @Getter
public class RankManager {
    private Rank rank;
    private int Stars;
    public RankManager(int Starts, Rank rank) {
        this.Stars = Starts;
        this.rank = rank;
    }

    public RankManager() {
        this.Stars = 0;
        this.rank = UNRANKED;
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
            if (this.Stars >= 5 && rank != Rank.CHAMPION) {
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
                    if (rank != UNRANKED) {
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
        int currentIndex = rank.ordinal();
        Rank[] ranks = Rank.values();

        if (operation == RankOperation.ADVANCE) {
            if (currentIndex < ranks.length - 1) rank = ranks[currentIndex + 1];

        } else if (operation == DEMOTE) {
            if (currentIndex > 0) {
                rank = ranks[currentIndex - 1];
            } else if (rank != UNRANKED) {
                rank = UNRANKED;
            }
        }
    }
}
