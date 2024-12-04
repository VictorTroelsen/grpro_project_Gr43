package animals;

import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import actions.WolfDen;

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
        if (pack.isEmpty()) {
            alphaWolf = this;
        }
        pack.add(this);
        this.isPlaced = placeAnimal(initiallocation);
        this.energy = 150;
    }

    @Override
    public void act(World world) {
        if (pack.size() > 1) {
            moveToPack();
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

        if (this.location.equals(den.getLocation())) {
            den.reproduce(world, program);
        }

        if (energy <= 0 || age > maximumAge()) {
            dies(); // Kalder dies metoden fra Animal klassen
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
        Random random = new Random();
        int randomIndex = random.nextInt(pack.size());
        alphaWolf = pack.toArray(new Wolf[0])[randomIndex];
    }

    public void moveToPack() {
        if (alphaWolf != null && !this.equals(alphaWolf)) {
            Location alphaLocation = world.getLocation(alphaWolf);
            Set<Location> path = world.getSurroundingTiles(this.location);
            Location bestMove = chooseBestMoveTowards (alphaLocation, path);

            if (bestMove != null && world.isTileEmpty(bestMove)) {
                world.move(this, bestMove);
                this.location = bestMove;

                System.out.println(this + " moved towards the pack leader at location: " + alphaLocation);
            }
        }
        if(pack.size() == maxPacksize()){
            createNewPack();
        }
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
        if (den == null) {
            den = new WolfDen(location);
            den.addWolf(this);
            System.out.println(this + " has dug a new den at location: " + location);
        }
    }

    public Location getWolfLocation() {
        return location;
    }

    public void setDen(WolfDen den) {
        this.den = den;
    }

}
