package com.frederikam.robotchess.util;

import com.frederikam.robotchess.mech.StepPosition;

public class Constants {

    public static final StepPosition BOARD_TOP_RIGHT = new StepPosition(10000, 6280);
    public static final double TILE_WIDTH = BOARD_TOP_RIGHT.y / 7;
    public static final StepPosition BOARD_BOTTOM_LEFT = new StepPosition(
            TILE_WIDTH * 2 // Offset by two tiles for the graveyard
                    // Center on the X axis
                    + (BOARD_TOP_RIGHT.x - TILE_WIDTH * 11) / 2 // "TILE_WIDTH * 11" is the effectively used width
            , 0);

    public static final double HALF_TILE_WIDTH = TILE_WIDTH/2;
    public static final double PIECE_DIAMETER = TILE_WIDTH/2;

}
