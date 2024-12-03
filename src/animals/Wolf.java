package animals;

import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class Wolf extends Carnivore{
    private static Wolf alphaWolf;
    private static Set<Wolf> pack = new HashSet<>();
    private final boolean isPlaced;

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

    public static void chooseNewAlpha() {
        if (!pack.isEmpty()) {
            Wolf[] wolves = pack.toArray(new Wolf[0]);
            alphaWolf = wolves [new Random().nextInt(wolves.length)];
        }
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
            chooseNewAlpha();
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

}
