package cesak.matur;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MenuController implements KeyListener
{
    // region Singleton

    private static final MenuController MENU_CONTROLLER = new MenuController();

    private MenuController()
    {

    }

    public static MenuController getInstance()
    {
        return MENU_CONTROLLER;
    }

    // endregion

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        // Prevents controlling the menu while in game
        if (SceneManager.getInstance().notInMenu())
            return;

        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            MenuManager.getInstance().onStartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        // Escape - Quits the game
        if (e.getKeyCode() == 27)
        {
            System.exit(0);
        }
    }
}
