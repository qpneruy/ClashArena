package org.qpneruy.clashArena.data;

import org.qpneruy.clashArena.model.ArenaPlayer;

import java.util.Optional;
import java.util.UUID;

public class t {
//todo: trien khai database tiep -> co lore update vao menu v.v
    public static void main(String[] args) {

        ArenaPlayerRepository repository = new ArenaPlayerRepository("plugins/clashArena/database");

        Player tinhtaken = new Player("TinhTaken", UUID.fromString("f7a7a1a2-876c-4317-8a1b-ab847b0daf4e"));
        ArenaPlayer player = new ArenaPlayer(tinhtaken);
        repository.save(player);

        Optional<ArenaPlayer> foundPlayer = repository.findById(player.getUniqueId());
        foundPlayer.ifPresent(p -> System.out.println("Found: " + p.getName()));


        repository.delete(player.getUniqueId());

    }
}
