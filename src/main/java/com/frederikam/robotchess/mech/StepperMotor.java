package com.frederikam.robotchess.mech;

import com.google.common.util.concurrent.AtomicDouble;
import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import static com.frederikam.robotchess.Launcher.gpio;

public class StepperMotor {

    private final GpioPinDigitalOutput pin1;
    private final GpioPinDigitalOutput pin2;
    private final GpioPinDigitalOutput pin3;
    private final GpioPinDigitalOutput pin4;
    private final GpioStepperMotorComponent motor;
    private AtomicDouble position = new AtomicDouble(0);

    public StepperMotor(Pin pin1, Pin pin2,
                        Pin pin3, Pin pin4,
                        int stepsPerRevolution) {
        this.pin1 = gpio.provisionDigitalOutputPin(pin1, PinState.LOW);
        this.pin2 = gpio.provisionDigitalOutputPin(pin3, PinState.LOW);
        this.pin3 = gpio.provisionDigitalOutputPin(pin2, PinState.LOW);
        this.pin4 = gpio.provisionDigitalOutputPin(pin4, PinState.LOW);

        final GpioPinDigitalOutput[] pins = {
                this.pin1,
                this.pin2,
                this.pin3,
                this.pin4};

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

        motor.setStepsPerRevolution(stepsPerRevolution);
        motor.setStepSequence(singleStepForwardSeq);
        motor.setStepInterval(2); // Full speed for these particular steppers
    }

    StepperMotor(Pin pin1, Pin pin2, Pin pin3, Pin pin4) {
        this(pin1, pin2, pin3, pin4, 400);
    }

    public void step(double steps) {
        double startPos = position.get();
        position.addAndGet(steps);

        // Calculate the steps that we need, with respect to mitigating rounding errors
        int roundedSteps = (int) (Math.floor(steps) - Math.floor(position.get()));
        motor.step(roundedSteps);
    }
}