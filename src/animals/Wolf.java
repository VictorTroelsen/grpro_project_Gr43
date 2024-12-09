package animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import actions.WolfDen;

import java.awt.*;
import java.util.*;


public class Wolf extends Carnivore{
    private static Wolf alphaWolf;
    private static Set<Wolf> pack = new HashSet<>();
    private final boolean isPlaced;
    private WolfDen den;
    private Location location;
    private Wolf[] wolves;

    public Wolf(World world, Location initiallocation, Program program) {
        super(world,initiallocation,program);
        this.location = initiallocation;
        if (pack.isEmpty()) {
            alphaWolf = this;
        }
        pack.add(this);
        this.isPlaced = placeAnimal(initiallocation);
        this.energy = 120;


    }

    public void act(World world) {
        if (world.isDay()) {
            coordinatePackMovement(world);
            System.out.println(this + " energy before hunt: " + energy);
            if (energy < 120) {
                hunt();
            }
        }

        super.act(world);

        if (pack.size() > 1 && den == null) {
            Location denLocation = findEmptyAdjacentLocation();
            if (denLocation != null) {
                digDen(denLocation);
            }
        }

        if (world.isNight() && den != null) {
            moveToDen(world);

            // Voks har reproduktion kapaciteten kun om natten
            if (energy > 70 && pack.size() > 1) {
                int maxNewWolves = pack.size() / 2;
                den.reproduce(world, program, maxNewWolves);
                energy -= 20;
            }
        } else if (world.isDay() && den != null) {
            leaveDen(world);
        }

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
                        int energyShare = 100 / pack.size();
                        for (Wolf wolf : pack) {
                            wolf.energy += energyShare;
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
            moveToPack();
        }
    }

    @Override
    public int maximumAge() {
        return 40;
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

        if (this.equals(alphaWolf)) {
            chooseNewAlpha();
            System.out.println("The alpha has died. A new alpha is chosen.");
        }
    }

    public void coordinatePackMovement(World world) {
        if (pack.size() > 1 && world.isDay()) {
            for (Wolf wolf : pack) {
                if (!wolf.equals(alphaWolf)) {
                    wolf.moveToPack();
                }
            }
        }
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public int getPackSize() {
        // Implementer logik for at bestemme størrelsen på flokken
        return pack.size(); // Eksempelværdi, erstat med reel logik
    }

    public static Wolf getAlpha() {
        return alphaWolf;
    }

    public void createNewPack() {
        if (!pack.isEmpty()) {
            wolves = pack.toArray(new Wolf[0]);
        }

        if(alphaWolf == null){
            chooseNewAlpha();
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
    }


    public void moveToPack() {
        if (!this.equals(alphaWolf) && world.contains(this) && world.isOnTile(this)) {
            Location alphaLocation = world.getLocation(alphaWolf);
            int maxRange = 2;  // Juster dette tal, hvis det er nødvendigt
            if (distance(this.location, alphaLocation) > maxRange) {
                Set<Location> path = world.getSurroundingTiles(this.location);
                Location bestMove = chooseBestMoveTowards(alphaLocation, path);

                if (bestMove != null && world.isTileEmpty(bestMove)) {
                    world.move(this, bestMove);
                    this.location = bestMove;

                    System.out.println(this + " moved towards the pack leader at location: " + alphaLocation);
                }
            }
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
                Set<Location> pathToDen = den.getPath(world, location, denLocation);
                try {
                    world.move(this, denLocation);
                    location = denLocation;
                    pack.remove(this); // Fjern fra aktiv flok, tilføj til hulen
                    den.addWolf(this); // Tilføj ulv til hulerne
                    world.remove(this); // Fjern fra verdens kort

                    // Visuel information til når ulven er i hulen
                    program.setDisplayInformation(Wolf.class, new DisplayInformation(Color.DARK_GRAY,"wolf-sleeping"));

                    System.out.println(this + " moved to den at location: " + denLocation);
                } catch (IllegalArgumentException e) {
                    System.err.println("Move to den failed: " + e.getMessage());
                }
            }
        }
    }

    public void leaveDen(World world) {
        if (den != null) {
            Location exitLocation = den.getLocation();
            try {
                program.setDisplayInformation(Wolf.class, new DisplayInformation(Color.GRAY, "wolf"));
                System.out.println(this + " left den and moved to location: " + exitLocation);
            } catch (IllegalArgumentException e) {
                System.err.println("Leaving den failed: " + e.getMessage());
            }
        }
    }

}
