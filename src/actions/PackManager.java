package actions;

/*import animals.WolfPrøve;
import itumulator.world.Location;

import java.util.*;

public class PackManager {
    private static List<Set<WolfPrøve>> packs = new ArrayList<>();
    private static Map<WolfPrøve, Set<WolfPrøve>> alphaToPackMap = new HashMap<>();
    public static Map<WolfPrøve, Location> wolfLocations = new HashMap<>();

    public static void addWolfToPack(WolfPrøve wolf, WolfPrøve alpha) {
        Set<WolfPrøve> pack = alphaToPackMap.get(alpha);
        if (pack == null) {
            pack = new HashSet<>();
            packs.add(pack);
            alphaToPackMap.put(alpha, pack);
        }
        pack.add(wolf);
        wolfLocations.put(wolf, wolf.getWolfLocation());
        System.out.println("Wolf added to pack with alpha " + alpha + ": Current pack size " + pack.size());
    }

    public static void createNewPack(WolfPrøve alpha) {
        if (alpha == null) {
            throw new IllegalArgumentException("Alpha cannot be null");
        }
        Set<WolfPrøve> newPack = new HashSet<>();
        newPack.add(alpha);
        packs.add(newPack);
        alphaToPackMap.put(alpha, newPack);
        WolfPrøve.alphaWolf = alpha;
        System.out.println("A new pack has been formed with " + alpha + " as the alpha.");
    }

    public static Set<WolfPrøve> getPack(WolfPrøve wolf) {
        for (Set<WolfPrøve> pack : packs) {
            if (pack.contains(wolf)) {
                return pack;
            }
        }
        return Collections.emptySet();
    }

    public static Location getWolfLocation(WolfPrøve wolf) {
        return wolfLocations.get(wolf); // Hent ulvens seneste kendte lokation
    }

    public static void removeWolfFromPack(WolfPrøve wolf) {
        for (Set<WolfPrøve> pack : packs) {
            if (pack.contains(wolf)) {
                pack.remove(wolf);
                wolfLocations.remove(wolf);
                if (wolf.equals(WolfPrøve.alphaWolf)) {
                    updateAlpha(pack);
                }
                if (pack.isEmpty()) {
                    packs.remove(pack);
                }
                break;
            }
        }
    }

    private static void updateAlpha(Set<WolfPrøve> pack) {
        if (!pack.isEmpty()) {
            WolfPrøve newAlpha = pack.iterator().next();
            alphaToPackMap.put(newAlpha, pack);
            WolfPrøve.alphaWolf = newAlpha;
            System.out.println("New alpha has been chosen: " + newAlpha);
        } else {
            System.out.println("Pack is empty, no new alpha can be chosen.");
        }
    }
}
*/