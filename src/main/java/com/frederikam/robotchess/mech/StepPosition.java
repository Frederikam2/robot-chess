package com.frederikam.robotchess.mech;

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

}
