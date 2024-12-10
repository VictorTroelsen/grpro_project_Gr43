package animals;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.Set;

abstract class Carnivore extends Animal {

    public Carnivore(World world, Location initiallocation, Program program) {
        super(world, initiallocation, program);
        this.program = program;
    }

    protected boolean canHunt(Object prey) {
        return prey instanceof Animal && !(prey instanceof NonBlocking) && !(prey.getClass().equals(this.getClass()));
    }

    public abstract void act(World world);

    protected void hunt() {
        // Hent omkringliggende felter i nærheden af ulvens nuværende position
        Set<Location> surroundingTiles = world.getSurroundingTiles(location, program.getSize() / 5);
        for (Location loc : surroundingTiles) {
            Object prey = world.getTile(loc); // Få objektet på den nuværende position
            //System.out.println("Checking location: " + loc + " for prey.");
            if (prey instanceof Rabbit && canHunt(prey)) { // Tjek om byttet er en kanin og kan jages
                System.out.println(this + " found rabbit to hunt at location: " + loc);
                try {
                    world.delete(prey); // Fjern kaninen fra verdenen
                    System.out.println("Rabbit at location " + loc + " has been eaten and deleted.");

                    // Flyt ulven til den nu ledige position
                    world.move(this, loc); // Brug move() til at flytte ulven
                    location = loc; // Opdater ulvens interne placering
                    energy += 50; // Tilføj energi efter at have spist byttet
                    System.out.println(this + " moved to location " + loc + " after eating.");
                } catch (IllegalArgumentException e) {
                    System.err.println("Error occurred while hunting for " + this + " at location: " + loc + ". " + e.getMessage());
                }
                break; // Afslut loopet efter vellykket jagt
            }
        }
    }
}
