package cesak.matur;

import java.io.File;
import java.util.Objects;
import javax.sound.sampled.*;

public class SoundManager
{
    // region Singleton

    private static final SoundManager soundManager = new SoundManager();

    private SoundManager()
    {

    }

    public static SoundManager getInstance()
    {
        return soundManager;
    }

    // endregion

    public void playSound(String fileName)
    {
        try
        {
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("sounds/" + fileName + ".wav")).getFile());
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;

            stream = AudioSystem.getAudioInputStream(file);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        } catch (Exception ignored)
        {
        }
    }

    public void playLoop(String fileName)
    {
        try
        {
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("sounds/" + fileName + ".wav")).getFile());
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;

            stream = AudioSystem.getAudioInputStream(file);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.setLoopPoints(0, -1);
            clip.start();

        } catch (Exception ignored)
        {
        }
    }
}
