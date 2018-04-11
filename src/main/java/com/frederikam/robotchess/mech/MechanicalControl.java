package com.frederikam.robotchess.mech;

import com.frederikam.robotchess.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MechanicalControl {

    private static final Logger log = LoggerFactory.getLogger(MechanicalControl.class);

    private final IWorkspace workspace;
    private final ExecutorService executor;

    public MechanicalControl(IWorkspace workspace) {
        this.workspace = workspace;
        executor = Executors.newSingleThreadExecutor(
                r -> new Thread(r, "mechanical-control")
        );
    }

    private void submit(Runnable r) {
        executor.submit(() -> {
            try {
                log.info("Running " + r);
                r.run();
            } catch (Exception e) {
                log.error("Caught fatal exception", e);
                System.exit(1);
            }
        });
    }

    public void queueDragAndDrop(StepPosition from, StepPosition to, boolean direct) {
        if (from.equals(to)) return;

        submit(() -> {
            workspace.moveToSync(from);
            workspace.setMagnetEnabled(true); // Start pulling

            if (direct) {
                workspace.moveToSync(to);
            } else {
                moveIndirect(from, to);
            }

            workspace.setMagnetEnabled(false);
        });
    }

    private void moveIndirect(StepPosition from, StepPosition to) {
        // Decide which side is most efficient to move to
        StepPosition diff = to.minus(from);
        boolean moveXFirst = Math.abs(diff.x) < Math.abs(diff.y);  // Which axis diff is lesser?
        if (moveXFirst) {
            double delta = diff.x > 0 ? Constants.HALF_TILE_WIDTH : -Constants.HALF_TILE_WIDTH;
            workspace.moveToSync(workspace.getPosition().plus(delta, 0));
        } else {
            double delta = diff.y > 0 ? Constants.HALF_TILE_WIDTH : -Constants.HALF_TILE_WIDTH;
            workspace.moveToSync(workspace.getPosition().plus(0, delta));
        }

        // Move on the other axis, now on the tile edges
        if (!moveXFirst) {
            // Handle x
            double delta = diff.x - Constants.HALF_TILE_WIDTH;
            workspace.moveToSync(workspace.getPosition().plus(delta, 0));
        } else {
            // Handle y
            double delta = diff.y - Constants.HALF_TILE_WIDTH;
            workspace.moveToSync(workspace.getPosition().plus(0, delta));
        }

        // Move the remaining length on the first axis we moved
        if (moveXFirst) {
            // Handle x
            workspace.moveToSync(new StepPosition(to.x, workspace.getPosition().y));
        } else {
            // Handle y
            workspace.moveToSync(new StepPosition(workspace.getPosition().x, to.y));
        }

        // And then move the remainder
        workspace.moveToSync(to);
    }

    public void reset() {
        // We will automatically stop any motor that triggers one of the microswitches
        log.info("Queued reset");
        submit(() -> workspace.moveToSync(new StepPosition(-10000, -10000)));
    }
}
