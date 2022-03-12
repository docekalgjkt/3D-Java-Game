package cesak.matur;

import java.util.List;
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

    public int frameRate()
    {
        return 60;
    }

    /**
     * Index of currently running scene
     */
    private int currentScene;

    /**
     * Loads chosen scene
     *
     * @param i index of the scene you wish to load
     */
    public void setScene(int i)
    {
        currentScene = i;
        window.switchScene(sceneNames[i]);
    }

    /**
     * @return Returns whether the game scene is loaded or not to prevent controlling the player when another scene is loaded
     */
    public boolean notInGame()
    {
        return currentScene != 1;
    }

    // ---

    public void start()
    {
        window = new Window();
        window.start();
        currentScene = 0;
        setScene(1);

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
                Player.getInstance().move();
                Player.getInstance().rotate();
                Player.getInstance().attack();

                updateEnemies();

                window.redraw(currentScene);
            }
        }, 0, 1000 / frameRate());
    }

    /**
     * Updates all enemies, who are alive (Movement, Attack, Animation)
     */
    private void updateEnemies()
    {
        List<Enemy> entities = LevelManager.getInstance().getEnemies();

        for (Enemy enemy : entities)
        {
            // If the enemy is dead then we don't want it to make any actions
            if (enemy.isDead()) continue;

            // Moves toward the player if its distance from the player is close enough but not too close
            if (enemy.distToPlayer() < 6 * 6 && enemy.distToPlayer() > 0.75 * 0.75)
            {
                enemy.move();
            }
            // If the enemy is moving while the upper conditions are not met, it is stopped
            else if (enemy.isMoving())
            {
                enemy.stopMove();
            }

            // If the enemy is close enough to the player then it will attack
            if (enemy.distToPlayer() <= enemy.getAttackRange() * enemy.getAttackRange())
            {
                enemy.attack();
            }

            // Makes the enemy change his visual based on its current action
            enemy.anim();
        }
    }
}
