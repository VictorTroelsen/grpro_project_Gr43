package programManagers;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.awt.Color;

import actions.WolfDen;
//import actions.WolfDenPrøve;
import actions.WolfPack;
import animals.Bear;
import animals.Wolf;
//import animals.WolfPrøve;
import animals.Rabbit;
import actions.RabbitHole;
import biodiversity.Bush;
import biodiversity.Grass;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class FileReaderUtil {

    public static List<Path> getTxtFilesFromDirectory(String directoryPath) throws IOException {
        List<Path> txtFiles = new ArrayList<>();
        try (var stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.txt")) {
            for (Path path : stream) {
                txtFiles.add(path);
            }
        }
        Collections.sort(txtFiles, Comparator.comparing(Path::getFileName));
        return txtFiles;
    }

    public static Program readFile(Path path) {
        Program p = null;
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;

            int worldSize = 0;
            if ((line = br.readLine()) != null) {
                worldSize = Integer.parseInt(line.trim());
                System.out.println("World size read from file: " + worldSize);
            }

            int displaySize = 800;
            int delay = 100;
            p = new Program(worldSize, displaySize, delay);
            World w = p.getWorld();

            p.setDisplayInformation(Grass.class, new DisplayInformation(Color.GREEN, "grass"));
            p.setDisplayInformation(Rabbit.class, new DisplayInformation(Color.GRAY, "rabbit-small"));
            p.setDisplayInformation(RabbitHole.class, new DisplayInformation(Color.RED, "hole-small"));
            p.setDisplayInformation(Bear.class, new DisplayInformation(Color.CYAN, "bear"));
            p.setDisplayInformation(Wolf.class, new DisplayInformation(Color.BLUE, "wolf"));
            p.setDisplayInformation(WolfDen.class, new DisplayInformation(Color.BLACK, "hole"));
            p.setDisplayInformation(Bush.class, new DisplayInformation(Color.PINK, "bush"));
            p.setDisplayInformation(SmallCarcass.class, new DisplayInformation(Color.ORANGE, "carcass-small"));
            p.setDisplayInformation(NormalCarcass.class, new DisplayInformation(Color.DARK_GRAY, "carcass"));

            logWorldState(w, "Initial world state");

            //int rabbitCount = 0;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String type = parts[0];
                    try {
                        // Kontrollér om koordinaterne findes i `parts[1]`
                        if (parts[1].matches("\\d+") && parts.length > 2 && parts[2].matches("\\(\\d+,\\d+\\)")) {
                            String[] coords = parts[2].replaceAll("[()]", "").split(",");
                            int x = Integer.parseInt(coords[0]);
                            int y = Integer.parseInt(coords[1]);
                            addElementsToWorld(type, 1, p, x, y);
                        }
                        // Hvis koordinaterne er inden i `parts[1]`
                        else if (parts[1].matches("\\(\\d+,\\d+\\)")) {
                            String[] coords = parts[1].replaceAll("[()]", "").split(",");
                            int x = Integer.parseInt(coords[0]);
                            int y = Integer.parseInt(coords[1]);
                            addElementsToWorld(type, 1, p, x, y);
                        }
                        else {
                            // Håndterer antals- og interval-strenge tilfælde
                            String value = parts[1];
                            if (value.contains("-")) {
                                String[] range = parts[1].split("-");
                                int minValue = Integer.parseInt(range[0]);
                                int maxValue = Integer.parseInt(range[1]);
                                int count = minValue + new Random().nextInt(maxValue - minValue + 1);
                                addElementsToWorld(type, count, p);
                            } else {
                                int count = Integer.parseInt(parts[1]);
                                addElementsToWorld(type, count, p);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in line: " + line);
                    }
                } else {
                    System.err.println("Invalid line format in file " + path + ": " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + path + ": " + e.getMessage());
        }
        return p;
    }

    private static void addElementsToWorld(String type, int count, Program p) {
        addElementsToWorld(type, count, p, -1, -1); // Call the coordinates method with default non-used values
    }

    private static void addElementsToWorld(String type, int count, Program p, int specificX, int specificY) {
        ActorManager actorManager = new ActorManager(p);
        World w = p.getWorld();
        Random random = new Random();
        //Program program = new Program(w.getSize(), 800, 100);

        int addedCount = 0;
        int attempts = 0;
        int maxAttempts = w.getSize() * w.getSize();

        while (addedCount < count && attempts < maxAttempts) {
            int x = specificX >= 0 ? specificX : random.nextInt(w.getSize());
            int y = specificY >= 0 ? specificY : random.nextInt(w.getSize());
            Location location = new Location(x, y);

            if (w.getTile(location) == null) {
                try {
                    switch (type.toLowerCase()) {
                        case "grass":
                            Grass grass = new Grass(location);
                            actorManager.addActor(grass, location);
                            addedCount++;
                            break;
                        case "rabbit":
                            Rabbit rabbit = new Rabbit(w, location, p);
                            if (rabbit.isPlaced()) {
                                addedCount++;
                            }
                            break;
                        case "burrow":
                            List<Rabbit> rabbitList = new ArrayList<>();
                            RabbitHole rabbitHole = new RabbitHole(location, rabbitList);
                            w.setTile(location, rabbitHole);
                            addedCount++;
                            break;
                        case "berry":
                            Bush bush = new Bush(w, location,p);
                            //actorManager.addActor(bush, location);
                            addedCount++;
                            break;
                        case "bear":
                            Bear bear = new Bear(w, location, p);
                            //actorManager.addActor(bear, location);
                            if(bear.isPlaced()) {
                                addedCount++;
                            }

                            break;
                        case "wolf":
                            int age = 0;
                            Wolf wolf = new Wolf(w, location, p, age, null); // Vi sender ikke en specifik WolfPack
                            // actorManager.addActor(wolf, location); // Tilføj ulven til aktørhåndteringen, hvis nødvendigt
                            if (wolf.isPlaced()) {
                                addedCount++;
                            }
                            break;
                                
                        default:
                            System.err.println("Unknown type: " + type);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Error adding " + type + " at location (" + x + ", " + y + "): " + e.getMessage());
                }
            } else {
                System.err.println("Tile at location (" + x + ", " + y + ") is already occupied by: " + w.getTile(location).getClass().getSimpleName());
            }
            attempts++;
        }

        if (addedCount < count) {
            System.err.println("Only added " + addedCount + " of " + count + " " + type + "(s) after " + attempts + " attempts.");
        } else {
            System.out.println("Successfully added " + addedCount + " " + type + "(s) after " + attempts + " attempts.");
        }

        logWorldState(w, "World state after adding " + type + "s");
    }

    public static void logWorldState(World w, String description) {
        System.out.println(description);
        for (int y = 0; y < w.getSize(); y++) {
            for (int x = 0; x < w.getSize(); x++) {
                Location location = new Location(x, y);
                Object tile = w.getTile(location);
                if (tile == null) {
                    System.out.print("[ ]");
                } else {
                    System.out.print("[X]");
                }
            }
            System.out.println();
        }
    }


}