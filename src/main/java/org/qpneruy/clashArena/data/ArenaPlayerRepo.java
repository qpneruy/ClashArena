package org.qpneruy.clashArena.data;

import org.bukkit.Bukkit;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.data.databaseManager.DatabaseManager;
import org.qpneruy.clashArena.model.ArenaPlayer;
import org.qpneruy.clashArena.utils.ClashArenaLogger;
import org.qpneruy.clashArena.utils.enums.ConsoleColor;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ArenaPlayerRepo extends AbstractDatabase {

    public ArenaPlayerRepo() {
        super("ArenaPlayer");
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS arena_players (
                    unique_id CHAR(36) PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    rank VARCHAR(50) DEFAULT 'UNRANKED' NOT NULL,
                    stars INT DEFAULT 0 NOT NULL,
                    create_account_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
                );
                """;

        try (Connection connection = super.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            ClashArenaLogger.info("Table 'arena_players' checked/created successfully for database: " + databaseName);
        } catch (SQLException e) {
            ClashArenaLogger.log(Level.SEVERE,"Failed to create 'arena_players' table for database: " + databaseName + e);
            throw new RuntimeException("Failed to create arena_players table for " + databaseName, e);
        }
    }

    private boolean performStarsUpdate(UUID uniqueId, int newStars) {
        String sql = "UPDATE arena_players SET stars = ? WHERE unique_id = ?";
        try (Connection connection = super.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, newStars);
            stmt.setString(2, uniqueId.toString());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ClashArenaLogger.info(ConsoleColor.GREEN + "Updated stars for " + ConsoleColor.YELLOW + uniqueId + ConsoleColor.GREEN + " to " + ConsoleColor.CYAN + newStars);
                return true;
            } else {
                ClashArenaLogger.warn(ConsoleColor.YELLOW + "No player found with UUID " + uniqueId + " to update stars, or stars value unchanged.");
                return false;
            }
        } catch (SQLException e) {
            ClashArenaLogger.log(Level.SEVERE,  "Failed to update player stars for UUID: " + uniqueId + e);
            return false;
        }
    }

    public boolean updateStars(UUID uniqueId, int newStars) {
        try {
            writeSemaphore.acquire();
            try {
                return performStarsUpdate(uniqueId, newStars);
            } finally {
                writeSemaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ClashArenaLogger.warn("Update stars operation for UUID " + uniqueId + " was interrupted.");
            return false;
        }
    }


    public CompletableFuture<Boolean> updateStarsAsync(UUID uniqueId, int newStars) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return updateStars(uniqueId, newStars);
            } catch (Exception e) {
                ClashArenaLogger.log(Level.SEVERE,  "Exception in updateStarsAsync for UUID: " + uniqueId + e);
                return false;
            }
        }, runnable -> Bukkit.getScheduler().runTaskAsynchronously(ClashArena.instance, runnable));
    }

    
    public boolean save(ArenaPlayer player) {
//        String sql = """
//        MERGE INTO arena_players (unique_id, name, rank, stars, create_account_time)
//        KEY (unique_id)
//        VALUES (?, ?, ?, ?, COALESCE((SELECT create_account_time FROM arena_players WHERE unique_id = ?), CURRENT_TIMESTAMP));
//        """;

        String mergeSql = """
        MERGE INTO arena_players (unique_id, name, rank, stars)
        KEY (unique_id)
        VALUES (?, ?, ?, ?);
        """;


        try {
            writeSemaphore.acquire();
            try (Connection connection = super.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(mergeSql)) {

                stmt.setString(1, player.getUniqueId().toString());
                stmt.setString(2, player.getName());
                stmt.setString(3, player.getPlayerRank().getRank().name());
                stmt.setInt(4, player.getPlayerRank().getStars());
                stmt.executeUpdate();
                ClashArenaLogger.info(ConsoleColor.GREEN + "Saved/Merged " + ConsoleColor.BLUE + player.getName() + ConsoleColor.GREEN + " with UUID: " + ConsoleColor.YELLOW + player.getUniqueId());
                return true; 
            } catch (SQLException e) {
                ClashArenaLogger.log(Level.SEVERE, "Failed to save ArenaPlayer " + player.getName() + " with UUID " + player.getUniqueId() + e);
                return false;
            } finally {
                writeSemaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ClashArenaLogger.warn("Save operation for ArenaPlayer " + player.getName() + " was interrupted.");
            return false;
        }
    }

    public CompletableFuture<Boolean> saveAsync(ArenaPlayer player) {
        return CompletableFuture.supplyAsync(() -> save(player),
                runnable -> Bukkit.getScheduler().runTaskAsynchronously(ClashArena.instance, runnable));
    }


    public Optional<ArenaPlayer> findById(UUID uniqueId) {
        String sql = "SELECT unique_id, name, rank, stars, create_account_time FROM arena_players WHERE unique_id = ?";

        try (Connection connection = super.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, uniqueId.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ArenaPlayer player = new ArenaPlayer(rs);
                return Optional.of(player);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            ClashArenaLogger.log(Level.SEVERE, "Failed to find ArenaPlayer by ID: " + uniqueId + e);
            return Optional.empty();
        }
    }

    public CompletableFuture<Optional<ArenaPlayer>> findByIdAsync(UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> findById(uniqueId),
                runnable -> Bukkit.getScheduler().runTaskAsynchronously(ClashArena.instance, runnable));
    }


    public boolean delete(UUID uniqueId) {
        String sql = "DELETE FROM arena_players WHERE unique_id = ?";
        try {
            writeSemaphore.acquire();
            try (Connection connection = super.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, uniqueId.toString());
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    ClashArenaLogger.info(ConsoleColor.RED + "Deleted player with UUID: " + ConsoleColor.YELLOW + uniqueId);
                    return true;
                } else {
                    ClashArenaLogger.warn(ConsoleColor.YELLOW + "No player found to delete with UUID: " + ConsoleColor.YELLOW + uniqueId);
                    return false;
                }
            } catch (SQLException e) {
                ClashArenaLogger.log(Level.SEVERE, "Failed to delete ArenaPlayer with UUID: " + uniqueId + e);
                return false;
            } finally {
                writeSemaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ClashArenaLogger.warn("Delete operation for UUID " + uniqueId + " was interrupted.");
            return false;
        }
    }


    public CompletableFuture<Boolean> deleteAsync(UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> delete(uniqueId),
                runnable -> Bukkit.getScheduler().runTaskAsynchronously(ClashArena.instance, runnable));
    }

    /**
     * Closes the DataSource associated with this repository.
     * Typically, you would call DatabaseManager.closeAllDataSources() on plugin disable.
     * This method is provided for specific scenarios where individual closure is needed.
     */
    public void close() {
        ClashArenaLogger.info("Attempting to close DataSource for repository: " + databaseName);
        // `databaseFileBasePath` là trường protected trong AbstractDatabase
        DatabaseManager.closeDataSource(super.databaseFileBasePath);
    }
}