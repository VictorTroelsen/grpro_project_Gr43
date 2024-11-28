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
    String Animal;

    public Animal(World world, Location initialLocation, Program program) {
        this.world = world;
        this.energy = 100;
        this.age = 0;
        this.program = program;
    }

    protected boolean canHunt(Object prey) {
        return prey instanceof Animal && !(prey instanceof NonBlocking) && !(prey.getClass().equals(this.getClass()));
    }

    protected void hunt() {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        for (Location loc : surroundingTiles) {
            Object prey = world.getTile(loc);
            if (prey instanceof Animal && canHunt(prey)) {
                world.delete(prey);
                energy += 50; // Standard værdistigning for at jage bytte
                System.out.println(Animal + " hunted and ate prey at location: " + loc);
                break;
            }
        }
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

    public void dies() {
        System.out.println("died at location: " + location);
        world.delete(this);


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

        for (int i = 0; i < shuffledTiles.length; i++) {
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

    public int getEnergy() {
        return energy;
    }

    public Object getLocation() {
        return location;
    }

    boolean placeAnimal(Location initialLocation) {
        int attempts = 0;
        int maxAttempts = 2; //world.getSize() * world.getSize();
        Random random = new Random();

        while (attempts < maxAttempts) {
            if (initialLocation != null && isTileEmptyOrNonBlocking(initialLocation)) {
                this.location = initialLocation;
                world.setTile(initialLocation, this);
                System.out.println(Animal + "placed at location: " + initialLocation);
                return true;
            }

            int x = random.nextInt(world.getSize());
            int y = random.nextInt(world.getSize());
            Location location = new Location(x, y);

            if (isTileEmptyOrNonBlocking(location)) {
                this.location = location;
                world.setTile(location, this);
                System.out.println(Animal + "placed at location: " + location);
                return true;
            }
            attempts++;
        }
        System.out.println(Animal + "could not be placed after " + maxAttempts + " attempts");
        return false;
    }
}
