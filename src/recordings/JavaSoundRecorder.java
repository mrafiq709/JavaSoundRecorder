/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.sound.sampled.*;
import java.io.*;

/**
 *
 * @author MHC
 */
public class JavaSoundRecorder {

    // record duration, in milliseconds
    static final long RECORD_TIME = 15000;  // 1 minute

    // path of the wav file
    File wavFile = new File("F:/Test/RecordAudio.wav");

    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    /**
     * Captures the sound and record into a WAV file
     */
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing

            System.out.println("Start capturing...");

            AudioInputStream ais = new AudioInputStream(line);

            System.out.println("Start recording...");

            // start recording
            AudioSystem.write(ais, fileType, wavFile);

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }

    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
        final JavaSoundRecorder recorder = new JavaSoundRecorder();

        // This for Mixer Information in Current Computer
        // Start --------------------------------------- Here
        Mixer.Info[] info = AudioSystem.getMixerInfo();

        for (Mixer.Info info1 : info) {
            System.out.println(info1);
        }

        Port lineIn;
        FloatControl volCtrl = null;
        try {
            Mixer mixer = AudioSystem.getMixer(null);
            lineIn = (Port) mixer.getLine(Port.Info.LINE_IN);
            lineIn.open();
            volCtrl = (FloatControl) lineIn.getControl(
                    FloatControl.Type.VOLUME);

            // Assuming getControl call succeeds, 
            // we now have our LINE_IN VOLUME control.
        } catch (Exception e) {
            System.out.println("Failed trying to find LINE_IN"
                    + " VOLUME control: exception = " + e);
        }
        if (volCtrl != null) {
            System.out.println("Volume");
        }

        // End --------------------------------------- Here
        
        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
            }
        });

        stopper.start();

        // start recording
        recorder.start();

    }
}
