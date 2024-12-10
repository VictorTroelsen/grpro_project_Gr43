package biodiversity;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.awt.*;

public class Fungi implements Actor {
    private Program program;
    private boolean hasFungi;
    private int spreadingRadius;


    public Fungi(Program program) {
        this.program = program;
        this.hasFungi = false;
        this.spreadingRadius = 3;
        updateDisplay();
    }

    private void updateDisplay() {
        String imageName = hasFungi ? "carcass" : "carcass-fungi";
        DisplayInformation displayInformation = new DisplayInformation(Color.GREEN, "fungus");
        program.setDisplayInformation(Bush.class, displayInformation);
        System.out.println("Fungus display updated to: " + imageName);
    }

    public void act(World world) {
    }

    public void hastenDecomposition(Carcass carcass) {
        int decompositionRate = 5;
    }

}
