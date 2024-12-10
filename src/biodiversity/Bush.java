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
    private int berries; // Antallet af bær busken har
    private static final int MAX_BERRIES = 10; // Maksimalt antal bær busken kan have
    private boolean hasBerries; // Indikerer om busken har bær
    private Program program; // Reference til programmet for display information

    public Bush(World world, Location initialLocation, Program program) {
        this.world = world;
        this.location = initialLocation;
        this.program = program;
        this.berries = new Random().nextInt(MAX_BERRIES) + 1; // Start med et tilfældigt antal bær
        this.hasBerries = berries > 0;
        updateDisplay(); // Initial visuel repræsentation
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
        updateDisplay(); // Opdater visuel repræsentation
        System.out.println(berriesPicked + " berries picked from bush at: " + location);
        return berriesPicked;
    }

    private void updateDisplay() {
        String imageName = hasBerries ? "bush-berries" : "bush";
        DisplayInformation displayInformation = new DisplayInformation(Color.BLACK, "bush-berries");
        program.setDisplayInformation(Bush.class, displayInformation);
        System.out.println("Bush display updated to: " + imageName);
    }

    @Override
    public void act(World world) {
        // Regenerer bær næste dag
        if (!hasBerries && berries < MAX_BERRIES) {
            berries = MAX_BERRIES;
            hasBerries = true;
            updateDisplay(); // Opdater visuel repræsentation
            System.out.println("Bush at " + location + " regenerated berries.");
        }
    }
}
