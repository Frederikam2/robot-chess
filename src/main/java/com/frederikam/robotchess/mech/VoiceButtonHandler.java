package com.frederikam.robotchess.mech;

import com.frederikam.robotchess.Launcher;
import com.frederikam.robotchess.audio.SpeechService;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoiceButtonHandler implements GpioPinListenerDigital {

    private static final Logger log = LoggerFactory.getLogger(VoiceButtonHandler.class);
    private final SpeechService speechService;

    public VoiceButtonHandler(Pin pin, SpeechService speechService) {
        this.speechService = speechService;
        Launcher.gpio.provisionDigitalInputPin(pin).addListener(this);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        log.info("PTT: {}", event.getState().isLow());
        speechService.setListening(event.getState().isLow());
    }
}
