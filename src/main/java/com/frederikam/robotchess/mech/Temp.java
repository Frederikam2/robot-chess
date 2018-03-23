package com.frederikam.robotchess.mech;

import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Temp {

    private static final Logger log = LoggerFactory.getLogger(Temp.class);

    public static void main(String[] args) throws InterruptedException {
        StepperMotor stepperX = new StepperMotor(RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_09);
        StepperMotor stepperY = new StepperMotor(RaspiPin.GPIO_04, RaspiPin.GPIO_05, RaspiPin.GPIO_06, RaspiPin.GPIO_07, RaspiPin.GPIO_10);

        boolean b = false;
        while (true) {
            b = !b;
            log.info(stepperX.getPosition() + "");
            stepperX.step(b ? 400 : -400, 2);
        }

        /*ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.submit(() -> stepperX.step(1600, 2));
        exec.submit(() -> stepperY.step(-1600, 2));
        exec.awaitTermination(1, TimeUnit.DAYS);*/
    }
}
