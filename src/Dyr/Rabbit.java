package Dyr;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.Program;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class Rabbit implements Actor {

    private Location location;
    private World world;
    private int energy;
    private int age;
    private boolean isPlaced;
    private RabbitHole homeHole;
    private Program program;
    private static final int DIGGING_ENERGY_COST = 10;
    private static final int EATING_ENERGY_GAIN = 20;

    public Rabbit(World world, Location initialLocation, Program program) {
        this.world = world;
        this.energy = 100;
        this.age = 0;
        this.isPlaced = placeRabbit(initialLocation);
        this.program = program;
    }

    private int initialEnergy() {
        return 100 - age;
    }

    private boolean placeRabbit(Location initialLocation) {
        int attempts = 0;
        int maxAttempts = world.getSize() * world.getSize();
        Random random = new Random();

        while (attempts < maxAttempts) {
            if (initialLocation != null && isTileEmptyOrNonBlocking(initialLocation)) {
                this.location = initialLocation;
                world.setTile(initialLocation, this);
                System.out.println("Dyr.Rabbit placed at location: " + initialLocation);
                return true;
            }

            int x = random.nextInt(world.getSize());
            int y = random.nextInt(world.getSize());
            Location location = new Location(x, y);

            if (isTileEmptyOrNonBlocking(location)) {
                this.location = location;
                world.setTile(location, this);
                System.out.println("Dyr.Rabbit placed at location: " + location);
                return true;
            }
            attempts++;
        }
        System.out.println("Dyr.Rabbit could not be placed after " + maxAttempts + " attempts");
        return false;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    @Override
    public void act(World world) {
        move();
        age++;
        eatGrass(location);
        updateEnergy();

        if (energy <= 0) {
            removeFromRabbitHole();
            world.setTile(location, null);
            System.out.println("Dyr.Rabbit died at location: " + location);
        }

        reproduce();

        if(world.isNight()) {
            moveToBurrow();
        }

        if (homeHole == null && energy >= DIGGING_ENERGY_COST) {
            digHole();
        }
    }

    private void updateEnergy() {
        energy -= age / 10;

        if (world.getTile(location) instanceof Grass) {
            energy += EATING_ENERGY_GAIN;
            world.setTile(location, null);
            System.out.println("Dyr.Rabbit ate grass at location: " + location);
        }
    }

    private void reproduce() {
        if (age > 5 && energy > 30) {
            Location babyLocation = findEmptyAdjacentLocation();
            if (babyLocation != null) {
                Rabbit baby = new Rabbit(world, babyLocation, program);
                System.out.println("Dyr.Rabbit reproduced at location: " + babyLocation);
                energy -= 20;
            }
        }
    }

    private Location findEmptyAdjacentLocation() {
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
            System.out.println("Dyr.Rabbit is not on any tile.");
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
            System.out.println("Dyr.Rabbit attempting to move from " + location + " to " + newLocation);

            Object tileContent = world.getTile(newLocation);

            if (tileContent == null || tileContent instanceof NonBlocking) {
                System.out.println("Move valid, proceeding...");
                try {
                    world.move(this, newLocation);
                    location = newLocation;

                eatGrass(newLocation);

                    System.out.println("Dyr.Rabbit moved to location: " + newLocation);
                    return;
                } catch (IllegalArgumentException e) {
                    System.out.println("Move blocked: " + e.getMessage());
                }
            } else {
                System.out.println("Move blocked: Location is occupied or the same as the current location.");
            }
        }
    }

    private void moveToBurrow() {
        if (homeHole != null) {
            Location burrowLocation = homeHole.getLocation();
            if(!location.equals(burrowLocation)) {
                world.move(this, burrowLocation);
                location = burrowLocation;
                System.out.println("Dyr.Rabbit moved to burrow at location: " + burrowLocation);
            }
        }
    }

    private void removeFromRabbitHole() {
        Object tileContent = world.getTile(location);
        if (tileContent instanceof RabbitHole) {
            ((RabbitHole) tileContent).removeRabbit(this);
        }
    }

    private void digHole() {
        if (world.getTile(location) == null) {
            homeHole = new RabbitHole(location, new ArrayList<>());
            world.setTile(location, homeHole);
            energy -= DIGGING_ENERGY_COST;
            System.out.println("Dyr.Rabbit dug a hole at location: " + location);
        }
    }

    private void eatGrass(Location location) {
        Object tileContent = world.getTile(location);

        if (tileContent instanceof Grass) {
            world.delete(tileContent); // Fjern græsset fra verden
            energy += EATING_ENERGY_GAIN; // Kaninen får energi
            System.out.println("Dyr.Rabbit ate grass at location: " + location);

            program.setDisplayInformation(Grass.class,null);
        }
    }

    private boolean isTileEmptyOrNonBlocking(Location location) {
        Object tile = world.getTile(location);
        return tile == null || tile instanceof NonBlocking;
    }

    public int getEnergy() { return energy; }

    public Object getLocation() { return location; }

}