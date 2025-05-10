package org.qpneruy.clashArena.data.databaseManager;

import org.bukkit.Location;
import org.qpneruy.clashArena.data.AbstractDatabase;
import org.qpneruy.clashArena.data.TeamLocation;
import org.qpneruy.clashArena.utils.ClashArenaLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SchematicLocationRepo extends AbstractDatabase {

    public SchematicLocationRepo() {
        super("SchematicLocation");
    }

    @Override
    protected void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS Schematic_LookUp (
                    crc32Hash CHAR(8) PRIMARY KEY,
                    FileName VARCHAR(255) NOT NULL,
                    PlayerLocationColumns INTEGER NOT NULL
                );
                """;

        try (Connection connection = super.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            ClashArenaLogger.info("Table 'Schematic_LookUp' checked/created successfully for database: " + databaseName);
        } catch (SQLException e) {
            ClashArenaLogger.log(Level.SEVERE, "Failed to create 'Schematic_LookUp' table for database: " + databaseName, e);
            throw new RuntimeException("Failed to create Schematic_LookUp table for " + databaseName, e);
        }
    }

    public void saveSchematic(String CRC32Hash, String fileName, TeamLocation blueTeam, TeamLocation redTeam) {

        int numPlayerColumns = blueTeam.getTeamLocations().size();


        try (Connection connection = super.getConnection();
             Statement statement = connection.createStatement()) {

            String upsertLookupSql = "INSERT OR REPLACE INTO Schematic_LookUp (crc32Hash, FileName, PlayerLocationColumns) VALUES (?, ?, ?)";
            try (PreparedStatement lookupPstmt = connection.prepareStatement(upsertLookupSql)) {
                lookupPstmt.setString(1, CRC32Hash);
                lookupPstmt.setString(2, fileName);
                lookupPstmt.setInt(3, numPlayerColumns);
                lookupPstmt.executeUpdate();
                ClashArenaLogger.info("Upserted into Schematic_LookUp for " + CRC32Hash + " with " + numPlayerColumns + " player columns.");
            }

            String dropTableSql = "DROP TABLE IF EXISTS \"" + CRC32Hash + "\"";
            statement.executeUpdate(dropTableSql);
            // ClashArenaLogger.info("Dropped existing table (if any): " + CRC32Hash);

            StringBuilder createTableSqlBuilder = new StringBuilder("CREATE TABLE \"");
            createTableSqlBuilder.append(CRC32Hash).append("\" (team VARCHAR(10) NOT NULL");

            for (int i = 1; i <= numPlayerColumns; i++) {
                createTableSqlBuilder.append(", p").append(i).append("_loc VARCHAR(50)");
            }
            createTableSqlBuilder.append(");");
            String createTableSql = createTableSqlBuilder.toString();

            statement.executeUpdate(createTableSql);
            ClashArenaLogger.info("Created new table: " + CRC32Hash + " with " + numPlayerColumns + " player location columns.");

            insertTeamData(connection, CRC32Hash, "Blue", blueTeam, numPlayerColumns);
            insertTeamData(connection, CRC32Hash, "Red", redTeam, numPlayerColumns);

        } catch (SQLException e) {
            ClashArenaLogger.log(Level.SEVERE, "Failed to save schematic data for CRC32Hash: " + CRC32Hash, e);
            throw new RuntimeException("Failed to save schematic data for CRC32Hash: " + CRC32Hash, e);
        }
    }

    private void insertTeamData(Connection connection, String tableName, String teamName, TeamLocation teamLocation, int totalPlayerColumnsInTable) throws SQLException {
        List<Location> locations = teamLocation.getTeamLocations();
        int actualPlayerLocationsForThisTeam = locations.size();

        StringBuilder insertSqlBuilder = new StringBuilder("INSERT INTO \"");
        insertSqlBuilder.append(tableName).append("\" (team");
        StringBuilder valuesPlaceholders = new StringBuilder("VALUES (?");

        for (int i = 1; i <= totalPlayerColumnsInTable; i++) {
            insertSqlBuilder.append(", p").append(i).append("_loc");
            valuesPlaceholders.append(", ?");
        }
        insertSqlBuilder.append(") ");
        valuesPlaceholders.append(")");
        insertSqlBuilder.append(valuesPlaceholders.toString());
        String insertSql = insertSqlBuilder.toString();

        try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
            stmt.setString(1, teamName);

            for (int i = 0; i < totalPlayerColumnsInTable; i++) {
                int parameterIndex = 2 + i;
                if (i < actualPlayerLocationsForThisTeam) {
                    Location loc = locations.get(i);
                    String locStr = String.format("%.3f,%.3f,%.3f", loc.getX(), loc.getY(), loc.getZ());
                    stmt.setString(parameterIndex, locStr);
                } else {
                    stmt.setNull(parameterIndex, Types.VARCHAR);
                }
            }
            stmt.executeUpdate();
            // ClashArenaLogger.info("Inserted data for team: " + teamName + " into table: " + tableName);
        }
    }

    public Map<String, List<String>> getSchematicTeamLocations(String CRC32Hash) {
        int playerLocationColumns = -1;

        String lookupSql = "SELECT PlayerLocationColumns FROM Schematic_LookUp WHERE crc32Hash = ?";
        try (Connection connection = super.getConnection();
             PreparedStatement lookupPstmt = connection.prepareStatement(lookupSql)) {
            lookupPstmt.setString(1, CRC32Hash);
            ResultSet rsLookup = lookupPstmt.executeQuery();
            if (rsLookup.next()) {
                playerLocationColumns = rsLookup.getInt("PlayerLocationColumns");
            } else {
                ClashArenaLogger.warn("Schematic with CRC32Hash: " + CRC32Hash + " not found in Schematic_LookUp.");
                return null;
            }
        } catch (SQLException e) {
            ClashArenaLogger.log(Level.SEVERE, "Failed to query Schematic_LookUp for CRC32Hash: " + CRC32Hash, e);
            throw new RuntimeException("Failed to query Schematic_LookUp for CRC32Hash: " + CRC32Hash, e);
        }

        Map<String, List<String>> result = new HashMap<>();
        String dataSql;

        StringBuilder selectColumns = new StringBuilder("team");
        for (int i = 1; i <= playerLocationColumns; i++) {
            selectColumns.append(", p").append(i).append("_loc");
        }
        dataSql = "SELECT " + selectColumns + " FROM \"" + CRC32Hash + "\"";

        try (Connection connection = super.getConnection();
             PreparedStatement dataPstmt = connection.prepareStatement(dataSql)) {
            ResultSet rsData = dataPstmt.executeQuery();
            while (rsData.next()) {
                String teamName = rsData.getString("team");
                List<String> locations = new ArrayList<>();
                for (int i = 1; i <= playerLocationColumns; i++) {
                    String locString = rsData.getString("p" + i + "_loc");
                    if (locString != null) {
                        locations.add(locString);
                    }
                }
                result.put(teamName, locations);
            }
        } catch (SQLException e) {
            ClashArenaLogger.log(Level.SEVERE, "Failed to retrieve data from table: " + CRC32Hash, e);
            throw new RuntimeException("Failed to retrieve data from table: " + CRC32Hash, e);
        }
        return result;
    }
}