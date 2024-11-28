package animals;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.Random;
import java.util.Set;

public class Animal implements Actor {
    Location location;
    static World world;
    int energy;
    int age;
    boolean isPlaced;
    Program program;

    public Animal(World world, Location initialLocation, Program program) {
        this.world = world;
        this.energy = 100;
        this.age = 0;
        this.program = program;
    }
    private int initialEnergy() {
        return 100 - age;
    }


    public boolean isPlaced() {
        return isPlaced;
    }

    boolean isTileEmptyOrNonBlocking(Location location) {
        Object tile = world.getTile(location);
        return tile == null || tile instanceof NonBlocking;
    }


    /**
     * @param world providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World world) {
        move();
        energy -= 5;
        updateEnergy();
    }

    void updateEnergy() {
        energy -= 2 * age;
    }


    Location findEmptyAdjacentLocation() {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        for (Location loc : surroundingTiles) {
            if (isTileEmptyOrNonBlocking(loc)) {
                return loc;
            }
        }
        return null;
    }

    public void move() {
        if (!world.isOnTile(this)) {
            System.out.println(Animal + " is not on any tile.");
            return;
        }

        Random random = new Random();
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        Location[] shuffledTiles = surroundingTiles.toArray(new Location[0]);

        for(int i = 0; i < shuffledTiles.length; i++) {
            int randomIndex = random.nextInt(shuffledTiles.length);
            Location temp = shuffledTiles[i];
            shuffledTiles[i] = shuffledTiles[randomIndex];
            shuffledTiles[randomIndex] = temp;
        }

        for (Location newLocation : shuffledTiles) {
            System.out.println(Animal + " attempting to move from " + location + " to " + newLocation);

            Object tileContent = world.getTile(newLocation);

            if (tileContent == null || tileContent instanceof NonBlocking) {
                System.out.println("Move valid, proceeding...");
                try {

                    world.move(this, newLocation);
                    location = newLocation;


                    System.out.println(Animal + " moved to location: " + newLocation);
                    return;
                } catch (IllegalArgumentException e) {
                    System.out.println("Move blocked: " + e.getMessage());
                }
            } else {
                System.out.println("Move blocked: Location is occupied or the same as the current location.");
            }
        }
    }
    public int getEnergy() { return energy; }

    public Object getLocation() { return location; }

}
