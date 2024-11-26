import Dyr.Rabbit;
import itumulator.world.Location;

import java.util.List;

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

    public List<Rabbit> getConnectedRabbits() { return connectedRabbits; }
}
