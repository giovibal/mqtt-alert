package io.github.giovibal;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by giovibal on 24/04/16.
 */
public class SoundPlayer {

    public void playWarningSound() {
        playSound("/car_chirp_x.wav");
    }
    public void playAlertSound() {
        playSound("/buzzer_x.wav");
    }
    public void playSound(String res) {
        try {
            // Open an audio input stream.
            // http://www.wavsource.com/sfx/sfx.htm
//            InputStream soundFile = getClass().getResourceAsStream("/car_chirp_x.wav");
            InputStream soundFile = getClass().getResourceAsStream(res);
//            InputStream soundFile = getClass().getResourceAsStream("/alarm_beep.wav");
            InputStream soundFileBuffered = new BufferedInputStream(soundFile);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFileBuffered);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
            long waitTime = clip.getMicrosecondLength()/1000;
            Thread.sleep(waitTime);
            clip.drain();
            clip.close();

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("Play end");
    }
}
