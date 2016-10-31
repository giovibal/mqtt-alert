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
            Clip clip;
//            clip = AudioSystem.getClip();
            // PATCH UBUNTU
            DataLine.Info info = new DataLine.Info(Clip.class, audioIn.getFormat());
            clip = (Clip)AudioSystem.getLine(info);
            // PATCH UBUNTU END

            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
            while(clip.isRunning()) {
                Thread.yield();
            }
//            long waitTime = clip.getMicrosecondLength()/1000;
//            Thread.sleep(waitTime);
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

    public static void main(String[] args) {
        SoundPlayer p = new SoundPlayer();
        p.playAlertSound();
        p.playWarningSound();
//        p.playSound("/sounds-1045-sisfus.mp3");
    }
}
