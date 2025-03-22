package org.qpneruy.clashArena.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class DatabaseManager {
    private static final Map<String, CompletableFuture<HikariDataSource>> dataSourceMap = new ConcurrentHashMap<>();

    public static void initializeDataSource(String databasePath) {
        dataSourceMap.computeIfAbsent(databasePath, path -> CompletableFuture.supplyAsync(() -> {
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("H2 Driver not found", e);
            }

            Path databaseFile = Path.of(databasePath);
            String dbUrl = "jdbc:h2:" + databaseFile + ";AUTO_SERVER=TRUE";

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            config.setLeakDetectionThreshold(0);
            config.setMetricRegistry(null);
            config.setHealthCheckRegistry(null);

            config.setPoolName("HikariPool-MatchMaking");
            config.setRegisterMbeans(false);
            return new HikariDataSource(config);
        }));
    }

    public static HikariDataSource getDataSource(String databasePath) {
        try {
            return dataSourceMap.get(databasePath).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to initialize data source for path: " + databasePath, e);
        }
    }

    public static void closeDataSource(String databasePath) {
        try {
            HikariDataSource dataSource = dataSourceMap.get(databasePath).get();
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to close data source for path: " + databasePath, e);
        }
    }

    public static void closeAllDataSources() {
        for (String databasePath : dataSourceMap.keySet()) {
            closeDataSource(databasePath);
        }
        dataSourceMap.clear();
    }
}