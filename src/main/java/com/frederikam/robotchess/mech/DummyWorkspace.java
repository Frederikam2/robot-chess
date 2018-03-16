package com.frederikam.robotchess.mech;

public class DummyWorkspace implements IWorkspace {

    private volatile StepPosition position = new StepPosition(0, 0);

    @Override
    public StepPosition getPosition() {
        return position;
    }

    @Override
    public void moveToSync(StepPosition position) {
        this.position = position;

    }

    @Override
    public void setMagnetEnabled(boolean enabled) {

    }
}
