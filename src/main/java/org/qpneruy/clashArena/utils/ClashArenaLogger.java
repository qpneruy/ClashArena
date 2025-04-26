package org.qpneruy.clashArena.utils;

import org.jetbrains.annotations.NotNull;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.utils.enums.ConsoleColor;
import org.qpneruy.clashArena.utils.enums.DebugLevel;

import java.util.logging.Level;

/**
 * Central logging utility for the ClashArena plugin.
 * Provides different logging levels with color-coded output and debug capabilities.
 */
public class ClashArenaLogger {
    /** Current debug level setting for the logger */
    private static final DebugLevel DEBUG_LEVEL = DebugLevel.LOWEST;

    /** Minimum debug level required to print stack traces */
    private static final DebugLevel STACKTRACE_LEVEL = DebugLevel.MEDIUM;

    /**
     * Determines if stack traces should be printed based on current debug level.
     * @return true if current debug level is less than or equal to stack trace level
     */
    public static boolean shouldPrintStackTrace() {
        return DEBUG_LEVEL.getPriority() <= STACKTRACE_LEVEL.getPriority();
    }

    /**
     * Gets the current debug level.
     * @return current DebugLevel setting
     */
    public static DebugLevel debugLevel() {
        return DEBUG_LEVEL;
    }

    /**
     * Logs an exception with stack trace if debug level permits.
     * @param message Description of the error
     * @param throwable Exception to be logged
     */
    public static void printStacktrace(@NotNull final String message, @NotNull final Throwable throwable) {
        if (!shouldPrintStackTrace()) return;
        ClashArena.instance.getLogger().log(Level.SEVERE, message, throwable);
    }

    /**
     * Logs debug messages if they meet the minimum debug level requirement.
     * @param debugLevel Required debug level for message to be logged
     * @param level Logging severity level
     * @param messages Array of messages to be logged
     */
    public static void debug(@NotNull final DebugLevel debugLevel,
                             @NotNull final Level level,
                             @NotNull final String... messages) {
        if (DEBUG_LEVEL.getPriority() > debugLevel.getPriority()) return;

        String combinedMessage = String.join(System.lineSeparator(), messages);
        log(level, combinedMessage);
    }

    /**
     * Logs an informational message in white.
     * @param message Message to be logged
     */
    public static void info(@NotNull final String message) {
        logWithColor(Level.INFO, message, ConsoleColor.WHITE);
    }

    /**
     * Logs a success message in green.
     * @param message Message to be logged
     */
    public static void good( @NotNull final String message) {
        logWithColor(Level.INFO, message, ConsoleColor.GREEN);
    }

    /**
     * Logs a warning message in yellow.
     * @param message Message to be logged
     */
    public static void warn(@NotNull final String message) {
        logWithColor(Level.SEVERE, message, ConsoleColor.YELLOW);
    }

    /**
     * Logs an error message in red.
     * @param message Message to be logged
     */
    public static void error(@NotNull final String message) {
        logWithColor(Level.FINE, message, ConsoleColor.RED);
    }

    /**
     * Internal helper method to log colored messages.
     */
    private static void logWithColor(@NotNull final Level level,
                                     @NotNull final String message,
                                     @NotNull final ConsoleColor color) {
        if (DEBUG_LEVEL.getPriority() > debugLevel().getPriority()) return;
        log(level, color + message + ConsoleColor.RESET);
    }

    /**
     * Internal helper method to perform the actual logging.
     */
    public static void log(@NotNull final Level level, @NotNull final String message) {
        ClashArena.instance.getLogger().log(level, message);
    }
}