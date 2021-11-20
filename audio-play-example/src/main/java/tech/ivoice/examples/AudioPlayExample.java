package tech.ivoice.examples;


import org.mobicents.media.server.component.audio.AudioComponent;
import org.mobicents.media.server.component.audio.AudioMixer;
import org.mobicents.media.server.component.audio.SoundCard;
import org.mobicents.media.server.impl.resource.mediaplayer.audio.AudioPlayerImpl;
import org.mobicents.media.server.scheduler.Clock;
import org.mobicents.media.server.scheduler.PriorityQueueScheduler;
import org.mobicents.media.server.scheduler.WallClock;

public class AudioPlayExample {
    private final AudioPlayerImpl audioPlayer;
    private final SoundCard soundCard;

    private final AudioMixer mixer;

    private final PriorityQueueScheduler scheduler;

    public AudioPlayExample() {
        Clock clock = new WallClock();

        scheduler = new PriorityQueueScheduler();
        scheduler.setClock(clock);

        audioPlayer = new AudioPlayerImpl("player", scheduler);
        soundCard = new SoundCard(scheduler);

        AudioComponent playerAudioComponent = new AudioComponent(1);
        AudioComponent soundCardComponent = new AudioComponent(2);

        playerAudioComponent.updateMode(true, true);
        soundCardComponent.updateMode(true, true);

        playerAudioComponent.addInput(audioPlayer.getAudioInput());
        soundCardComponent.addOutput(soundCard.getAudioOutput());

        mixer = new AudioMixer(scheduler);
        mixer.addComponent(playerAudioComponent);
        mixer.addComponent(soundCardComponent);
    }

    public static void main(String[] args) throws Exception {
        AudioPlayExample example = new AudioPlayExample();
        example.start();
    }

    private void start() throws Exception {
        soundCard.activate();

        mixer.start();

        scheduler.start();

        audioPlayer.setURL("http://audios.ivoice.online/tests/goodbye.wav");
        audioPlayer.activate();

        Thread.sleep(2000);

        audioPlayer.deactivate();
        soundCard.deactivate();
        mixer.stop();

        scheduler.stop();
        System.exit(0); // scheduler is not stopped correctly
    }
}
