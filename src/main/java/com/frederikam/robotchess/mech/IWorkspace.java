package com.frederikam.robotchess.mech;

public interface IWorkspace {
    StepPosition getPosition();

    // Move synchronously with both steppers
    void moveToSync(StepPosition position, int time) throws InterruptedException;

    void setMagnetEnabled(boolean enabled);
}
