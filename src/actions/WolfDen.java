package actions;

import animals.Wolf;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import itumulator.executable.Program;
import java.util.*;

public class WolfDen implements NonBlocking {
    private Location location;
    private List<Wolf> connectedWolves;

    public WolfDen(Location location) {
        this.location = location;
        this.connectedWolves = new ArrayList<>();
    }

    public void addWolf(Wolf wolf) {
        connectedWolves.add(wolf);
        wolf.setDen(this);
    }

    public void removeWolf(Wolf wolf) {
        connectedWolves.remove(wolf);
        wolf.setDen(null);
    }

    public void connectPackToDen(List<Wolf> wolfPack) {
        for (Wolf wolf : wolfPack) {
            if( !connectedWolves.contains(wolf) ) {
                connectedWolves.add(wolf);
                wolf.setDen(this);
            }
        }
    }



    public void reproduce(World world, Program program) {
        // Assume that Object program is a placeholder for actual program type
        if (connectedWolves.size() >= 2) {
            System.out.println("Wolves are reproducing in the den.");

            Location newLocation = findSuitableLocation(world);
            if (newLocation != null) {
                Wolf newWolf = new Wolf(world, newLocation, program);
                world.add(newWolf);
                System.out.println("A new wolf was born at " + newLocation);
            }
        }
    }

    public Location getLocation() {
        return location;
    }

    public List<Wolf> getConnectedWolves() {
        return connectedWolves;
    }

    public Set<Location> getPath(World world, Location start, Location end) {
        Set<Location> path = new LinkedHashSet<>();
        Queue<Location> queue = new LinkedList<>();
        Map<Location, Location> cameFrom = new HashMap<>();

        queue.add(start);
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {
            Location current = queue.poll();

            if (current.equals(end)) {
                Location step = current;
                while (step != null) {
                    path.add(step);
                    step = cameFrom.get(step);
                }
                return path;
            }

            for (Location next : world.getSurroundingTiles(current)) {
                if (!cameFrom.containsKey(next) && (world.getTile(next) == null || world.getTile(next) instanceof NonBlocking)) {
                    queue.add(next);
                    cameFrom.put(next, current);
                }
            }
        }
        path.add(start);
        path.add(end);
        return path;
    }

    private Location findSuitableLocation(World world) {
        // Implement logic to find a suitable location around the den
        // As a placeholder, returning null meaning it should be properly implemented
        return null;
    }
}