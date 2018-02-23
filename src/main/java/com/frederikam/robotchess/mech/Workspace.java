package com.frederikam.robotchess.mech;

import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * High-level wrapper of steppers and the magnet
 */
public class Workspace {

    private static final Logger log = LoggerFactory.getLogger(Workspace.class);

    private final StepperMotor stepperX;
    private final StepperMotor stepperY;
    private final ExecutorService stepperExecutor = Executors.newFixedThreadPool(2);

    public Workspace() {
        stepperX = new StepperMotor(RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03);
        stepperY = new StepperMotor(RaspiPin.GPIO_04, RaspiPin.GPIO_05, RaspiPin.GPIO_06, RaspiPin.GPIO_07);
    }

    public StepPosition getPosition() {
        return new StepPosition(stepperX.getPosition(), stepperY.getPosition());
    }

    // Move synchronously with both steppers
    private void moveSync(StepPosition position) throws InterruptedException {
        log.info("Moving x: {}, y: {}", position.x, position.y);
        Future futureX = stepperExecutor.submit(() -> stepperX.stepTo(position.x));
        Future futureY = stepperExecutor.submit(() -> stepperY.stepTo(position.y));

        try {
            futureX.get();
            futureY.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
