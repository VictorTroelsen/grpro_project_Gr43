package biodiversity;

import itumulator.world.Location;
import itumulator.world.World;

public abstract class Carcass {
    protected final Location location;
    protected final World world;
    protected boolean isDecomposed;
    protected int decompositionTime;
    private static final int MAX_DECOMPOSITION_TIME = 5;

    public Carcass(World world, Location initialLocation) {
        this.world = world;
        this.location = initialLocation;
        this.decompositionTime = MAX_DECOMPOSITION_TIME;
        this.isDecomposed = false;

        placeCarcass(initialLocation);
    }

    private void placeCarcass(Location location) {
        if (world.getTile(location) == null) {
            world.setTile(location, this);
        }
    }

    public void act(World world) {
        if (!isDecomposed) {
            decompositionTime--;
            if (decompositionTime <= 0) {
                isDecomposed = true;
                world.setTile(location, null);
            }
        }
    }
}