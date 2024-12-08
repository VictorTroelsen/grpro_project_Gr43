package actions;

import animals.Wolf;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import itumulator.executable.Program;
import java.util.*;

public class WolfDen implements NonBlocking {
    private Location location;
    private List<Wolf> connectedWolves;
    private boolean hasReproduced;

    public WolfDen(Location location) {
        this.location = location;
        this.connectedWolves = new ArrayList<>();
        this.hasReproduced = false;
    }

    public void beginNight() {
        hasReproduced = false;
    }

    public void addWolf(Wolf wolf) {
        connectedWolves.add(wolf);
        wolf.setDen(this);
    }

    public void removeWolf(Wolf wolf) {
        connectedWolves.remove(wolf);
        wolf.setDen(null);
    }

    public void hideWolves(World world) {
        for (Wolf wolf : connectedWolves) {
            world.remove(wolf);
            System.out.println(wolf + " is now hiding in the den.");
        }
    }

    public void revealWolves(World world, Program program) {
        for (Wolf wolf : connectedWolves) {
            Location spot = findSuitableLocation(world);
            if (spot != null && world.isTileEmpty(spot)) {
                try {
                    wolf.relocate(world, spot, program);
                    System.out.println(wolf + " has emerged from the den at " + spot);
                } catch (IllegalArgumentException e) {
                    System.out.println("Failed to reveal wolf " + wolf + ": " + e.getMessage());
                }
            }
        }
    }

    public void connectPackToDen(List<Wolf> wolfPack) {
        for (Wolf wolf : wolfPack) {
            if( !connectedWolves.contains(wolf) ) {
                connectedWolves.add(wolf);
                wolf.setDen(this);
            }
        }
    }



    public void reproduce(World world, Program program, int maxNewWolves) {
        if (hasReproduced) {
            System.out.println("Already reproduced this night.");
            return;
        }

        int newWolvesCount = 0;

        while (newWolvesCount < maxNewWolves && connectedWolves.size() >= 2) {
            System.out.println("Wolves are reproducing in the den.");

            Location newLocation = findSuitableLocation(world);
            if (newLocation != null && world.isTileEmpty(newLocation)) {
                Wolf newWolf = new Wolf(world, newLocation, program);
                try {
                    world.add(newWolf);
                    System.out.println("A new wolf was born at " + newLocation);
                    newWolvesCount++;
                } catch (IllegalArgumentException e) {
                    System.out.println("Failed to add a new wolf at location " + newLocation + ": " + e.getMessage());
                }
            } else {
                System.out.println("No suitable location found. Ending reproduction.");
                break;
            }
        }

        hasReproduced = true; // Markér som reproduceret
        System.out.println("Reproduction cycle completed. Total new wolves: " + newWolvesCount);
    }

    public Location getLocation() {
        return location;
    }

    public List<Wolf> getConnectedWolves() {
        return connectedWolves;
    }

    public Set<Location> getPath(World world, Location start, Location end) {
        Set<Location> path = new LinkedHashSet<>();
        Queue<Location> queue = new LinkedList<>();
        Map<Location, Location> cameFrom = new HashMap<>();

        // Check for null locations to prevent NullPointerException
        if (start == null || end == null) {
            System.out.println("Warning: Provided locations cannot be null.");
            return path; // Return an empty path when either location is null
        }

        queue.add(start);
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {
            Location current = queue.poll();

            if (current.equals(end)) {
                Location step = current;
                while (step != null) {
                    path.add(step);
                    step = cameFrom.get(step);
                }
                return path;
            }

            for (Location next : world.getSurroundingTiles(current)) {
                if (!cameFrom.containsKey(next) && (world.getTile(next) == null || world.getTile(next) instanceof NonBlocking)) {
                    queue.add(next);
                    cameFrom.put(next, current);
                }
            }
        }
        path.add(start);
        path.add(end);
        return path;
    }

    private Location findSuitableLocation(World world) {
        // Vi antager, at den nuværende position er centrum for søgningen
        Location center = this.location;

        // Henter alle tilstødende positioner omkring centrum
        Set<Location> surroundingLocations = world.getSurroundingTiles(center);

        // Prøv at finde en tom lokation flere gange, med øget radius hver gang
        int maxAttempts = 5;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            for (Location potentialLocation : surroundingLocations) {
                if (world.isTileEmpty(potentialLocation) || world.getTile(potentialLocation) instanceof NonBlocking) {
                    return potentialLocation; // Return the first available location
                }
            }
            surroundingLocations = expandSearchArea(world, center, attempt);
        }

        // Hvis ingen egnet lokation findes, returneres null
        System.out.println("Could not find a suitable location for a new wolf.");
        return null;
    }

    private Set<Location> expandSearchArea(World world, Location center, int attempt) {
        // Logik til at udvide søgeområdet
        Set<Location> expandedArea = new HashSet<>();
        // Implementer logik til at inkludere flere tilstødende områder baseret på attempt
        // Dette kan inkludere at bruge en større radius eller inkorporere mere intelligente søgemetoder
        return expandedArea;
    }
}