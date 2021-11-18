package tech.ivoice.examples;

import org.restcomm.media.core.component.audio.AudioComponent;
import org.restcomm.media.core.component.audio.AudioMixer;
import org.restcomm.media.core.component.audio.Sine;
import org.restcomm.media.core.component.audio.SoundCard;
import org.restcomm.media.core.scheduler.Clock;
import org.restcomm.media.core.scheduler.PriorityQueueScheduler;
import org.restcomm.media.core.scheduler.WallClock;

/**
 * Sine generates audio on defined frequency. This audio is played.
 *
 * @see Sine
 * @see org.restcomm.media.core.component.audio.SoundCard
 */
public class SinePlayingExample {
    private final Sine sine;
    private final SoundCard soundCard;

    private final AudioMixer mixer;

    private final PriorityQueueScheduler scheduler;

    public SinePlayingExample() {
        Clock clock = new WallClock();

        scheduler = new PriorityQueueScheduler();
        scheduler.setClock(clock);

        sine = new Sine(scheduler);
        soundCard = new SoundCard(scheduler);

        AudioComponent sineComponent = new AudioComponent(1);
        AudioComponent soundCardComponent = new AudioComponent(2);

        sineComponent.updateMode(true, true);
        soundCardComponent.updateMode(true, true);

        sineComponent.addInput(sine.getAudioInput());
        soundCardComponent.addOutput(soundCard.getAudioOutput());

        // mixer passes data from Sine to Recorder
        mixer = new AudioMixer(scheduler);
        mixer.addComponent(sineComponent);
        mixer.addComponent(soundCardComponent);
    }

    public static void main(String[] args) throws Exception {
        SinePlayingExample example = new SinePlayingExample();
        example.start();
    }

    private void start() throws Exception {
        // QUEUING tasks (they are not actually running), see PriorityQueueScheduler
        soundCard.activate();

        sine.setFrequency(300);
        sine.activate();

        mixer.start();

        // START PROCESSING: worker threads started to process queued tasks
        scheduler.start();

        Thread.sleep(500);
        sine.setFrequency(250);
        Thread.sleep(500);

        sine.deactivate();
        soundCard.deactivate();
        mixer.stop();

        scheduler.stop();
        System.exit(0); // scheduler is not stopped correctly
    }
}
