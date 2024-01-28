package lms.io;

import java.io.*;
import java.util.*;

import lms.exceptions.*;
import lms.grid.*;
import lms.logistics.*;
import lms.logistics.belts.*;
import lms.logistics.container.*;

/**
 * Class responsible for the leading of a text file containing the details of a simulated factory
 * and creating a GameGrid based off the information. Handles all files and errors related to
 * parsing etc.
 */
public class GameLoader {
    /** Variable used to denote the end of a section in the text file. */
    private static final String sectionEnd = "_____";
    /** The range of the GameGrid. */
    private static int range;
    /** The GameGrid variable. */
    private static GameGrid gameGrid;
    /** A Map containing all transport nodes for keys, and their relative Coordinate portion
     * as the values. Used for locating nodes. */
    private static final Map<Transport, Coordinate> position = new HashMap<>();

    /**
     * A helper method used to convert a [row, column] position into a hexagonal Coordinate.
     * @param row the current 2-dimensional row.
     * @param column the current 2-dimensional column.
     * @return Coordinate corresponding to the [row, column] position given.
     */
    private static Coordinate toCoordinate(int row, int column) {
        /* Calculates q by using the logic that the column is dependent on row.
         * Upper half -> column - row, lower half -> column - range. */
        int q = column - Math.min(range, row);
        int r = row - range;  // More intuitive than q
        return new Coordinate(q, r);
    }

    /**
     * A helper method used to instantiate Items from a List of keys, and return a List
     * containing the initialised Items.
     * @param keys the list of item keys as retrieved by parsing the text file.
     * @return list containing all Items corresponding to the keys in the input list.
     */
    private static List<Item> getItems(List<String> keys) {
        List<Item> itemList = new ArrayList<>();

        for (String key : keys) {
            itemList.add(new Item(key));
        }
        return itemList;
    }

    /**
     * A helper method used to split a List of all the lines in the text file into a List
     * containing lists that correspond to the separate sections of the text file (i.e. the
     * sections that are separated by 5 underscores). The list can then be used to access each
     * section of the text file easily.
     * @param lines a list containing all the lines of the text file.
     * @return a List of Lists, with each list being a separate section of the file.
     * @throws FileFormatException if there are not exactly 6 section in the file.
     */
    private static List<List<String>> splitSections(List<String> lines) throws FileFormatException {
        List<Integer> breakPoints = new ArrayList<>();

        // Creating a list of integers, to mark the indices where the input list should be split
        // The breakpoint is any occurrence of '_____' (sectionEnd)
        String toCheck;
        int countSections = 1;
        int index = 0;
        for (String line : lines) {
            try {
                toCheck = line.substring(0, 5);
            } catch (IndexOutOfBoundsException e) {
                toCheck = null;
                index++;
                continue;
            }
            if (Objects.equals(toCheck, sectionEnd)) {
                breakPoints.add(index);
                countSections++;
            }
            index++;
        }

        // If the number of sections != 6, the file is invalid
        if (countSections != 6) {
            throw new FileFormatException();
        }

        List<List<String>> section = new ArrayList<>();

        int last = -1;

        // Breaks the input list into sublist, according to the breakpoints previously found
        for (int breakPoint : breakPoints) {
            section.add(lines.subList(last + 1, breakPoint));
            last = breakPoint;
        }

        section.add(lines.subList(last  + 1, lines.size()));

        return section;
    }


    /**
     * A method used for retrieving nodes from the grid section of the text file (section 5). The
     * nodes are initialised, their position is saved, and they are added to a list of nodes. All
     * wall and blank cells are written to the GameGrid immediately as they do not require linking.
     * @param section the list containing the lines from section 5 of the text file (the lines of
     *               the grid representation)
     * @param producerItems list of all Items needed for producers.
     * @param receiverItems list of all Items needed for receivers.
     * @return list of all transport nodes for this GameGrid.
     * @throws FileFormatException if the grid is not hexagonal or if the file is formatted
     * incorrectly.
     */
    private static List<Transport> getNodes(List<String> section, List<Item> producerItems,
                                           List<Item> receiverItems) throws FileFormatException {
        int row = 0;
        int column;
        int id = 1;
        int max = range * 2 + 1; // Max length of any row in the maze
        int current; // Max length of current row in maze

        List<Transport> nodes = new ArrayList<>();

        // For each row in the hexagonal grid
        for (String line : section) {

            // Max length calculated and stored
            current = max - Math.abs(row - range);
            column = 0;

            // Row split up
            String[] lineSplit = line.split("");

            // For each letter in the row
            for (String letter : lineSplit) {

                // If column exceeds the max, throw error
                if (column + 1 > current) {
                    throw new FileFormatException();
                }

                try {
                    switch (letter) {
                        // Send to GameGrid
                        case "w" -> {
                            gameGrid.setCoordinate(toCoordinate(row, column), () -> "w");
                            column++;
                        }

                        // Send to GameGrid
                        case "o" -> {
                            gameGrid.setCoordinate(toCoordinate(row, column), () -> "o");
                            column++;
                        }

                        // Initialise and save
                        case "p" -> {
                            Producer producer = new Producer(id, producerItems.get(0));
                            position.put(producer, toCoordinate(row, column));
                            producerItems.remove(0);
                            nodes.add(producer);
                            id++;
                            column++;
                        }

                        // Initialise and save
                        case "r" -> {
                            Receiver receiver = new Receiver(id, receiverItems.get(0));
                            position.put(receiver, toCoordinate(row, column));
                            receiverItems.remove(0);
                            nodes.add(receiver);
                            id++;
                            column++;
                        }

                        // Initialise and save
                        case "b" -> {
                            Belt belt = new Belt(id);
                            position.put(belt, toCoordinate(row, column));
                            nodes.add(belt);
                            column++;
                            id++;
                        }

                        // Skip whitespace
                        case " " -> {
                        }

                        // Throw error if any other character is present
                        default -> throw new FileFormatException();
                    }

                } catch (Error e) {
                    // Any error is a result of incorrect file formatting
                    throw new FileFormatException();
                }
            }

            // Enforces the hexagonal grid shape
            if (column != current) {
                throw new FileFormatException();
            }

            row++;
        }

        return nodes;
    }

    /**
     * A method used for linking all nodes in the nodes list to each other based off the
     * information obtained from section 6 of the file.
     * @param section the list containing the lines from section 6 of the text file (the lines with
     *              linking information).
     * @param nodes list of all nodes in this GameGrid.
     * @return list of all nodes, which are now linked.
     * @throws FileFormatException if the linking information is invalid.
     */
    private static List<Transport> linkNodes(List<String> section, List<Transport> nodes)
            throws FileFormatException {

        // For each line
        for (String line : section) {

            // Split on either "-" or ","
            String[] lineSplit = line.split("[-,]");

            // First character representing id of the node
            int currentId = Integer.parseInt(lineSplit[0]);

            Transport currentNode = nodes.get(currentId - 1);

            int nextId;
            int previousId;

            Transport nextNode;
            Transport previousNode;

            // Split based off the node type, as the type dictates how the information is processed
            switch (currentNode.getEncoding()) {

                // Second number is the nextNode
                case "p" -> {
                    // There cant be 3 numbers for a Producer
                    if (lineSplit.length > 2) {
                        throw new FileFormatException();
                    }
                    nextId = Integer.parseInt(lineSplit[1]);
                    nextNode = nodes.get(nextId - 1);
                    currentNode.setOutput(nextNode.getPath());
                    nextNode.setInput(currentNode.getPath());
                    nodes.set(currentId - 1, currentNode);
                    nodes.set(nextId - 1, nextNode);
                }

                // Second number is the previousNode
                case "r" -> {
                    // There cant be 3 numbers for a Receiver
                    if (lineSplit.length > 2) {
                        throw new FileFormatException();
                    }
                    previousId = Integer.parseInt(lineSplit[1]);
                    previousNode = nodes.get(previousId - 1);
                    currentNode.setInput(previousNode.getPath());
                    previousNode.setOutput(currentNode.getPath());
                    nodes.set(currentId - 1, currentNode);
                    nodes.set(previousId - 1, previousNode);
                }

                // Second number is the previousNode and third number is nextNode
                case "b" -> {
                    try {
                        // Set previous node
                        previousId = Integer.parseInt(lineSplit[1]);
                        previousNode = nodes.get(previousId - 1);
                        currentNode.setInput(previousNode.getPath());
                        previousNode.setOutput(currentNode.getPath());
                        nodes.set(currentId - 1, currentNode);
                        nodes.set(previousId - 1, previousNode);

                    } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                        // Ignored
                    }

                    try {
                        // Set next node
                        nextId = Integer.parseInt(lineSplit[2]);
                        nextNode = nodes.get(nextId - 1);
                        currentNode.setOutput(nextNode.getPath());
                        nextNode.setInput(currentNode.getPath());
                        nodes.set(currentId - 1, currentNode);
                        nodes.set(nextId - 1, nextNode);
                    } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                        // Ignored
                    }
                }
            }
        }
        return nodes;
    }

    /**
     * Provides an access point to lead and parse the grid map text file.
     * @param readerGiven the reader to read from.
     * @return the GameGrid loaded from the reader file.
     * @throws IOException if there is am error reading from the reader.
     * @throws FileFormatException if the file is not in the correct format.
     */
    public static GameGrid load(Reader readerGiven)
            throws IOException, FileFormatException {

        // Null reader
        if (readerGiven == null) {
            throw new NullPointerException();
        }

        // Buffered readers suit this purpose since we can readLine()
        BufferedReader reader = new BufferedReader(readerGiven);

        List<String> lines = new ArrayList<>();

        // Read all lines and save to a list
        try {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (Error e) {
            // Error reading leads to IOException
            throw new IOException();
        }

        // List of lines then split according to section
        List<List<String>> section = GameLoader.splitSections(lines);

        // Range is the first letter of the file
        range = Integer.parseInt(section.get(0).get(0));

        gameGrid = new GameGrid(range);

        // Initialises all Items into a list
        List<Item> producerItems = GameLoader.getItems(section.get(2));
        List<Item> receiverItems = GameLoader.getItems(section.get(3));

        // Gets all nodes into list
        List<Transport> nodes = GameLoader.getNodes(section.get(4), producerItems, receiverItems);

        // Updates nodes with linking information
        nodes = GameLoader.linkNodes(section.get(5), nodes);

        int producerCount = 0;
        int receiverCount = 0;

        // Sends each node to GameGrid
        for (Transport node : nodes) {
            if (node instanceof Producer) {
                // Counting producers to confirm it matches section 2
                producerCount++;
            }

            if (node instanceof Receiver) {
                // Counting receivers to confirm it matches section 3
                receiverCount++;
            }

            gameGrid.setCoordinate(position.get(node), node);
        }

        // Number of producers in this GameGrid
        int numberProducers = Integer.parseInt(section.get(1).get(0));

        // Number of receivers in this GameGrid
        int numberReceivers = Integer.parseInt(section.get(1).get(1));

        if (producerCount != numberProducers || receiverCount != numberReceivers
                            || section.get(2).size() != producerCount
                            || section.get(3).size() != receiverCount) {
            throw new FileFormatException();
        }

        return gameGrid;
    }
}

