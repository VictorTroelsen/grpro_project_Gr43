package actions;

import animals.Rabbit;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;

public class RabbitHole {
    private Location location;
    private List<Rabbit> connectedRabbits;

    public RabbitHole(Location location, List<Rabbit> connectedRabbits) {
        this.location = location;
        this.connectedRabbits = connectedRabbits;
    }

    public void addRabbit(Rabbit rabbit) {
        connectedRabbits.add(rabbit);
    }

    public void removeRabbit(Rabbit rabbit) {
        connectedRabbits.remove(rabbit);
    }

    public Location getLocation() { return location; }

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
                while(step != null) {
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
        return path;
    }

    public List<Rabbit> getConnectedRabbits() { return connectedRabbits; }
}
