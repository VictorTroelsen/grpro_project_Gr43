package actions;

import animals.Wolf;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WolfPack {
    private final Map<Wolf, Set<Wolf>> packs;
    private WolfDen den;

    public WolfPack() {
        packs = new HashMap<>();
    }

    public void createPack(Wolf alpha) {
        if (!packs.containsKey(alpha)) {
            packs.put(alpha, new HashSet<>());
        }
    }

    public void addToPack(Wolf alpha, Wolf member) {
        if (packs.containsKey(alpha)) {
            packs.get(alpha).add(member);
        } else {
            throw new IllegalArgumentException("Alpha must be in pack before adding member.");
        }
    }

    public void removeFromPack(Wolf alpha, Wolf member) {
        if (packs.containsKey(alpha)) {
            packs.get(alpha).remove(member);
        } else {
            throw new IllegalArgumentException("Alpha must be in pack before removing member.");
        }
    }

    public void disbandPack(Wolf alpha) {
        packs.remove(alpha);
    }

    // Tjek, om flokken allerede har en hule
    public boolean hasDen() {
        return den != null;
    }

    // Returner hulen
    public WolfDen getDen() {
        return den;
    }

    // Sæt hulen til flokken
    public void setDen(WolfDen den) {
        this.den = den;
    }

    public Set<Wolf> getPackMembers(Wolf alpha) {
        return packs.getOrDefault(alpha, Set.of());
    }

    public void movePack(Wolf alpha, Location newLocation, World world) {
        if (alpha == null || newLocation == null || world == null) {
            throw new IllegalArgumentException("Alpha, location, or world cannot be null");
        }

        if (!world.contains(alpha)) {
            throw new IllegalArgumentException("Alpha is not on the map");
        }

        // Check if the destination location is valid for the alpha
        if (!world.isTileEmpty(newLocation)) {
            System.out.println("[DEBUG] Cannot move pack led by Alpha Wolf #" + alpha.getId() + " to " + newLocation
                    + ": Target tile is occupied.");
            return; // Skip moving the pack if the destination is not valid
        }

        // Get the pack members
        Set<Wolf> packMembers = getPackMembers(alpha);

        // Fjern ulve, der ikke længere eksisterer i verden
        packMembers.removeIf(wolf -> !world.contains(wolf));

        // Flyt flokkens medlemmer én ad gangen
        for (Wolf wolf : packMembers) {
            if (world.contains(wolf)) { // Check, at ulven stadig eksisterer
                try {
                    Location currentLocation = world.getLocation(wolf); // Valider, at vi kan hente lokationen
                    Location followLocation = calculateFollowLocation(currentLocation, newLocation, world);

                    if (followLocation != null) {
                        world.move(wolf, followLocation);
                        System.out.println("[DEBUG] Moved Wolf #" + wolf.getId() + " to " + followLocation);
                    } else {
                        System.out.println("[DEBUG] Could not find valid location to move Wolf #" + wolf.getId());
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("[DEBUG] Failed to move Wolf #" + wolf.getId() + ": " + e.getMessage());
                }
            }
        }

        // Flyt alpha til den ønskede nye position
        try {
            world.move(alpha, newLocation);
            System.out.println("[DEBUG] Moved Alpha Wolf #" + alpha.getId() + " to " + newLocation);
        } catch (IllegalArgumentException e) {
            System.out.println("[DEBUG] Failed to move Alpha Wolf #" + alpha.getId() + ": " + e.getMessage());
        }

        // Hvis flokken bliver tom, opløses den
        if (packMembers.isEmpty()) {
            disbandPack(alpha);
            System.out.println("[DEBUG] The pack led by Alpha Wolf #" + alpha.getId() + " has been disbanded.");
        }
    }

    public boolean isAlpha(Wolf wolf) {
        return packs.containsKey(wolf);
    }

    public Set<Wolf> getAllAlphas() {
        return packs.keySet(); // Returnér alle alpha-ulve som et sæt
    }


    // (Privat) Beregn en lokation tættere på alpha
    private Location calculateFollowLocation(Location member, Location alpha, World world) {
        int xDiff = alpha.getX() - member.getX();
        int yDiff = alpha.getY() - member.getY();

        // Bevægelse mod alpha (x og y retning)
        int xStep = Integer.compare(xDiff, 0);
        int yStep = Integer.compare(yDiff, 0);

        Location newLocation = new Location(member.getX() + xStep, member.getY() + yStep);

        // Tjek først den beregnede position
        if (world.isTileEmpty(newLocation)) {
            return newLocation;
        }

        // Hvis første placering ikke er ledig, prøv nærliggende tiles
        Set<Location> surrounding = world.getSurroundingTiles(alpha, 1); // Få nabotiles
        for (Location loc : surrounding) {
            if (world.isTileEmpty(loc)) {
                return loc;
            }
        }

        return null; // Send null, hvis ingen gyldige placeringer findes
    }

    public boolean isPartOfPack(Wolf wolf) {
        for (Set<Wolf> packMembers : packs.values()) {
            if (packMembers.contains(wolf)) {
                return true; // Ulven findes i mindst én af flokkene
            }
        }
        return false; // Ulven er ikke en del af nogen flok
    }


}
