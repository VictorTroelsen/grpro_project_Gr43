package animals;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Set;

public class Bear extends Animal {
    private final Location territoryCenter;

    public Bear (World world, Location initialLocation, Program program) {
        super(world, initialLocation, null);
        this.energy = 100;
        this.age = 0;
        this.isPlaced = placeAnimal(initialLocation);
        this.territoryCenter = initialLocation;
    }

    public void act(World world) {
        if (world.isDay()) {
            hunt();
            forageBerries();
        }
        moveWithinTerritory();
        updateEnergy();
    }

    private void forageBerries() {
        // Implementér logik for at finde bær og få energi
        System.out.println("Bear is foraging for berries around location: " + location);
        // Logik til at finde og 'spise' bær
    }

    private void moveWithinTerritory() {
        if (!world.isOnTile(this)) {
            System.out.println("Bear is not on any tile.");
            return;
        }
        // Begrænset bevægelse til nærområder af territoriet
        Set<Location> surroundingTiles = world.getSurroundingTiles(territoryCenter, 2); // Radius kan justeres
        for (Location loc : surroundingTiles) {
            if (isTileEmptyOrNonBlocking(loc) && !loc.equals(location)) {
                world.move(this, loc);
                location = loc;
                System.out.println("Bear moved within its territory to location: " + loc);
                break;
            }
        }
    }


}
