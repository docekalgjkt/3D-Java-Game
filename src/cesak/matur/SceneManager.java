package cesak.matur;

import javax.swing.*;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SceneManager
{
    // region Singleton

    private static final SceneManager SCENE_MANAGER = new SceneManager();

    private SceneManager()
    {

    }

    public static SceneManager getInstance()
    {
        return SCENE_MANAGER;
    }

    // endregion

    private final String[] sceneNames = {
            "Menu",
            "Game"
    };

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
        Window.getInstance().switchScene(sceneNames[i]);
    }

    /**
     * @return Returns whether the game scene is loaded or not to prevent controlling the player when another scene is loaded
     */
    public boolean notInGame()
    {
        return !Objects.equals(sceneNames[currentScene], "Game");
    }

    /**
     * @return Returns whether the menu scene is loaded or not to prevent controlling the menu when another scene is loaded
     */
    public boolean notInMenu()
    {
        return !Objects.equals(sceneNames[currentScene], "Menu");
    }

    // ---

    public int getCurrentScene()
    {
        return currentScene;
    }

    // ---

    public void setUp()
    {
        Window.getInstance().start();
        setScene(0);
    }

    /**
     * Starts the actual game
     */
    public void newGame()
    {
        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("story/intro.txt");

        StringBuilder sb = new StringBuilder();
        for (String s : list)
        {
            sb.append(s);
        }

        JOptionPane.showMessageDialog(null, sb.toString());

        startGame();
    }

    private void startGame()
    {
        LevelManager.getInstance().loadLevel(0);

        // Player Set Up
        Player.getInstance().setUp();
        Player.getInstance().getMyWeapon().addAmmunition(1, 20);

        setScene(1);
        GameManager.getInstance().start();
    }

    public void endGame()
    {
        GameController.getInstance().reset();
        GameManager.getInstance().stop();
        setScene(0);
    }
}
