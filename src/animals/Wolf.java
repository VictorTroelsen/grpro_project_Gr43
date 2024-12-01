package animals;

import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;


public class Wolf extends Animal{

    private final boolean isPlaced;

    public Wolf(World world, Location initiallocation, Program program) {
        super(world,initiallocation,program);
        this.isPlaced = placeAnimal(initiallocation);
        this.energy = 120;
    }

    @Override
    public void act(World world) {
        System.out.println(this + " energy before hunt: " + energy);
        if(energy <= 80) {
            hunt();
        }

        super.act(world); // Kald forældremetoden for at håndtere standardadfærd som bevægelse og energiforbrug.

        if (energy <= 0 || age > maximumAge()) {
            dies(); // Kalder dies metoden fra Animal klassen
        }
    }

    @Override
    public int maximumAge() {
        return 20;
    }

    @Override
    protected boolean canHunt(Object prey) {
        if (prey instanceof Bear) {
            return getPackSize() >= 3;
        } else return prey instanceof Rabbit;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public int getPackSize() {
        // Implementer logik for at bestemme størrelsen på flokken
        return 3; // Eksempelværdi, erstat med reel logik
    }
}
