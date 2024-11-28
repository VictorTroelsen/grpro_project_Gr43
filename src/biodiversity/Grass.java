package biodiversity;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grass implements NonBlocking, Actor {
    private final Location location;
    private static final Random random = new Random();

    public Grass(Location location) {
        this.location = location;
    }

    @Override
    public void act(World world) {
        spread(world);
    }

public void spread(World world) {
    // Add a random probability for spreading
    double spreadProbability = 0.1; // 10% chance to spread

    if (random.nextDouble() > spreadProbability) {
        return;
    }

    //int x = location.getX();
    //int y = location.getY();
    int size = world.getSize();
    List<Location> potentialLocations = new ArrayList<>();

    // Find alle nærliggende tiles
    //System.out.printf("Current location (%d, %d)\n", x, y);
    if(world.isDay()){
    for (int dx = -1; dx <= 1; dx++) {
        for (int dy = -1; dy <= 1; dy++) {
            //System.out.printf("dx: %d, dy: %d\n", dx, dy);
            if (dx == 0 && dy == 0) {
                continue; // Ignorer den nuværende position
            }
            int newX = (location.getX() + dx + size) % size;
            int newY = (location.getY() + dy + size) % size;

            //System.out.printf("Calculated newX: %d\n", newX);
            //newX = newX < 0 ? size - 1 : newX;
            //newY = newY < 0 ? size - 1 : newY;
            //System.out.printf("Calculated newY: %d\n", newY);

            Location newLocation = new Location(newX, newY);

            Object tileContent = world.getTile(newLocation);
            // Tjek om tile er tom eller indeholder en non-blocking objekt.
            if (tileContent == null) {
                potentialLocations.add(newLocation);
            }
        }
    }
    }

    if (!potentialLocations.isEmpty()) {
        Location newLocation = potentialLocations.get(random.nextInt(potentialLocations.size()));
        Grass newGrass = new Grass(newLocation);
        world.setTile(newLocation, newGrass);
        //System.out.printf("New biodiversity.Grass planted at location (%d, %d)\n", newLocation.getX(), newLocation.getY());
    }
    //System.out.printf("Potential locations found: %d\n", potentialLocations.size());

    // Filter potential locations for null content again to ensure it's empty
    //List<Location> filteredLocations = potentialLocations.stream()
      //      .filter(loc -> world.getTile(loc) == null)
        //    .collect(Collectors.toList());

    //Kun hvis vi har potentielle placeringer, der er tomme.
    //if (!filteredLocations.isEmpty()) {
      //  System.out.printf("Potential locations available: %d\n", filteredLocations.size());
        //int randomIndex = (int) (Math.random() * filteredLocations.size());
        //Location newLocation = filteredLocations.get(randomIndex);
        //biodiversity.Grass newGrass = new biodiversity.Grass(newLocation);
        //world.setTile(newLocation, newGrass);
        //System.out.printf("New biodiversity.Grass planted at location (%d, %d)\n", newLocation.getX(), newLocation.getY());
    //} else {
     //   System.out.println("No suitable location found for spreading.");
    //}
}
}