package lms.grid;

import java.util.HashMap;
import java.util.Map;

/**
 * The GameGrid is responsible for managing the state and initialisation of the game's grid.
 * It provides the Map structure to hold the coordinates of each node in the grid. It also
 * maintains the size of the grid using a range variable. The range value donates how many
 * nodes each hexagonal grid node extends to.
 *
 * @ass2
 * @version 1.1
 * <p>
 * Summary: Initializes a grid of the game.
 *
 */
public class GameGrid {
    /** The range of this grid, that is the distance from the center of the grid to any of the
     * sides. Grid size can be calculated by range * 2 + 1*/
    private final int range;

    /** A map representing the current GameGrid, with coordinates mapping to the corresponding
     * GridComponent found at the coordinate. */
    private final Map<Coordinate, GridComponent> map;

    /**
     * Constructs a new GameGrid with the given range, stored in a Map. A private helper method
     * generate() is used to populate the initial map.
     * @param range the range of the grid.
     * @requires range > 0.
     */
    public GameGrid(int range) {
        this.range = range;
        this.map = generate(range);
    }

    /**
     * Accessor method to ge the range of the grid stored when the GameGrid was initialised.
     * @return the range of the grid.
     */
    public int getRange() {
        return range;
    }

    /**
     * Set the GridComponent at the given Coordinate on the map.
     * @param coordinate the Coordinate of the GridComponent.
     * @param component the GridComponent to be set.
     */
    public void setCoordinate(Coordinate coordinate, GridComponent component) {
        map.put(coordinate, component);  // Overrides anything that was previously at Coordinate
    }

    /**
     * get a copy of the grid of the game.
     * @return Map a copy of the grid of the game.
     */
    public Map<Coordinate, GridComponent> getGrid() {
        return new HashMap<>(map);
    }

    /**
     * Helper method:
     * Generates a grid with the given range, starting from the origin (the centre) and maintaining
     * a balanced shape for the entire mapping structure.
     * This has been provided to support you with the hexagonal coordinate logic.
     * @param range The range of the map.
     * @provided
     */
    private Map<Coordinate, GridComponent> generate(int range) {
        Map<Coordinate, GridComponent> tempGrid = new HashMap<>();
        for (int q = -range; q <= range; q++) { // From negative to positive (inclusive)
            for (int r = -range; r <= range; r++) { // From negative to positive (inclusive)
                for (int s = -range; s <= range; s++) { // From negative to positive (inclusive)
                    if (q + r + s == 0) {
                        // Useful to default to error
                        tempGrid.put(new Coordinate(q, r, s), () -> "ERROR");

                    }
                }
            }
        }
        return tempGrid;
    }

}
