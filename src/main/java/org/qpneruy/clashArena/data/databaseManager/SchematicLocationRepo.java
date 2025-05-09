package org.qpneruy.clashArena.data.databaseManager;

import org.qpneruy.clashArena.data.AbstractDatabase;

public class SchematicLocationRepo extends AbstractDatabase {
    public SchematicLocationRepo() {
        super("SchematicLocation");
    }

    @Override
    protected void createTableIfNotExists() {
    }
}
