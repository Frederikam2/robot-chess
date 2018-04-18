package com.frederikam.mercury;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;

public class AudioInputManager {
    
    private static final Logger log = LoggerFactory.getLogger(AudioInputManager.class);

    public TargetDataLine getInputLine() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info: mixerInfos){
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getTargetLineInfo();

            if (lineInfos.length == 0) continue;

            Line.Info lineInfo = lineInfos[0];
            boolean isMic = lineInfos[0].getLineClass().equals(TargetDataLine.class);

            if(isMic){//Only prints out info if it is a Microphone
                log.info("Selected mic: " + info.getName());//The name of the AudioDevice
                log.info("Mic description: " + info.getDescription());//The type of audio device
                try {
                    return (TargetDataLine) m.getLine(lineInfo);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("No microphones found");
    }
}
