package org.qpneruy.clashArena.ArenaManager.worldManager.Schematic.paster;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.qpneruy.clashArena.utils.ClashArenaLogger;
import org.qpneruy.clashArena.ArenaManager.worldManager.Schematic.AbstractSchematicPaster;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

public class FawePaster extends AbstractSchematicPaster {

    private final ExecutorService executorService;
    private static final Object mutex = new Object();

    public FawePaster(ExecutorService executorService) {
        super();
        this.executorService = Objects.requireNonNull(executorService, "ExecutorService cannot be null");
    }

    @Override
    public CompletableFuture<Void> paste(File file, Location location, boolean ignoreAirBlock) {
        Objects.requireNonNull(file, "File cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(location.getWorld(), "Location world cannot be null");

        return CompletableFuture.runAsync(() -> {

            ClipboardData clipboardData = loadClipboardAndWorld(file, location);
            synchronized (mutex) {
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(clipboardData.worldEditWorld())) {
                    Operation operation = new ClipboardHolder(clipboardData.clipboard())
                            .createPaste(editSession)
                            .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                            .copyEntities(true)
                            .ignoreAirBlocks(ignoreAirBlock)
                            .build();

                    Operations.complete(operation);
                    Operations.complete(editSession.commit());
                    ClashArenaLogger.debug(ClashArenaLogger.debugLevel(), Level.INFO, "FAWE paste completed for: " + file.getName());

                } catch (WorldEditException e) {
                    ClashArenaLogger.log(Level.SEVERE, "Error during FAWE paste operation for: " + file.getName() + "\n" + e);
                    throw new CompletionException("FAWE paste operation failed", e);
                }
            }
        }, executorService).exceptionally(ex -> {

            ClashArenaLogger.log(Level.SEVERE, String.valueOf(ex.getCause() != null ? ex.getCause() : ex));
            return null;
        });
    }
}