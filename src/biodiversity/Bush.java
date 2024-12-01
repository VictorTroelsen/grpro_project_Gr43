package biodiversity;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Random;

public class Bush implements Actor {
    private Location location;
    private World world;
    private int berries; // Antallet af bær busken har
    private static final int MAX_BERRIES = 10; // Maksimalt antal bær busken kan have
    private static final int BERRY_REGEN_RATE = 1; // Antal bær, der regenereres per trin

    public Bush(World world, Location initialLocation) {
        this.world = world;
        this.location = initialLocation;
        this.berries = new Random().nextInt(MAX_BERRIES) + 1; // Start med et tilfældigt antal bær
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
        System.out.println(berriesPicked + " berries picked from bush at: " + location);
        return berriesPicked;
    }

    @Override
    public void act(World world) {
        regenerateBerries();
    }

    private void regenerateBerries() {
        if (berries < MAX_BERRIES) {
            berries += BERRY_REGEN_RATE; // Regenerér bær over tid
            System.out.println("Bush at " + location + " regenerated berries. Current berries: " + berries);
        }
    }

    public int getBerries() {
        return berries;
    }

    public Location getLocation() {
        return location;
    }
}
