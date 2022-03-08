package cesak.matur;

import java.util.Timer;
import java.util.TimerTask;

public class GameManager
{
    // region Singleton

    private static final GameManager gameManager = new GameManager();

    private GameManager()
    {

    }

    public static GameManager getInstance()
    {
        return gameManager;
    }

    // endregion

    private Window window;

    private final String[] sceneNames = {
            "Menu",
            "Game"
    };

    private int currentScene;

    public void setScene(int i)
    {
        currentScene = i;
        window.switchScene(sceneNames[i]);
    }

    public boolean inGame()
    {
        return currentScene == 1;
    }

    // ---

    public void start()
    {
        window = new Window();
        window.start();
        currentScene = 0;

        update();
    }

    /**
     * Does stuff every set period of time.
     * <br></br>
     * (Default: 60 fps)
     */
    void update()
    {
        java.util.Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                //Player.getInstance().healthRegen();
                //Player.getInstance().manaRegen();

                Player.getInstance().move();
                Player.getInstance().rotate();

                window.redraw(currentScene);
            }
        }, 0, 1000 / 60);
    }
}
