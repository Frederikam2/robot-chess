package com.frederikam.robotchess.mech;

import com.frederikam.robotchess.chess.TilePosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StepPositionTest {

    @Test
    public void getNearestTile() {
        TilePosition tile;
        tile = new TilePosition(-2,0);
        assertEquals(tile, tile.toStepPosition().getNearestTile());
        tile = new TilePosition(9,0);
        assertEquals(tile, tile.toStepPosition().getNearestTile());
        tile = new TilePosition(-2,7);
        assertEquals(tile, tile.toStepPosition().getNearestTile());
        tile = new TilePosition(9,7);
        assertEquals(tile, tile.toStepPosition().getNearestTile());
    }

    @Test
    public void getNearestTileRounding() {
        TilePosition tile = new TilePosition(5,5);
        assertEquals(tile, tile.toStepPosition().plus(1,  1).getNearestTile());
        assertEquals(tile, tile.toStepPosition().plus(-1, 1).getNearestTile());
        assertEquals(tile, tile.toStepPosition().plus(1, -1).getNearestTile());
        assertEquals(tile, tile.toStepPosition().plus(-1,-1).getNearestTile());
    }
}