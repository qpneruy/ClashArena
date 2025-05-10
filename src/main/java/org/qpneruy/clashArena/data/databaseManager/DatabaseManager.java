package org.qpneruy.clashArena.data.databaseManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.qpneruy.clashArena.ClashArena; // Cần import này

import java.io.File; // Cần import này
// import java.nio.file.Path; // Không cần thiết nếu dùng File
import java.util.ArrayList; // Cần import này
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level; // Cần import này

public class DatabaseManager {
    private static final Map<String, CompletableFuture<HikariDataSource>> dataSourceFuturesMap = new ConcurrentHashMap<>();

    public static void initializeDataSource(String databaseFileBasePath) {
        dataSourceFuturesMap.computeIfAbsent(databaseFileBasePath, path -> CompletableFuture.supplyAsync(() -> {
            ClashArena.instance.getLogger().info("Initializing DataSource for: " + path);
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            String dbUrl = "jdbc:h2:file:" + path + ";AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(300000);
            config.setMaxLifetime(1800000);

            String poolName = "Pool-" + new File(path).getName();
            config.setPoolName(poolName);

            try {
                HikariDataSource ds = new HikariDataSource(config);
                ClashArena.instance.getLogger().info("Successfully initialized DataSource for: " + path + " with pool name: " + poolName);
                return ds;
            } catch (Exception e) {
                ClashArena.instance.getLogger().log(Level.SEVERE, "Failed to create HikariDataSource for: " + path, e);
                throw new RuntimeException("Failed to create HikariDataSource for: " + path, e);
            }
        }));
    }

    public static HikariDataSource getDataSource(String databaseFileBasePath) {
        CompletableFuture<HikariDataSource> future = dataSourceFuturesMap.get(databaseFileBasePath);
        if (future == null) {
            ClashArena.instance.getLogger().severe("DataSource future not found for path (getDataSource called before initializeDataSource?): " + databaseFileBasePath);
            initializeDataSource(databaseFileBasePath);
            future = dataSourceFuturesMap.get(databaseFileBasePath);
            if (future == null) {
                throw new RuntimeException("Catastrophic failure: DataSource future still null after re-attempted initialization for: " + databaseFileBasePath);
            }
        }

        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ClashArena.instance.getLogger().log(Level.SEVERE, "Interrupted while waiting for DataSource: " + databaseFileBasePath, e);
            throw new RuntimeException("Interrupted while waiting for DataSource: " + databaseFileBasePath, e);
        } catch (ExecutionException e) {
            ClashArena.instance.getLogger().log(Level.SEVERE, "Failed to initialize DataSource (execution exception): " + databaseFileBasePath, e.getCause());
            throw new RuntimeException("Failed to initialize DataSource for path: " + databaseFileBasePath, e.getCause());
        }
    }

    public static void closeDataSource(String databaseFileBasePath) {
        CompletableFuture<HikariDataSource> futureDataSource = dataSourceFuturesMap.remove(databaseFileBasePath);
        if (futureDataSource != null) {
            if (futureDataSource.isDone() && !futureDataSource.isCompletedExceptionally()) {
                try {
                    HikariDataSource dataSource = futureDataSource.getNow(null);
                    if (dataSource != null && !dataSource.isClosed()) {
                        dataSource.close();
                        ClashArena.instance.getLogger().info("Closed DataSource for: " + databaseFileBasePath);
                    }
                } catch (Exception e) {
                    ClashArena.instance.getLogger().log(Level.SEVERE, "Error closing DataSource: " + databaseFileBasePath, e);
                }
            } else if (futureDataSource.isCompletedExceptionally()) {
                ClashArena.instance.getLogger().warning("DataSource for " + databaseFileBasePath + " was not closed because its initialization failed.");
                futureDataSource.exceptionally(ex -> {
                    ClashArena.instance.getLogger().log(Level.FINE, "Original initialization failure for " + databaseFileBasePath, ex);
                    return null;
                });
            } else {
                ClashArena.instance.getLogger().info("DataSource for " + databaseFileBasePath + " was not closed as it was not yet (or no longer) fully initialized or already removed.");
            }
        } else {
            ClashArena.instance.getLogger().info("No active DataSource future to close for path: " + databaseFileBasePath);
        }
    }

    public static void closeAllDataSources() {
        ClashArena.instance.getLogger().info("Closing all DataSources...");
        for (String databasePath : new ArrayList<>(dataSourceFuturesMap.keySet())) {
            closeDataSource(databasePath);
        }
        dataSourceFuturesMap.clear();
        ClashArena.instance.getLogger().info("All DataSources closed.");
    }
}