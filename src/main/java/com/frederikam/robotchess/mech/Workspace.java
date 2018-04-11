package com.frederikam.robotchess.mech;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import javafx.util.Pair;
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

    private static final double MIN_STEP_INTERVAL = 2;

    private final StepperMotor stepperX;
    private final StepperMotor stepperY;
    private final ExecutorService stepperExecutor = Executors.newFixedThreadPool(2);
    private final GpioPinDigitalOutput magnet;

    public Workspace() {
        stepperX = new StepperMotor(RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_12);
        stepperY = new StepperMotor(RaspiPin.GPIO_04, RaspiPin.GPIO_05, RaspiPin.GPIO_06, RaspiPin.GPIO_07, RaspiPin.GPIO_10);
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

        Future futureX = stepperExecutor.submit(() -> runStepper(stepperX, deltaX, time));
        Future futureY = stepperExecutor.submit(() -> runStepper(stepperY, deltaY, time));

        try {
            futureX.get();
            futureY.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void runStepper(StepperMotor stepper, double steps, int time) {
        if (time == 0 || steps == 0) return;

        double targetCycleSteps = 100; // 1/4 revolution
        double cycles = steps / targetCycleSteps;

        // Adjust the number of cycles to a non-zero integer
        cycles = Math.max(1, Math.round(cycles));
        int stepsPerCycle = (int) (steps / cycles);
        //noinspection CodeBlock2Expr
        try {
            NanosecondExecutor[] executor = new NanosecondExecutor[1];
            executor[0] = new NanosecondExecutor(() -> {
                stepper.step(stepsPerCycle);

                if (stepper.getPosition() == 0 && steps < 0)
                    executor[0].stop();
            }, (int) cycles, (int) ((time / cycles) * 1000000));

            executor[0].run();
        } catch (InterruptedException e) {
            log.error("Interrupted while running stepper", e);
        }
    }

    @Override
    public void setMagnetEnabled(boolean enabled) {
        magnet.setState(enabled);
    }

    public Pair<StepperMotor, StepperMotor> getMotors() {
        return new Pair<>(stepperX, stepperY);
    }

}
