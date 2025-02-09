package org.qpneruy.clashArena.menu.Gui.mainMenu;

import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.qpneruy.clashArena.ClashArena;

import java.util.HashMap;
import java.util.UUID;

public class FakeRoomCreator {

    public HashMap<UUID, Party> fakeRooms = new HashMap<>();

    public void createArrayRoom(int NumberOfRooms) {
        UUID fakePlayerUUID = UUID.randomUUID();
        PartyPlayer fakePlayer = ClashArena.parties.getPartyPlayer(fakePlayerUUID);
        for (int i = 0; i < NumberOfRooms; i++) ClashArena.parties.createParty("FakeRoom " + i, fakePlayer);
        fakeRooms.put(fakePlayerUUID, ClashArena.parties.getParty(fakePlayerUUID));
    }

    public void deleteRoom(UUID fakePlayerUUID) {
        if (fakeRooms.containsKey(fakePlayerUUID)) {
            ClashArena.parties.deleteParty(fakeRooms.get(fakePlayerUUID));
            fakeRooms.remove(fakePlayerUUID);
        }
    }

    public void addRoom(UUID fakePlayerUUID) {
        PartyPlayer fakePlayer = ClashArena.parties.getPartyPlayer(fakePlayerUUID);
        ClashArena.parties.createParty("FakeRoom", fakePlayer);
        fakeRooms.put(fakePlayerUUID, ClashArena.parties.getParty(fakePlayerUUID));
    }
}
