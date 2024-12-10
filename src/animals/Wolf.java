package animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import actions.WolfDen;
import actions.PackManager;

import java.awt.*;
import java.util.*;


public class Wolf extends Carnivore{
    public static Wolf alphaWolf;
    private static Set<Wolf> pack = new HashSet<>();
    private final boolean isPlaced;
    private WolfDen den;
    private Location location;
    private Wolf[] wolves;

    public Wolf(World world, Location initiallocation, Program program) {
        super(world,initiallocation,program);
        this.location = initiallocation;
        this.isPlaced = placeAnimal(initiallocation);
        this.energy = 120;
        if (alphaWolf == null) {
            alphaWolf = this;
            PackManager.createNewPack(this);
        } else {
            PackManager.addWolfToPack(this, alphaWolf);
            this.pack = PackManager.getPack(this);
        }
    }

    public void act(World world) {
        System.out.println("Acting Wolf: " + this + ", Current pack size: " + pack.size());
        if (world.isDay()) {
            if (den != null) {
                leaveDen(world);
            }
            coordinatePackMovement(world);
            System.out.println(this + " energy before hunt: " + energy);
            if (energy < 120) {
                hunt();
            }
        } else if (world.isNight()) {
            if (den == null && pack.size() > 1) {
                Location denLocation = findEmptyAdjacentLocation();
                if (denLocation != null) {
                    digDen(denLocation);
                }
            }
            // Om natten bevæger sig til hulen og gemmer sig
            moveToDen(world);
            den.hideWolves(world);
            if (energy > 70 && pack.size() > 1) {
                int maxNewWolves = pack.size() / 2;
                den.reproduce(world, program, maxNewWolves);
                energy -= 20;
            }
        }

        super.act(world);

        if (energy <= 0 || age > maximumAge()) {
            dies();
        }
    }

    @Override
    protected void hunt() {
        if (this.equals(alphaWolf)) {
            // Kun alpha-ulven vælger bytte, resten følger alphaen
            Set<Location> surroundingTiles = world.getSurroundingTiles(location, program.getSize() / 5);
            for (Location loc : surroundingTiles) {
                Object prey = world.getTile(loc);
                if (prey instanceof Rabbit && canHunt(prey)) {
                    System.out.println(this + " found prey to hunt at location: " + loc);
                    try {
                        world.delete(prey);
                        System.out.println("prey at location " + loc + " has been eaten and deleted.");

                        world.move(this, loc);
                        location = loc;

                        // Del energi mellem ulvene i pakken
                        int energyShare = 100;
                        for (Wolf wolf : pack) {
                            wolf.energy = Math.min(wolf.energy + energyShare, 120);
                        }
                        System.out.println(this + " moved to location " + loc + " after eating.");
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error occurred while hunting for " + this + " at location: " + loc + ". " + e.getMessage());
                    }
                    break;
                }
            }
        } else {
            // Flyt mod alphaen, hvis ikke alpha og den er på jagt
            coordinatePackMovement(world);
        }
    }

    @Override
    public int maximumAge() {
        return 50;
    }

    @Override
    protected boolean canHunt(Object prey) {
        if (prey instanceof Bear) {
            return getPackSize() >= 3;
        } else return prey instanceof Rabbit;
    }

    @Override
    public void dies() {
        super.dies();

        pack.remove(this);

        PackManager.removeWolfFromPack(this);
    }

    public void coordinatePackMovement(World world) {
        Location alphaLocation = getAlphaLocation();
        for (Wolf wolf : pack) {
            if (!wolf.equals(alphaWolf) && wolf.distance(wolf.location, alphaLocation) > 2) {
                wolf.moveToPack(alphaLocation);
            }
        }
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public int getPackSize() {
        return pack.size();
    }

    public static Set<Wolf> getCurrentPack() {
        return alphaWolf != null ? PackManager.getPack(alphaWolf) : Collections.emptySet();
    }

    public static Wolf getAlpha() {
        return alphaWolf;
    }

    public Location getAlphaLocation() {
        if (alphaWolf == null) {
            chooseNewAlpha();
        }
        if (alphaWolf != null && world.contains(alphaWolf) && world.isOnTile(alphaWolf)) {
            return world.getLocation(alphaWolf);
        } else {
            chooseNewAlpha();
            if (alphaWolf != null && world.contains(alphaWolf) && world.isOnTile(alphaWolf)) {
                return world.getLocation(alphaWolf);
            } else {
                throw new IllegalStateException("Ingen gyldig alpha ulv fundet i verdenen.");
            }
        }
    }


    public void chooseNewAlpha() {
        if (pack.isEmpty()) {
            System.out.println("Pakken er tom, der kan ikke vælges en ny alpha.");
            return; // Tidligere attempt at stoppe
        }

        Random random = new Random();
        int randomIndex = random.nextInt(pack.size());
        alphaWolf = pack.toArray(new Wolf[0])[randomIndex];

        if(world.contains(alphaWolf) && world.isOnTile(alphaWolf)) {
            System.out.println("Ny alpha er valgt: " + alphaWolf);
        } else {
            System.out.println("Den valgte alpha er ikke korrekt placeret, forsøger at flytte.");
        }
    }


    public void moveToPack(Location alphaLocation) {
        Set<Location> path = world.getSurroundingTiles(this.location);
        Location bestMove = chooseBestMoveTowards(alphaLocation, path);

        if (bestMove != null && world.isTileEmpty(bestMove)) {
            world.move(this, bestMove);
            this.location = bestMove;

            System.out.println(this + " moved towards the pack leader at location: " + alphaLocation);
        }
    }

    private boolean isWithinRange(Location loc, Location target, int range) {
        return distance(loc, target) <= range;
    }

    public int maxPacksize() {
        return 5;
    }

    private Location chooseBestMoveTowards(Location target, Set<Location> options) {
        return options.stream().min(Comparator.comparingInt(loc -> distance(loc, target))).orElse(null);
    }

    private int distance (Location a, Location b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    public void digDen(Location location) {
        if (den == null && energy >= 20) {
            if (world.getTile(location) == null || world.getTile(location) instanceof NonBlocking) {
                // Check for an alternative tile if needed
                try {
                    // Attempt to set the tile
                    den = new WolfDen(location);
                    den.addWolf(this);
                    world.setTile(location, den); // Safely attempting to set the tile

                    // Set visual information for wolf den
                    DisplayInformation displayInformation = new DisplayInformation(Color.GRAY, "hole");
                    program.setDisplayInformation(WolfDen.class, displayInformation);

                    energy -= 20; // Adjust energy consumption
                    System.out.println(this + " has dug a new den at location: " + location);
                } catch (IllegalArgumentException e) {
                    System.err.println("Failed digging den at location: " + location + ". " + e.getMessage());
                    // Consider alternative options if needed, e.g., try another location
                }
            } else {
                System.out.println("Could not dig den at location: " + location + " because it's occupied by a non-blocking element.");
                // Consider finding a new spot if desired
            }
        }
    }

    public Location getWolfLocation() {
        return location;
    }

    public void setDen(WolfDen den) {
        this.den = den;
    }

    public void relocate(World world, Location newLocation, Program program) {
        this.location = newLocation;
        world.setTile(newLocation, this);
    }

    public static Set<Wolf> getPack() {
        return pack;
    }

    public void moveToDen(World world) {
        if (den != null) {
            Location denLocation = den.getLocation();
            if (!location.equals(denLocation)) {
                try {
                    world.move(this, denLocation);
                    location = denLocation;
                    den.addWolf(this); // Tilføj ulv til hulen
                    System.out.println(this + " moved to den at location: " + denLocation);
                } catch (IllegalArgumentException e) {
                    System.err.println("Move to den failed: " + e.getMessage());
                }
            }
        }
    }

    public void leaveDen(World world) {
        if (den != null) {
            try {
                Location exitLocation = den.getLocation();
                program.setDisplayInformation(Wolf.class, new DisplayInformation(Color.GRAY, "wolf"));
                System.out.println(this + " left den and moved to location: " + exitLocation);
            } catch (IllegalArgumentException e) {
                System.err.println("Leaving den failed: " + e.getMessage());
            }
        }
    }

}
