package actions;

import animals.Wolf;
import itumulator.world.Location;

import java.util.*;

public class PackManager {
    private static List<Set<Wolf>> packs = new ArrayList<>();
    private static Map<Wolf, Set<Wolf>> alphaToPackMap = new HashMap<>();
    private static Map<Wolf, Location> wolfLocations = new HashMap<>();

    public static void addWolfToPack(Wolf wolf, Wolf alpha) {
        Set<Wolf> pack = alphaToPackMap.get(alpha);
        if (pack == null) {
            pack = new HashSet<>();
            packs.add(pack);
            alphaToPackMap.put(alpha, pack);
        }
        pack.add(wolf);
        wolfLocations.put(wolf, wolf.getWolfLocation());
        System.out.println("Wolf added to pack with alpha " + alpha + ": Current pack size " + pack.size());
    }

    public static void createNewPack(Wolf alpha) {
        if (alpha == null) {
            throw new IllegalArgumentException("Alpha cannot be null");
        }
        Set<Wolf> newPack = new HashSet<>();
        newPack.add(alpha);
        packs.add(newPack);
        alphaToPackMap.put(alpha, newPack);
        Wolf.alphaWolf = alpha;
        System.out.println("A new pack has been formed with " + alpha + " as the alpha.");
    }

    public static Set<Wolf> getPack(Wolf wolf) {
        for (Set<Wolf> pack : packs) {
            if (pack.contains(wolf)) {
                return pack;
            }
        }
        return Collections.emptySet();
    }

    public static Location getWolfLocation(Wolf wolf) {
        return wolfLocations.get(wolf); // Hent ulvens seneste kendte lokation
    }

    public static void removeWolfFromPack(Wolf wolf) {
        for (Set<Wolf> pack : packs) {
            if (pack.contains(wolf)) {
                pack.remove(wolf);
                wolfLocations.remove(wolf);
                if (wolf.equals(Wolf.alphaWolf)) {
                    updateAlpha(pack);
                }
                if (pack.isEmpty()) {
                    packs.remove(pack);
                }
                break;
            }
        }
    }

    private static void updateAlpha(Set<Wolf> pack) {
        if (!pack.isEmpty()) {
            Wolf newAlpha = pack.iterator().next();
            alphaToPackMap.put(newAlpha, pack);
            Wolf.alphaWolf = newAlpha;
            System.out.println("New alpha has been chosen: " + newAlpha);
        } else {
            System.out.println("Pack is empty, no new alpha can be chosen.");
        }
    }
}