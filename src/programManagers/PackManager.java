package programManagers;
import animals.Wolf;

import java.util.*;

public class PackManager {
    private static List<Set<Wolf>> packs = new ArrayList<>();
    private static Map<Wolf, Set<Wolf>> alphaToPackMap = new HashMap<>();

    public static void addWolfToPack(Wolf wolf) {
        for (Set<Wolf> pack : packs) {
            if (pack.size() < wolf.maxPacksize()) {
                pack.add(wolf);
                if (pack.size() == 1) { // Check if the new wolf is the first in its pack
                    alphaToPackMap.put(wolf, pack);
                }
                return;
            }
        }
        // If no available packs, create a new pack
        createNewPack(wolf);
    }

    private static void createNewPack(Wolf alpha) {
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
        return alphaToPackMap.getOrDefault(wolf, Collections.emptySet());
    }
}