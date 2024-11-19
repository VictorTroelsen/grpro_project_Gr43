import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        int size = 5;
        int display_size = 800;
        int delay = 75;
        Program p = new Program(size, display_size, delay);
        World w = p.getWorld();

        // w.setTile(new Location(0, 0), new <MyClass>());

        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

        p.show();
    }
}