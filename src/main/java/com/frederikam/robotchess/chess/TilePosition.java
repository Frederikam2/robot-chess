package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.Constants;
import com.frederikam.robotchess.mech.StepPosition;

import java.util.Objects;
import java.util.regex.Pattern;

public class TilePosition {

    private static Pattern PATTERN = Pattern.compile("^[A-H][1-8]$");
    public final int x;
    public final int y;

    public TilePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public TilePosition(String tile) {
        tile = tile.toLowerCase();
        char letter = tile.charAt(0);
        switch (letter) {
            //@formatter:off
            case 'a': x = 0; break;
            case 'b': x = 1; break;
            case 'c': x = 2; break;
            case 'd': x = 3; break;
            case 'e': x = 4; break;
            case 'f': x = 5; break;
            case 'g': x = 6; break;
            case 'h': x = 7; break;
            default: x = -1; break;
            //@formatter:on
        }
        y = Integer.parseInt(tile.substring(1)) - 1;
        if (isOutOfBounds()) throw new RuntimeException("Invalid position: " + tile);
    }

    public boolean isOutOfBounds() {
        return x < 0 || x > 7 || y < 0 || y > 7;
    }

    public TilePosition minus(TilePosition other) {
        return new TilePosition(x - other.x, y - other.y);
    }

    public TilePosition plus(TilePosition other) {
        return new TilePosition(x + other.x, y + other.y);
    }

    public TilePosition abs() {
        return new TilePosition(Math.abs(x), Math.abs(y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TilePosition that = (TilePosition) o;
        return x == that.x &&
                y == that.y;
    }

    public StepPosition toStepPosition() {
        return new StepPosition(
                Constants.BOARD_BOTTOM_LEFT.x + Constants.TILE_WIDTH*x + Constants.HALF_TILE_WIDTH,
                Constants.BOARD_BOTTOM_LEFT.y + Constants.TILE_WIDTH*y + Constants.HALF_TILE_WIDTH
        );
    }

    public String toTileNotation() {
        switch (x) {
            case 0: return "A" + y;
            case 1: return "B" + y;
            case 2: return "C" + y;
            case 3: return "D" + y;
            case 4: return "E" + y;
            case 5: return "F" + y;
            case 6: return "G" + y;
            case 7: return "H" + y;
            default: throw new RuntimeException("Invalid tile position: " + this);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

    public static boolean isValidTile(String s) {
        return PATTERN.matcher(s).matches();
    }
}
