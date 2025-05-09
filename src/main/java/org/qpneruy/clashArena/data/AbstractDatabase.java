package org.qpneruy.clashArena.data;

import com.zaxxer.hikari.HikariDataSource;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.data.databaseManager.DatabaseManager;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;


public abstract class AbstractDatabase {

    protected final String databaseName;
    protected final String databaseFileBasePath;
    protected final HikariDataSource dataSource;

    protected static final int MAX_CONCURRENT_WRITES = 5;
    protected final Semaphore writeSemaphore;

    public AbstractDatabase(String dbName) {
        this.databaseName = dbName;

        File dbFolder = new File(ClashArena.instance.getDataFolder(), "Database");
        this.databaseFileBasePath = new File(dbFolder, dbName).getAbsolutePath();

        DatabaseManager.initializeDataSource(this.databaseFileBasePath);
        this.dataSource = DatabaseManager.getDataSource(this.databaseFileBasePath);

        this.writeSemaphore = new Semaphore(MAX_CONCURRENT_WRITES);
        if (this.dataSource != null) {
            createTableIfNotExists();
        } else {
            ClashArena.instance.getLogger().severe("DataSource was null for " + dbName + ". Table creation skipped.");
        }
    }

    protected abstract void createTableIfNotExists();

    protected Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("DataSource is not available or closed for " + databaseName);
        }
        return dataSource.getConnection();
    }


}