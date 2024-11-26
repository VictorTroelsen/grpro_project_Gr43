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
    private static final int MAX_ACCEPTABLE_DISTANCE = 10;

    public Rabbit(World world, Location initialLocation, Program program) {
        super(world, initialLocation, program);
        this.energy = 100;
        this.age = 0;
        this.isPlaced = placeRabbit(initialLocation);
    }

    private int initialEnergy() {
        return 100 - age;
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

    public boolean isPlaced() {
        return isPlaced;
    }

    @Override
    public void act(World world) {
        move();
        energy -= 5;
        age++;
        eatGrass(location);
        updateEnergy();

        /*if (energy <= 0 || age == 20) {
            dies();
        }

        reproduce();*/

        if (homeHole == null && energy >= 20) {
            digHole();
        }

        if(world.isNight()) {
            if (!moveToBurrow()) {
                sleepOutside();
            }
        }
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
        if (age > 5 && energy > 50) {
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

    private boolean moveToBurrow() {
        if (homeHole != null) {
            Location burrowLocation = homeHole.getLocation();
            if (!location.equals(burrowLocation)) {
                Set<Location> pathToBurrow = homeHole.getPath(world, location, burrowLocation);
                if(pathToBurrow != null && pathToBurrow.size() <= MAX_ACCEPTABLE_DISTANCE) {
                    world.move(this, burrowLocation);
                    location = burrowLocation;
                    program.setDisplayInformation(Rabbit.class, new DisplayInformation(Color.GRAY,"rabbit-small-sleeping"));
                    System.out.println("Dyr.Rabbit moved to burrow at location: " + burrowLocation);
                    return true;
                } else {
                    System.out.println("Dyr.Rabbit could not find a path to the burrow and will sleep outside.");
                    return false;
                }
            } else {
                program.setDisplayInformation(Rabbit.class, new DisplayInformation(Color.GRAY,"rabbit-small-sleeping"));
                System.out.println("Dyr.Rabbit is already in the burrow.");
                return true;
            }
        }
        return false;
    }



    private void sleepOutside() {
        program.setDisplayInformation(Rabbit.class, new DisplayInformation(Color.GRAY,"rabbit-small-sleeping"));
        System.out.println("Dyr.Rabbit is sleeping outside at location: " + location);
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

    private boolean isTileEmptyOrNonBlocking(Location location) {
        Object tile = world.getTile(location);
        return tile == null || tile instanceof NonBlocking;
    }

    public int getEnergy() { return energy; }

    public Object getLocation() { return location; }

}