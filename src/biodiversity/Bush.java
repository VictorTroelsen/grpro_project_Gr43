package biodiversity;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class Bush implements Actor {
    private Location location;
    private World world;
    private int berries;
    private static final int MAX_BERRIES = 10;
    private boolean hasBerries;
    private boolean isEaten; // Indikerer om busken er blevet spist
    private Program program;

    public Bush(World world, Location initialLocation, Program program) {
        this.world = world;
        this.location = initialLocation;
        this.program = program;
        this.berries = new Random().nextInt(MAX_BERRIES) + 1;
        this.hasBerries = berries > 0;
        this.isEaten = false;
        updateDisplay();
        placeBush(initialLocation);
    }

    private void placeBush(Location location) {
        if (world.getTile(location) == null) {
            world.setTile(location, this);
            System.out.println("Bush placed at: " + location + " with " + berries + " berries.");
        } else {
            System.out.println("Cannot place bush at: " + location + ". Tile is occupied.");
        }
    }

    public int pickBerries(int amount) {
        int berriesPicked = Math.min(amount, berries);
        berries -= berriesPicked;
        hasBerries = berries > 0;
        updateDisplay();
        System.out.println(berriesPicked + " berries picked from bush at: " + location);
        return berriesPicked;
    }

    public void markAsEaten() {
        isEaten = true;
        hasBerries = false;
        updateDisplay();
    }

    private void updateDisplay() {
        String imageName = hasBerries ? "bush-berries" : "bush";
        DisplayInformation displayInformation = new DisplayInformation(Color.BLACK, imageName);
        program.setDisplayInformation(Bush.class, displayInformation);
        System.out.println("Bush display updated to: " + imageName);
    }

    @Override
    public void act(World world) {
        if (isEaten) {
            isEaten = false;
            berries = MAX_BERRIES;
            hasBerries = true;
            placeBush(location); // Genplacér busken
            updateDisplay();
            System.out.println("Bush at " + location + " has regrown.");
        }
    }
}
