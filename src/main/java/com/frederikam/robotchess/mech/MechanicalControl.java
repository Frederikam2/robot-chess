package com.frederikam.robotchess.mech;

import com.frederikam.robotchess.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MechanicalControl {

    private static final Logger log = LoggerFactory.getLogger(MechanicalControl.class);

    private final IWorkspace IWorkspace;
    private final ExecutorService executor;

    public MechanicalControl(IWorkspace IWorkspace) {
        this.IWorkspace = IWorkspace;
        executor = Executors.newSingleThreadExecutor(
                r -> new Thread(r, "mechanical-control")
        );
    }

    private void submit(Runnable r) {
        executor.submit(() -> {
            try {
                r.run();
            } catch (Exception e) {
                log.error("Caught fatal exception", e);
                System.exit(1);
            }
        });
    }

    public void queueMove(StepPosition position) {
        submit(() -> IWorkspace.moveToSync(position));
    }

    public void queueDragAndDrop(StepPosition from, StepPosition to) {
        if (from.equals(to)) return;

        submit(() -> {
            IWorkspace.moveToSync(from);
            IWorkspace.setMagnetEnabled(true); // Start pulling

            // Decide which side is most efficient to move to
            StepPosition diff = to.minus(from);
            boolean moveXFirst = Math.abs(diff.x) < Math.abs(diff.y);  // Which axis diff is lesser?
            if (moveXFirst) {
                double delta = diff.x > 0 ? Constants.HALF_TILE_WIDTH : -Constants.HALF_TILE_WIDTH;
                IWorkspace.moveToSync(IWorkspace.getPosition().plus(delta, 0));
            } else {
                double delta = diff.y > 0 ? Constants.HALF_TILE_WIDTH : -Constants.HALF_TILE_WIDTH;
                IWorkspace.moveToSync(IWorkspace.getPosition().plus(0, delta));
            }

            // Move on the other axis, now on the tile edges
            if (!moveXFirst) {
                // Handle x
                double delta = diff.x - Constants.HALF_TILE_WIDTH;
                IWorkspace.moveToSync(IWorkspace.getPosition().plus(delta, 0));
            } else {
                // Handle y
                double delta = diff.y - Constants.HALF_TILE_WIDTH;
                IWorkspace.moveToSync(IWorkspace.getPosition().plus(0, delta));
            }

            // Move the remaining length on the first axis we moved
            if (moveXFirst) {
                // Handle x
                IWorkspace.moveToSync(new StepPosition(to.x, IWorkspace.getPosition().y));
            } else {
                // Handle y
                IWorkspace.moveToSync(new StepPosition(IWorkspace.getPosition().x, to.y));
            }

            // And then move the remainder
            IWorkspace.moveToSync(to);
            IWorkspace.setMagnetEnabled(false);
        });
    }
}
