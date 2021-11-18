package tech.ivoice.lab.media;

import org.restcomm.media.core.component.audio.AudioComponent;
import org.restcomm.media.core.component.audio.AudioMixer;
import org.restcomm.media.core.component.audio.Sine;
import org.restcomm.media.core.resource.recorder.audio.AudioRecorderImpl;
import org.restcomm.media.core.scheduler.Clock;
import org.restcomm.media.core.scheduler.PriorityQueueScheduler;
import org.restcomm.media.core.scheduler.WallClock;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Sine generates audio on defined frequency. This audio is recorded into a file.
 *
 * @see Sine
 * @see AudioRecorderImpl
 */
public class SineRecordingExample {
    private static final String RECORD = "/tmp/test.wav";

    private final Sine sine;
    private final AudioRecorderImpl recorder;

    private final AudioMixer mixer;

    private final PriorityQueueScheduler scheduler;

    public SineRecordingExample() throws Exception {
        Clock clock = new WallClock();

        scheduler = new PriorityQueueScheduler();
        scheduler.setClock(clock);

        sine = new Sine(scheduler);
        recorder = new AudioRecorderImpl(scheduler, (bytes, i, i1) -> false);
        recorder.setRecordFile("file://" + RECORD, false);

        AudioComponent sineComponent = new AudioComponent(1);
        AudioComponent recorderComponent = new AudioComponent(2);

        sineComponent.updateMode(true, true);
        recorderComponent.updateMode(true, true);

        sineComponent.addInput(sine.getAudioInput());
        recorderComponent.addOutput(recorder.getAudioOutput());

        // mixer passes data from Sine to Recorder
        mixer = new AudioMixer(scheduler);
        mixer.addComponent(sineComponent);
        mixer.addComponent(recorderComponent);
    }

    public static void main(String[] args) throws Exception {
        Files.deleteIfExists(Paths.get(RECORD));
        Files.deleteIfExists(Paths.get(RECORD + "~"));

        SineRecordingExample example = new SineRecordingExample();
        example.start();
    }

    private void start() throws Exception {
        // QUEUING tasks (they are not actually running), see PriorityQueueScheduler
        recorder.activate();

        sine.setFrequency(300);
        sine.activate();

        mixer.start();

        // START PROCESSING: worker threads started to process queued tasks
        scheduler.start();

        Thread.sleep(500);
        sine.setFrequency(250);
        Thread.sleep(500);

        sine.deactivate();
        recorder.deactivate();
        mixer.stop();

        scheduler.stop();
        System.exit(0); // scheduler is not stopped correctly
    }
}
