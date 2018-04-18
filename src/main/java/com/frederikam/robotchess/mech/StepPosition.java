package com.frederikam.robotchess.mech;

import com.frederikam.robotchess.util.Constants;
import com.frederikam.robotchess.chess.TilePosition;

import static com.frederikam.robotchess.util.Constants.BOARD_BOTTOM_LEFT;

public class StepPosition {

    public final double x;
    public final double y;

    public StepPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public StepPosition minus(StepPosition other) {
        return new StepPosition(x - other.x, y - other.y);
    }

    public StepPosition plus(StepPosition other) {
        return new StepPosition(x + other.x, y + other.y);
    }

    public StepPosition plus(double x, double y) {
        return new StepPosition(x + this.x, y + this.y);
    }

    public StepPosition times(double factor) {
        return new StepPosition(x * factor, y * factor);
    }

    public double magnitude() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public TilePosition getNearestTile() {
        //Constants.BOARD_BOTTOM_LEFT.x + Constants.TILE_WIDTH*x + Constants.HALF_TILE_WIDTH,
        //Constants.BOARD_BOTTOM_LEFT.y + Constants.TILE_WIDTH*y + Constants.HALF_TILE_WIDTH

        int tileX = (int) Math.round((x - BOARD_BOTTOM_LEFT.x) / Constants.TILE_WIDTH);
        int tileY = (int) Math.round((y - BOARD_BOTTOM_LEFT.y) / Constants.TILE_WIDTH);

        return new TilePosition(tileX, tileY);
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

}
