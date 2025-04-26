package org.qpneruy.clashArena.worldManager.chunkProvider;

import org.bukkit.Location;
import org.bukkit.World;
import org.qpneruy.clashArena.Party.Mode;
import org.qpneruy.clashArena.utils.game.Yaw;

import java.util.HashSet;
import java.util.Set;

public class Utils {
    public static Set<Location> genLocation(World world, Location location, Mode type) {
        if (type == Mode.SOLO) return Set.of(location);
        Set<Location> newLocations = new HashSet<>();
        Yaw yaw = Utils.YawLogic(location);
        if (yaw == Yaw.NORTH || yaw == Yaw.SOUTH) {
            double[] newLoc = genLocation(location.getX(), type.getRequiredSize());
            for (double v : newLoc) {
                newLocations.add(new Location(world, v, location.getY(), location.getZ()));
            }
        } else {
            //East, West
            double[] newLoc = genLocation(location.getZ(), type.getRequiredSize());
            for (double v : newLoc) {
                newLocations.add(new Location(world, location.getX(), location.getY(), v));
            }
        }
        return newLocations;
    }
    private static double[] genLocation(double Loc0, int teamSize) {
        double[] newLoc = new double[10];
        newLoc[0] = Loc0;
        int heso = 2;
        newLoc[1] = newLoc[0] - heso;
        for (int c = 2; c <= teamSize; c++) {
            if (c % 2 == 0) {
                newLoc[c] = newLoc[c - 2] + heso;
            } else {
                newLoc[c] = newLoc[c - 2] - heso;
            }
        }
        return newLoc;
    }
    public static Yaw YawLogic(Location location) {
        float yaw = location.getYaw();
        yaw = (yaw % 360 + 360) % 360;
        if (yaw > 45 && yaw < 135) {
            return Yaw.WEST;
        } else if (yaw > 135 && yaw < 225) {
            return Yaw.NORTH;
        } else if (yaw > 225 && yaw < 315) {
            return Yaw.EAST;
        } else {
            return Yaw.SOUTH;
        }
    }
}
