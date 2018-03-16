package com.frederikam.robotchess.mech;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.frederikam.robotchess.Launcher.gpio;

/**
 * High-level wrapper of steppers and the magnet
 */
public class Workspace implements IWorkspace {

    private static final Logger log = LoggerFactory.getLogger(Workspace.class);

    private static final double MIN_STEP_INTERVAL = 5;

    private final StepperMotor stepperX;
    private final StepperMotor stepperY;
    private final ExecutorService stepperExecutor = Executors.newFixedThreadPool(2);
    private final GpioPinDigitalOutput magnet;

    public Workspace() {
        stepperX = new StepperMotor(RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03);
        stepperY = new StepperMotor(RaspiPin.GPIO_04, RaspiPin.GPIO_05, RaspiPin.GPIO_06, RaspiPin.GPIO_07);
        magnet = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08);
    }

    @Override
    public StepPosition getPosition() {
        return new StepPosition(stepperX.getPosition(), stepperY.getPosition());
    }

    // Move synchronously with both steppers
    @Override
    public void moveToSync(StepPosition position)  {
        double deltaX = position.x - stepperX.getPosition();
        double deltaY = position.y - stepperY.getPosition();

        int time = (int) Math.max(
                Math.abs(deltaX * MIN_STEP_INTERVAL),
                Math.abs(deltaY * MIN_STEP_INTERVAL));

        log.info("Moving to x: {}, y: {}, time: {}ms", position.x, position.y, time);

        Future futureX = stepperExecutor.submit(() -> runStepperX(deltaX, time));
        Future futureY = stepperExecutor.submit(() -> runStepperY(deltaY, time));

        try {
            futureX.get();
            futureY.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void runStepperX(double steps, int time) {
        if (time == 0 || steps == 0) return;
        // Ranging from 0 to 1
        double speed = (MIN_STEP_INTERVAL * ((double) Math.abs(steps))) / ((double) time);
        double interval = MIN_STEP_INTERVAL / speed;
        // Stepper X supports variable speed, unlike stepper Y which behaves weird
        stepperX.step(steps, (int) interval);
    }

    private void runStepperY(double steps, int time) {
        if (time == 0 || steps == 0) return;

        // Stepper Y steps at a constant interval of 4ms.
        double interval = 4;
        double targetCycleSteps = 25; // 1/16 revolution
        double cycles = steps / targetCycleSteps;

        // Adjust the number of cycles to a non-zero integer
        cycles = Math.max(1, Math.round(cycles));
        int stepsPerCycle = (int) (steps / cycles);
        //noinspection CodeBlock2Expr
        try {
            new NanosecondExecutor(() -> {
                stepperY.step(stepsPerCycle, (int) interval);
            }, (int) cycles, (int) ((time / cycles) * 1000000))
                    .run();
        } catch (InterruptedException e) {
            log.error("Interrupted while running stepper", e);
        }
    }

    @Override
    public void setMagnetEnabled(boolean enabled) {
        magnet.setState(enabled);
    }

}
