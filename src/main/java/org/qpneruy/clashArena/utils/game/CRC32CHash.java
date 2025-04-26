package org.qpneruy.clashArena.utils.game;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32C;

public class CRC32CHash {
    @Getter
    private static final CRC32CHash instance = new CRC32CHash();

    public static String CRC32C(File file) throws IOException {
        CRC32C crc32c = new CRC32C();
        ByteBuffer buffer = ByteBuffer.allocateDirect(8192);

        try (FileChannel channel = FileChannel.open(Path.of(file.getPath()), StandardOpenOption.READ)) {
            while (channel.read(buffer) != -1) {
                buffer.flip();
                crc32c.update(buffer);
                buffer.clear();
            }
        }

        String hash = Long.toHexString(crc32c.getValue());
        return hash;
    }

    public static String CRC32C(String data) {
        CRC32C crc32c = new CRC32C();
        crc32c.update(data.getBytes());
        return Long.toHexString(crc32c.getValue());
    }
}

