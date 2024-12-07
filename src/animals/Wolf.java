package animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import actions.WolfDen;

import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


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
        this.energy = 150;


    }

    @Override
    public void act(World world) {
        if (pack.size() > 1 && world.isDay()) {
            for (Wolf wolf : pack) {
                if (!wolf.equals(alphaWolf)) {
                    wolf.moveToPack();
                }
            }
        }
        System.out.println(this + " energy before hunt: " + energy);
        if(energy <= 100) {
            hunt();
        }

        super.act(world); // Kald forældremetoden for at håndtere standardadfærd som bevægelse og energiforbrug.

        if (pack.size() > 1 && den == null) {
            Location denLocation = findEmptyAdjacentLocation();
            if (denLocation != null) {
                digDen(denLocation);
            }
        }

        if(world.isNight() && den != null) {
            Set<Location> pathToDen = den.getPath(world, getWolfLocation(), den.getLocation());
            for (Location step : pathToDen) {
                if (world.isTileEmpty(step)) {
                    world.move(this, step);
                    this.location = step;
                    System.out.println(this + " moved to den at location: " + step);}
            }
        }



        if (energy <= 0 || age > maximumAge()) {
            dies(); // Kalder dies metoden fra Animal klassen
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
                    System.out.println(this + " found rabbit to hunt at location: " + loc);
                    try {
                        world.delete(prey);
                        System.out.println("Rabbit at location " + loc + " has been eaten and deleted.");

                        world.move(this, loc);
                        location = loc;

                        // Del energi mellem ulvene i pakken
                        int energyShare = 50 / pack.size();
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
        if (!this.equals(alphaWolf)) {
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
                // Hvis der ikke er noget non-blocking element, graver ulven hulen
                den = new WolfDen(location);
                den.addWolf(this);

                world.setTile(location, den); // Sæt hulen fysisk på kortet

                // Opsæt visuel information for wolf den
                DisplayInformation displayInformation = new DisplayInformation(Color.GRAY, "hole");
                program.setDisplayInformation(WolfDen.class, displayInformation);

                energy -= 20; // Juster energiforbruget
                System.out.println(this + " has dug a new den at location: " + location);
            } else {
                System.out.println("Could not dig den at location: " + location + " because it's occupied by a non-blocking element.");
                // Her kan du forsøge at finde et nyt sted, hvis det er ønsket
            }
        }
    }

    public Location getWolfLocation() {
        return location;
    }

    public void setDen(WolfDen den) {
        this.den = den;
    }

}
