package org.qpneruy.clashArena.ArenaManager.worldManager.Schematic;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.bukkit.Location;
import org.bukkit.World;
import org.qpneruy.clashArena.utils.ClashArenaLogger;
import org.qpneruy.clashArena.ArenaManager.worldManager.Schematic.paster.SchematicPaster;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;

public abstract class AbstractSchematicPaster implements SchematicPaster {
    protected record ClipboardData(Clipboard clipboard, com.sk89q.worldedit.world.World worldEditWorld) {}
    /**
     * Loads a schematic file into a Clipboard and adapts the Bukkit World.
     *
     * @param file     The schematic file to load.
     * @param location The location providing the world context.
     * @return A ClipboardData record containing the loaded Clipboard and WorldEdit World.
     * @throws CompletionException if the format is unsupported or reading fails.
     */
    protected ClipboardData loadClipboardAndWorld(File file, Location location) {
        Objects.requireNonNull(file, "File cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        World bukkitWorld = Objects.requireNonNull(location.getWorld(), "Location world cannot be null");

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new CompletionException(new IOException("Unsupported schematic format for file: " + file.getName()));
        }

        Clipboard clipboard;
        try (FileInputStream fis = new FileInputStream(file);
             ClipboardReader reader = format.getReader(fis)) {
            clipboard = reader.read();
        } catch (IOException e) {
            ClashArenaLogger.log(Level.SEVERE, "Failed to read schematic file: " + file.getAbsolutePath() + "\n" + e);
            throw new CompletionException("Failed to read schematic file", e);
        }

        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(bukkitWorld);
        return new ClipboardData(clipboard, weWorld);
    }
    protected void adjustLocation(Location location) {
        int newLength = (int) (location.getBlockX() / 2.00);
        int newWidth = (int) (location.getBlockZ() / 2.00);
        int newHeight = (int) (location.getBlockY() / 2.00);
        location.subtract(newWidth, newHeight, newLength);
    }

}