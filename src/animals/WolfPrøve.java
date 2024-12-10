package animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
//import actions.WolfDenPrøve;
//import actions.PackManager;

import java.awt.*;
import java.util.*;


/*public class WolfPrøve extends Carnivore {
    public static WolfPrøve alphaWolf;
    private static Set<WolfPrøve> pack = new HashSet<>();
    private final boolean isPlaced;
    private WolfDenPrøve den;
    private Location location;
    private WolfPrøve[] wolves;
    private boolean isHidden;

    public WolfPrøve(World world, Location initiallocation, Program program) {
        super(world, initiallocation, program);
        this.location = initiallocation;
        this.isPlaced = placeAnimal(initiallocation);
        this.energy = 120;
        this.isHidden = false;
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

        /*if (!world.contains(this) || world.getLocation(this) == null) {
            System.err.println(this + " is not correctly placed in the world, skipping action.");
            return;
        }

        if (world.isDay()) {
            // Handlinger om dagen
            if (den != null) {
                revealAtDay(world, location);  // Gør ulvene visuelt tilgængelige
                digDen(den.getLocation());    // Dig den, hvis muligt
            }
            /*if (location != null && (den == null || !world.getLocation(den).equals(location))) {
                coordinatePackMovement(world); // Bevæger pakken
            }
            if (energy < 120) {
                hunt(); // Ulvene jager for energi
            }
        } else if (world.isNight()) {
            // Handlinger om natten
            if (den == null && pack.size() > 1) {
                Location denLocation = findEmptyAdjacentLocation();
                if (denLocation != null) {
                    digDen(denLocation); // Gravning af en hule
                }
            }

            hideAtNight(world);  // Skjul ulvene

            if (energy > 70 && pack.size() > 1) {
                int maxNewWolves = pack.size() / 2;
                den.reproduce(world, program, maxNewWolves); // Reproduktion
                energy -= 20; // Energiforbrug ved reproduktion
            }
        }

        super.act(world);

        if (energy <= 0 || age > maximumAge()) {
            dies(); // Ulven dør hvis energi er 0 ell. alder overstiger max
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
                        for (WolfPrøve wolf : pack) {
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
        for (WolfPrøve wolf : pack) {
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

    public static Set<WolfPrøve> getCurrentPack() {
        return alphaWolf != null ? PackManager.getPack(alphaWolf) : Collections.emptySet();
    }

    public static WolfPrøve getAlpha() {
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
        alphaWolf = pack.toArray(new WolfPrøve[0])[randomIndex];

        if (world.contains(alphaWolf) && world.isOnTile(alphaWolf)) {
            System.out.println("Ny alpha er valgt: " + alphaWolf);
        } else {
            System.out.println("Den valgte alpha er ikke korrekt placeret, forsøger at flytte.");
        }
    }


    public void moveToPack(Location alphaLocation) {
        if (location != null && world.contains(this) && world.isOnTile(this)) {

            Set<Location> path = world.getSurroundingTiles(this.location);
            Location bestMove = chooseBestMoveTowards(alphaLocation, path);

            if (bestMove != null && world.isTileEmpty(bestMove)) {
                world.move(this, bestMove);
                this.location = bestMove;
                PackManager.wolfLocations.put(this, bestMove);
                System.out.println(this + " moved towards the pack leader at location: " + alphaLocation);
            }
        } else {
            System.out.println("Cannot move as the wolf is not correctly placed on the map.");
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

    private int distance(Location a, Location b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    public void digDen(Location location) {
        if (den == null && energy >= 20) {
            if (world.getTile(location) == null || world.getTile(location) instanceof NonBlocking) {
                // Check for an alternative tile if needed
                try {
                    // Attempt to set the tile
                    den = new WolfDenPrøve(location);
                    den.addWolf(this);
                    world.setTile(location, den); // Safely attempting to set the tile

                    // Set visual information for wolf den
                    DisplayInformation displayInformation = new DisplayInformation(Color.GRAY, "hole");
                    program.setDisplayInformation(WolfDenPrøve.class, displayInformation);

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

    public void hideAtNight(World world) {
        if (world.contains(this) && world.isOnTile(this)) {
            world.remove(this);  // Fjern ulven fra kortet
            this.isHidden = true;  // Marker ulven som skjult
            System.out.println(this + " is now hidden at night.");
        }
    }

    public void revealAtDay(World world, Location spot) {
        if (isHidden && spot != null && world.isTileEmpty(spot)) {
            world.setTile(spot, this);  // Tilføj ulven tilbage på kortet
            this.isHidden = false;  // Marker ulven som ikke skjult
            this.location = spot;
            System.out.println(this + " has reappeared at location: " + spot);
        }
    }

    public Location getWolfLocation() {
        return location;
    }

    public void setDen(WolfDenPrøve den) {
        this.den = den;
    }

    public void relocate(World world, Location newLocation, Program program) {
        this.location = newLocation;
        world.setTile(newLocation, this);
    }

    public static Set<WolfPrøve> getPack() {
        return pack;
    }

    public void moveToDen(World world) {
        if (den != null) {
            Location denLocation = den.getLocation();
            try {
                if (!location.equals(denLocation) && world.isOnTile(this)) {
                    world.move(this, denLocation);
                    this.location = denLocation;
                    den.addWolf(this);
                    System.out.println(this + " moved to den at location: " + denLocation);
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Move to den failed: " + e.getMessage());
            }
        } else {
            System.err.println("No den found for " + this + " to move to.");
        }
    }


    public void leaveDen(World world) {
        if (den != null) {
            try {
                Location exitLocation = den.getLocation();
                program.setDisplayInformation(WolfPrøve.class, new DisplayInformation(Color.GRAY, "wolf"));
                System.out.println(this + " left den and moved to location: " + exitLocation);
            } catch (IllegalArgumentException e) {
                System.err.println("Leaving den failed: " + e.getMessage());
            }
        }
    }
}
*/