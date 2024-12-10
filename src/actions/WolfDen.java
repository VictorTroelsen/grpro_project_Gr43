package actions;

import animals.Wolf;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.Program;

import java.awt.*;
import java.util.Set;

public class WolfDen implements NonBlocking {
    private final Location denLocation;
    private final World world;
    private final WolfPack wolfPack;


    public WolfDen(World world, Location location, WolfPack pack, Program program) {
        if (world == null || location == null || pack == null) {
            throw new IllegalArgumentException("world, location and pack must not be null");
        }
        this.world = world;
        this.denLocation = location;
        this.wolfPack = pack;

        DisplayInformation displayInformation = new DisplayInformation(Color.DARK_GRAY, "hole");
        program.setDisplayInformation(WolfDen.class, displayInformation);
    }

    public void restNearDen() {
        Set<Location> nearbyLocations = world.getSurroundingTiles(denLocation, 3); //Område tæt på hulen
        Set<Wolf> nearbyWolves = world.getAll(Wolf.class, nearbyLocations);

        for (Wolf wolf : nearbyWolves) {
            if (wolfPack.isPartOfPack(wolf)) {
                wolf.setEnergy(Math.min(wolf.getEnergy() + 20, 100));
                System.out.println("Wolf #" + wolf.getId() + " rested near the den and regained energy!");
            }
        }
    }


    // Reproduktion i hulen
    public void reproduce() {
        // Find ulve tæt på hulen
        Set<Location> nearbyLocations = world.getSurroundingTiles(denLocation, 3);
        Set<Wolf> nearbyWolves = world.getAll(Wolf.class, nearbyLocations);

        // Tæl alpha-ulve og andre ulve
        Wolf alpha = null;
        Wolf mate = null;

        for (Wolf wolf : nearbyWolves) {
            if (wolfPack.isAlpha(wolf)) {
                alpha = wolf;
            } else if (wolfPack.isPartOfPack(wolf) && mate == null) {
                mate = wolf; // Find den første mulige "mate" ulv
            }
        }
        // Reproduktion: Skal ske mellem alpha og en anden ulv
        if (alpha != null && mate != null) {
            // Generer en ny ulv med forældre i flokken
            Location emptyTile = findEmptyTileNearDen();
            if (emptyTile != null) {
                Wolf newWolf = new Wolf(world, emptyTile, null, 0, wolfPack); // Nyfødt ulv med alder 0
                wolfPack.addToPack(alpha, newWolf);
                System.out.println("A new wolf (ID #" + newWolf.getId() + ") was born in the pack led by Wolf #" + alpha.getId());
            } else {
                System.out.println("No empty tile was found near the den to place the new wolf.");
            }
        } else {
            System.out.println("Reproduction cannot occur. Conditions not met near the den.");
        }
    }

    // Find en tom placering tæt på hulen
    private Location findEmptyTileNearDen() {
        Set<Location> nearbyEmpty = world.getEmptySurroundingTiles(denLocation);
        return nearbyEmpty.isEmpty() ? null : nearbyEmpty.iterator().next(); // Returnér en tom lokation, hvis det er muligt
    }

    // Getters
    public Location getDenLocation() {
        return denLocation;
    }

    public WolfPack getWolfPack() {
        return wolfPack;
    }

}
