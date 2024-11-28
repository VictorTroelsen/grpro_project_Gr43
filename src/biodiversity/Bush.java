package biodiversity;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;

import java.awt.Color;
import java.util.Random;

public class Bush implements NonBlocking, Actor {
    private final Location location;

    public Bush(Location location) {
        this.location = location;
    }

    @Override
    public void act(World world) {
        // Initialize bushes in the world if needed
    }

    public Location getLocation() {
        return location;
    }

    public static void placeRandomBushes(World world, Program program) {
        Random random = new Random();
        int numberOfBushes = random.nextInt(5) + 1; // Random number between 1 and 5
        int worldSize = world.getSize();

        for (int i = 0; i < numberOfBushes; i++) {
            int x = random.nextInt(worldSize);
            int y = random.nextInt(worldSize);
            Location location = new Location(x, y);

            // Ensure the tile is empty before placing a bush
            if (world.isTileEmpty(location)) {
                Bush bush = new Bush(location);
                world.setTile(location, bush);
                program.setDisplayInformation(Bush.class, new DisplayInformation(Color.GREEN, "bush"));
            }
        }
    }

    private static void initializeBushesInWorld(World world, Program program) {
        if (world != null && program != null) {
            placeRandomBushes(world, program);
        }
    }

    @Override
    public String toString() {
        return "Bush at " + location;
    }
}
