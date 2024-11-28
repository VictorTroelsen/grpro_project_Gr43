package animals;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Random;

public class Wolf extends Carnivore{

    public Wolf(World world, Location initiallocation, Program program) {
        super(world, initiallocation, program);
        this.age = 0;
        this.energy = 100;
        this.isPlaced = placeAnimal(initiallocation);
    }
    @Override
    public void act(World world) {
        if(energy <= 40) {
            hunt();
        }
    }


}
