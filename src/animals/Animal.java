package animals;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.Random;

public class Animal implements Actor {
    Location location;
    World world;
    int energy;
    int age;
    boolean isPlaced;
    Program program;

    public Animal(World world, Location initialLocation, Program program) {
        this.world = world;
        this.energy = 100;
        this.age = 0;
        this.isPlaced = placeAnimal(initialLocation);
        this.program = program;
    }

    private boolean placeAnimal(Location initialLocation) {
        int attempts = 0;
        int maxAttempts = 2;
        Random random = new Random();
        return false;
    }
    public boolean isPlaced() {
        return isPlaced;
    }

    private boolean isTileEmptyOrNonBlocking(Location location) {
        Object tile = world.getTile(location);
        return tile == null || tile instanceof NonBlocking;
    }


    /**
     * @param world providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World world) {

    }
}
