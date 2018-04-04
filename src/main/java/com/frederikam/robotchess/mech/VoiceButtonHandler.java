package com.frederikam.robotchess.mech;

import com.frederikam.robotchess.Launcher;
import com.frederikam.robotchess.audio.SpeechService;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class VoiceButtonHandler implements GpioPinListenerDigital {

    private final SpeechService speechService;

    public VoiceButtonHandler(Pin pin, SpeechService speechService) {
        this.speechService = speechService;
        Launcher.gpio.provisionDigitalInputPin(pin).addListener(this);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        speechService.setListening(event.getState().isHigh());
    }
}
