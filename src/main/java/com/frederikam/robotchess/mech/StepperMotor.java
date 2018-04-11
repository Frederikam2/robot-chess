package com.frederikam.robotchess.mech;

import com.google.common.util.concurrent.AtomicDouble;
import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.frederikam.robotchess.Launcher.gpio;

public class StepperMotor {

    private static final Logger log = LoggerFactory.getLogger(StepperMotor.class);
    private static final int STEPS_PER_REVOLUTION = 400;
    private final GpioStepperMotorComponent motor;
    private AtomicDouble position = new AtomicDouble(0);
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            (r) -> {
                Thread t = new Thread(r, "microswitch-observer");
                t.setDaemon(true);
                return t;
            }
    );
    private final GpioPinDigitalInput swtch;
    private volatile boolean goingForward = false;

    StepperMotor(Pin pin1, Pin pin2,
                         Pin pin3, Pin pin4,
                         Pin microswitch) {
        GpioPinDigitalOutput pin11 = gpio.provisionDigitalOutputPin(pin1, PinState.LOW);
        GpioPinDigitalOutput pin21 = gpio.provisionDigitalOutputPin(pin3, PinState.LOW);
        GpioPinDigitalOutput pin31 = gpio.provisionDigitalOutputPin(pin2, PinState.LOW);
        GpioPinDigitalOutput pin41 = gpio.provisionDigitalOutputPin(pin4, PinState.LOW);
        swtch = gpio.provisionDigitalInputPin(microswitch);
        executor.scheduleAtFixedRate(this::recurringTask, 0, 5, TimeUnit.MILLISECONDS);

        final GpioPinDigitalOutput[] pins = {
                pin11,
                pin21,
                pin31,
                pin41};

        // this will ensure that the motor is stopped when the program terminates
        gpio.setShutdownOptions(true, PinState.LOW, pins);

        // create motor component
        motor = new GpioStepperMotorComponent(pins);

        // create byte array to demonstrate a single-step sequencing
        // (This is the most basic method, turning on a single electromagnet every time.
        //  This sequence requires the least amount of energy and generates the smoothest movement.)
        byte[] singleStepForwardSeq = new byte[4];
        singleStepForwardSeq[0] = (byte) 0b0001;
        singleStepForwardSeq[1] = (byte) 0b0010;
        singleStepForwardSeq[2] = (byte) 0b0100;
        singleStepForwardSeq[3] = (byte) 0b1000;

        motor.setStepsPerRevolution(STEPS_PER_REVOLUTION);
        motor.setStepSequence(singleStepForwardSeq);
    }

    public void step(double steps) {
        double startPos = position.get();
        double newPos = position.addAndGet(steps);
        motor.setStepInterval(2);

        // Calculate the steps that we need, with respect to mitigating rounding errors
        int roundedSteps = (int) (Math.floor(newPos) - Math.floor(startPos));
        log.info("Rounded {}", roundedSteps);

        if (steps > 0 && swtch.isHigh()) {
            log.warn("Ignored movement because we are already at 0!");
            position.set(0);
        } else {
            goingForward = steps > 0;
            motor.step(roundedSteps);
            goingForward = false;
        }
    }

    public void stepTo(double newPosition) {
        step(newPosition - position.get());
    }

    public double getPosition() {
        return position.get();
    }

    private void recurringTask() {
        if(goingForward && swtch.isHigh()) {
            log.info("Motor triggered microswitch");

            motor.stop();
            position.set(0);
        }
    }
}