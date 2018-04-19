package com.frederikam.robotchess.util;

import com.frederikam.robotchess.mech.StepPosition;

public class Constants {

    private static final double LEFT_X = 2275;
    private static final double LEFT_Y = 100;
    private static final double RIGHT_X = 8190;
    private static final double RIGHT_Y = 6100; // force 8275.0 6100

    public static final double TILE_WIDTH = (RIGHT_Y - LEFT_Y) / 7;
    public static final StepPosition BOARD_BOTTOM_LEFT = new StepPosition(LEFT_X, LEFT_Y);
    public static final StepPosition BOARD_TOP_RIGHT = new StepPosition(LEFT_X + TILE_WIDTH * 7, RIGHT_Y);

    public static final double HALF_TILE_WIDTH = TILE_WIDTH / 2;
    public static final double PIECE_DIAMETER = TILE_WIDTH / 2;

}
