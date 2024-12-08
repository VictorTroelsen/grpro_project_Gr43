package animals;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import biodiversity.Bush;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Bear extends Carnivore {
    private final Location territoryCenter;
    private static final int TERRITORY_RADIUS = 5; // Radius for bjørnens territorium

    public Bear(World world, Location initialLocation, Program program) {
        super(world, initialLocation, program);
        this.energy = 100;
        this.age = 0;
        this.isPlaced = placeAnimal(initialLocation);
        this.territoryCenter = initialLocation;
        System.out.println("Bear's territory center is at: " + territoryCenter);
    }

    @Override
    public void act(World world) {
        moveWithinTerritory();
        if (world.isDay() && energy <= 70) {
            hunt();
        }
        updateEnergy();

        if (energy <= 0) {
            dies();
        }
    }

    @Override
    protected boolean canHunt(Object prey) {
        // Bjørnen kan jage kaniner og ulve
        return prey instanceof Rabbit || prey instanceof Wolf;
    }

    @Override
    protected void hunt() {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        for (Location loc : surroundingTiles) {
            Object prey = world.getTile(loc);
            if (canHunt(prey)) {
                System.out.println(this + " found prey to hunt at location: " + loc);
                try {
                    world.delete(prey); // Fjern byttet fra verdenen
                    System.out.println(prey + " at location " + loc + " has been eaten and deleted.");

                    // Flyt bjørnen til byttets position
                    world.move(this, loc);
                    location = loc;

                    // Tilføj energi afhængigt af byttets type
                    if (prey instanceof Rabbit) {
                        energy += 50; // Kaniner giver mere energi
                    } else if (prey instanceof Wolf) {
                        energy += 75; // Ulve giver endnu mere energi
                    }

                    System.out.println(this + " moved to location " + loc + " after eating.");

                } catch (IllegalArgumentException e) {
                    System.err.println("Error occurred while hunting at location: " + loc + ". " + e.getMessage());
                }
            }
        }
        System.out.println("No prey found near location: " + location);
        forageBerries();
    }

    private void forageBerries() {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        for (Location loc : surroundingTiles) {
            Object tileContent = world.getTile(loc);
            if (tileContent instanceof Bush) {
                Bush bush = (Bush) tileContent;
                int berriesPicked = bush.pickBerries(3); // Bjørnen plukker fx 3 bær
                energy += berriesPicked * 5; // Bær giver mindre energi end kød
                System.out.println("Bear foraged " + berriesPicked + " berries from bush at " + loc);
                return;
            }
        }
        System.out.println("No berries found near location: " + location);
    }

    private void moveWithinTerritory() {
        if (!world.isOnTile(this)) {
            System.out.println("Bear is not on any tile.");
            return;
        }

        Set<Location> surroundingTiles = world.getSurroundingTiles(location); // Hent nærliggende felter
        List<Location> validLocations = new ArrayList<>();

        // Find gyldige nærliggende felter
        for (Location loc : surroundingTiles) {
            if (isTileEmptyOrNonBlocking(loc)) {
                validLocations.add(loc);
            }
        }

        // Hvis der er gyldige nærliggende felter, vælg tilfældigt ét
        if (!validLocations.isEmpty()) {
            Random random = new Random();
            Location newLocation = validLocations.get(random.nextInt(validLocations.size()));

            try {
                world.move(this, newLocation);
                location = newLocation;
                System.out.println("Bear moved to nearby location: " + location);
            } catch (IllegalArgumentException e) {
                System.out.println("Move blocked: " + e.getMessage());
            }
        } else {
            System.out.println("Bear could not find a valid nearby move.");
        }
    }
}