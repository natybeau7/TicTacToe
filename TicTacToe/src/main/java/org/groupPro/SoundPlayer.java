package org.groupPro;

import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {

    public static void playSound(String path) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        }
        catch (Exception e) {
            System.out.println("Sound Error");
        }
    }
}