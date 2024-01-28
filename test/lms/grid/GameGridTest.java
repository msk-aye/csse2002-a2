package lms.grid;

import lms.logistics.belts.Belt;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class GameGridTest {

    private GameGrid gameGrid1;
    private GameGrid gameGrid2;
    private GameGrid gameGrid3;
    private GameGrid gameGrid4;

    @Before
    public void setUp() {
        gameGrid1 = new GameGrid(0);
        gameGrid2 = new GameGrid(11);
        gameGrid3 = new GameGrid(11);
        gameGrid4 = new GameGrid(100);

    }
    @Test
    public void rangeIncorrect() {
        GameGrid gameGrid = new GameGrid(-3);
        assertEquals(gameGrid.getRange(), -3);
    }

    @Test
    public void ranges() {
        assertEquals(gameGrid1.getRange(), 0);
        assertEquals(gameGrid2.getRange(), 11);
        assertEquals(gameGrid3.getRange(), 11);
        assertEquals(gameGrid4.getRange(), 100);
    }

    @Test
    public void gridNotCopy() {
        Map<Coordinate, GridComponent> map1 = gameGrid1.getGrid();
        Map<Coordinate, GridComponent> map2 = gameGrid1.getGrid();
        assertNotSame(map2.hashCode(), map1.hashCode());
    }

    @Test
    public void setCoordinate() {
        Coordinate newCord = new Coordinate(0,0);
        Belt newBelt = new Belt(12);
        gameGrid2.setCoordinate(new Coordinate(0,0), newBelt);
        assertEquals(gameGrid2.getGrid().get(newCord), newBelt);
    }

}
