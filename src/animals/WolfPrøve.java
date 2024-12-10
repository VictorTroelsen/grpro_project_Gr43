/*package animals;

import actions.WolfDen;
import biodiversity.Grass;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Wolf extends Carnivore {
    private Set<Wolf> pack = new HashSet<>();
    private WolfDen den;
    private Location location;

    public Wolf(World world, Location initialLocation, Program program) {
        super(world, initialLocation, program);
        this.age = 0;
        this.energy = 120;
        this.isPlaced = placeAnimal(initialLocation);

        if (!isPlaced || initialLocation == null) {
            System.err.println("Failed to place wolf. Location is null or placement failed.");
        } else {
            this.location = initialLocation;
        }
    }

    // Handlinger som en ulv skal udføre
    @Override
    public void act(World world) {
        if (isLeader()) {
            Location newDestination = chooseNewDestination();
            setPackDestination(newDestination);
        }

        move(); // Kald superklassens move metode

        Set<Wolf> newWolves = findNearbyWolves();
        joinOrCreatePack(newWolves);

        for (Wolf otherWolf : newWolves) {
            fightOrJoinPack(otherWolf);
        }

        if (location == null) {
            System.out.println("Warning: Wolf location is null, cannot perform actions.");
            return;  // Afbryd videre handling hvis placeringen er null
        }

        // Eksempel på logik for hvordan ulven kan handle
        if (shouldBuildDen(world, location)) {  // Betingelse for bygge hule
            buildDen(world, location);
        }

        if (energy <= 0 || age == maximumAge()) {
            dies();
        }
        energy -= 3;
    }

    private boolean isLeader() {
        return !pack.isEmpty() && pack.iterator().next().equals(this);
    }

    private Set<Wolf> findNearbyWolves() {
        Set<Wolf> nearbyWolves = new HashSet<>();
        if (location == null) {
            System.err.println("Warning: Wolf location is null, cannot find nearby wolves.");
            return nearbyWolves;
        }

        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        if(surroundingTiles == null) {
            System.err.println("Error retrieving surrounding tiles.");
            return nearbyWolves;
        }

        for (Location loc : surroundingTiles) {
            Object tileContent = world.getTile(loc);
            if (tileContent instanceof Wolf && tileContent != this) {
                nearbyWolves.add((Wolf) tileContent);
            }
        }
        return nearbyWolves;
    }

    private Location chooseNewDestination() {
        // Logik til at vælge en ny destination
        return findEmptyAdjacentLocation();
    }

    public void setLocationInWorld(World world, Location newLocation) {
        if (world == null || newLocation == null) {
            System.err.println("Error: World or Location is null. Cannot set location.");
            throw new IllegalArgumentException("World must not be null");
        }

        try {
            world.setTile(newLocation, this);  // Flytter ulven til den nye placering i verden
            this.location = newLocation;       // Opdatér ulvens interne placering
        } catch (IllegalArgumentException e) {
            System.out.println("Could not set tile: " + e.getMessage());
        }
    }

    private void setPackDestination(Location destination) {
        for (Wolf wolf : pack) {
            wolf.setLocationInWorld(world, destination);
        }
    }

    public void buildDen(World world, Location location) {
        if (world == null || location == null) {
            throw new IllegalArgumentException("World or location cannot be null.");
        }

        // Kontroller om flisen er egnet til brug
        if (isTileSuitableForDen(world, location)) {
            try {
                world.setTile(location, this);  // Forsøg kun at sætte flisen, hvis den er klar
                System.out.println(getClass().getSimpleName() + " built a new den at " + location);
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to build den: " + e.getMessage());
            }
        } else {
            System.err.println(getClass().getSimpleName() + " cannot build den - Tile is occupied or incompatible at: " + location);
        }
    }

    private boolean shouldBuildDen(World world, Location location) {
        // Logik for hvorvidt der burde bygges en hule
        return !isTileOccupied(world, location);
    }

    private boolean isTileSuitableForDen(World world, Location location) {
        Object existingContent = world.getTile(location);
        // Tile kan kun indeholde selve ulven eller være tom
        return existingContent == null || existingContent == this;
    }

    public void joinOrCreatePack(Set<Wolf> newWolves) {
        if (newWolves.isEmpty()) {
            return;
        }

        if (this.pack.isEmpty()) {
            // Start ny pakke
            this.pack.add(this);  // Inkluder den aktuelle ulv i pakken
            this.pack.addAll(newWolves);
            synchronizePackPackReferences();
            System.out.println("New pack created.");
        } else {
            // Udvid eksisterende pakke
            this.pack.addAll(newWolves);
            synchronizePackPackReferences();
            System.out.println("Pack expanded.");
        }
    }

    private void synchronizePackPackReferences() {
        for (Wolf wolf : pack) {
            wolf.pack = this.pack; // Sikrer, at alle ulve deler samme pakke-reference
        }
    }

    public boolean isTileOccupied(World world, Location location) {
        if (world == null || location == null) {
            throw new IllegalArgumentException("World or location cannot be null.");
        }
        // Tjek om der er en genstand på denne lokation
        Object existingContent = world.getTile(location);
        return existingContent != null;
    }

    public void fightOrJoinPack(Wolf otherWolf) {
        if (!pack.contains(otherWolf)) {
            // Implementer logik for kamp eller sammenføjning
            if (new Random().nextBoolean()) {
                if (this.energy > otherWolf.energy) {
                    otherWolf.dies();
                } else {
                    this.dies();
                }
            } else {
                this.pack.add(otherWolf);
                otherWolf.pack.addAll(this.pack);
            }
        }
    }
}
*/
