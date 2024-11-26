package programManagers;

import animals.Rabbit;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;

import java.util.ArrayList;
import java.util.List;

public class ActorManager {
    private Program program;

    public ActorManager(Program program) {
        this.program = program;
    }

    public void addActor(Actor actor, Location location) {
        World world = program.getWorld();
        if (world.getTile(location) == null) {
            System.out.println("Placing " + actor.getClass().getSimpleName() + " at location " + location);
            world.setTile(location, actor);
        } else {
            throw new IllegalArgumentException("Entity already exists at location: " + location);
        }
    }

    public List<Rabbit> getRabbits() {
        List<Rabbit> rabbits = new ArrayList<>();
        World world = program.getWorld();

        for (int x = 0; x < world.getSize(); x++) {
            for (int y = 0; y < world.getSize(); y++) {
                Location location = new Location(x, y);
                Object tile = world.getTile(location);
                if (tile instanceof Rabbit) {
                    rabbits.add((Rabbit) tile);
                }
            }
        }
        return rabbits;
    }

    public void removeTile(Location location) {
        World world = program.getWorld();

        Object tileObject = world.getTile(location);

        if (tileObject != null) {
            System.out.println("Removing object: " + tileObject.getClass().getSimpleName());
            world.setTile(location, null); // Remove the object by setting the tile to null
        } else {
            throw new IllegalArgumentException("No entity exists at location: " + location);
        }
    }
}