package actions;

import animals.Wolf;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

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
    }

    public void removeWolf(Wolf wolf) {
        connectedWolves.remove(wolf);
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
}
