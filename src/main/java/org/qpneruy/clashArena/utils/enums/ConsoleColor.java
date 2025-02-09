package org.qpneruy.clashArena.utils.enums;

/**
 * Enum representing ANSI color codes and special characters for console output.
 * Provides constants for text formatting and symbols.
 */
public enum ConsoleColor {
    // Text colors
    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    PURPLE("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m"),

    // Text formatting
    RESET("\u001b[0m"),
    BOLD("\u001b[1m"),
    ITALICS("\u001b[2m"),
    UNDERLINE("\u001b[4m"),

    // Special characters
    CHECK_MARK("✓"),
    ERROR_MARK("✗");

    private final String ansiCode;

    ConsoleColor(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    /**
     * Gets the ANSI code for this color/format.
     * @return ANSI escape sequence
     */
    public String getAnsiColor() {
        return this.ansiCode;
    }

    @Override
    public String toString() {
        return this.ansiCode;
    }
}