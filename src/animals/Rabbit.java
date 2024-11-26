package animals;

import actions.RabbitHole;
import biodiversity.Grass;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.Program;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class Rabbit extends Animal {//implements Actor {

    private RabbitHole homeHole;

    public Rabbit(World world, Location initialLocation, Program program) {
        super(world, initialLocation, program);
        this.energy = 100;
        this.age = 0;
        this.isPlaced = placeRabbit(initialLocation);
    }

    private boolean placeRabbit(Location initialLocation) {
        int attempts = 0;
        int maxAttempts = 2; //world.getSize() * world.getSize();
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
    @Override
    public void act(World world) {
        super.move();
        age++;
        eatGrass(location);
        if (energy <= 0 || age == 20) {
            super.dies();
        }

        reproduce();

        if (homeHole == null && energy >= 10) {
            digHole();
        }

        /*if(world.isNight()) {
            moveToBurrow();
        }*/


    }

    private void updateEnergy() {
        energy -= 2 * age;

        if (world.getTile(location) instanceof Grass) {
            energy += 20;
            world.setTile(location, null);
            System.out.println("Dyr.Rabbit ate grass at location: " + location);
        }
    }

    private void reproduce() {
        if (age > 15 && energy > 80) {
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
        if (world.isDay() && homeHole == null && energy >= 10) {
            Set<Location> surroundingTiles = world.getSurroundingTiles(location);

            for (Location loc : surroundingTiles) {
                if (world.getTile(loc) == null) {
                    homeHole = new RabbitHole(loc, new ArrayList<>());
                    homeHole.addRabbit(this); // Tilføj kaninen til rabbit hole
                    world.setTile(loc, homeHole);

                    // Setup display information for rabbit hole
                    DisplayInformation displayInformation = new DisplayInformation(Color.GRAY, "hole-small");
                    // Assuming there's a method to set display information, you might need to adjust this as per your actual setup.
                    program.setDisplayInformation(RabbitHole.class, displayInformation);

                    energy -= 10;
                    System.out.println("Dyr.Rabbit dug a new hole at location: " + loc);
                    break;
                }
            }

            if (homeHole == null) {
                System.out.println("Dyr.Rabbit could not find a nearby location to dig a hole.");
            }
        }
    }

    private void eatGrass(Location location) {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);

        for (Location loc : surroundingTiles) {
            Object tileContent = world.getNonBlocking(loc);
            if (tileContent instanceof Grass) {
                energy += 30; // Kaninen får energi
                System.out.println("Dyr.Rabbit ate grass at location: " + location);
                world.delete(tileContent); // Fjern græsset fra verden
                break;
            }
        }
    }
}