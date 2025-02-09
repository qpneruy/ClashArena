package org.qpneruy.clashArena.utils.enums;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enum representing different debug levels for logging.
 * Levels are ordered by priority from LOWEST (0) to HIGHEST (4).
 */
public enum DebugLevel {
    LOWEST(0, "LOWEST"),
    LOW(1, "LOW"),
    MEDIUM(2, "MEDIUM"),
    HIGH(3, "HIGH"),
    HIGHEST(4, "HIGHEST");

    private final String[] names;

    @Getter
    private final int priority;

    DebugLevel(final int priority, @NotNull final String... names) {
        this.priority = priority;
        this.names = names;
    }

    /** Map of lowercase level names to their corresponding DebugLevel */
    private static final Map<String, DebugLevel> LEVELS = Arrays.stream(values())
            .flatMap(level -> Arrays.stream(level.names)
                    .map(name -> Map.entry(name.toLowerCase(Locale.ROOT), level)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    /**
     * Looks up a DebugLevel by its name (case-insensitive).
     * @param name Name of the debug level
     * @return Corresponding DebugLevel or null if not found
     */
    public static @Nullable DebugLevel getByName(@NotNull final String name) {
        return LEVELS.get(name.toLowerCase(Locale.ROOT));
    }
}