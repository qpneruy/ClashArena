package org.qpneruy.clashArena.data;

import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.model.ArenaPlayer;

import java.io.File;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class ArenaPlayerRepository {

    private final String databasePath;

    public ArenaPlayerRepository() {
        this.databasePath = new File(ClashArena.instance.getDataFolder(), "/Database/ArenaPlayer").getAbsolutePath();
        DatabaseManager.initializeDataSource(databasePath);
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS arena_players (
                    unique_id CHAR(36) PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    rank VARCHAR(50) DEFAULT 'UNRANKED' NOT NULL,
                    stars INT DEFAULT 0 NOT NULL,
                    create_account_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
                );
                """;

        try (Connection connection = DatabaseManager.getDataSource(databasePath).getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create arena_players table", e);
        }
    }

    public void save(ArenaPlayer player) {
        String sql = """
        MERGE INTO arena_players (unique_id, name, rank, stars)
        KEY (unique_id)
        VALUES (?, ?, ?, ?);
        """;

        try (Connection connection = DatabaseManager.getDataSource(databasePath).getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, player.getPlayerRank().getRank().name());
            stmt.setInt(4, player.getPlayerRank().getStars());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save ArenaPlayer", e);
        }
    }

    public Optional<ArenaPlayer> findById(UUID uniqueId) {
        String sql = "SELECT * FROM arena_players WHERE unique_id = ?";

        try (Connection connection = DatabaseManager.getDataSource(databasePath).getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, uniqueId.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ArenaPlayer player = new ArenaPlayer(rs);
                return Optional.of(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find ArenaPlayer by ID", e);
        }
        return Optional.empty();
    }

    public void delete(UUID uniqueId) {
        String sql = "DELETE FROM arena_players WHERE unique_id = ?";

        try (Connection connection = DatabaseManager.getDataSource(databasePath).getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, uniqueId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ArenaPlayer", e);
        }
    }

    public void close() {
        DatabaseManager.closeDataSource(databasePath);
    }
}
