package cesak.matur;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener
{
    // region Singleton

    private static final Controller controller = new Controller();

    private Controller()
    {

    }

    public static Controller getInstance()
    {
        return controller;
    }

    // endregion

    private boolean isMoveF = false; // Is moving forward?
    private boolean isMoveB = false; // Is moving Backward?
    private boolean isMoveL = false; // Is moving left?
    private boolean isMoveR = false; // Is moving right?

    private boolean isRotateL = false; // Is rotating left?
    private boolean isRotateR = false; // Is rotating right?

    private boolean attacked = false; // Has attacked?

    public boolean isMoveF()
    {
        return isMoveF;
    }

    public boolean isMoveB()
    {
        return isMoveB;
    }

    public boolean isMoveL()
    {
        return isMoveL;
    }

    public boolean isMoveR()
    {
        return isMoveR;
    }

    public boolean isRotateL()
    {
        return isRotateL;
    }

    public boolean isRotateR()
    {
        return isRotateR;
    }

    public boolean isAttacked()
    {
        return attacked;
    }
// ---

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        // Prevents controlling the player character while in Menu
        if (GameManager.getInstance().notInGame())
            return;

        // W
        if (e.getKeyCode() == 87)
        {
            isMoveF = true;
        }
        // S
        if (e.getKeyCode() == 83)
        {
            isMoveB = true;
        }
        // A
        if (e.getKeyCode() == 65)
        {
            isMoveL = true;
        }
        // D
        if (e.getKeyCode() == 68)
        {
            isMoveR = true;
        }

        // Left Arrow
        if (e.getKeyCode() == 37)
        {
            isRotateL = true;
        }
        // Right Arrow
        if (e.getKeyCode() == 39)
        {
            isRotateR = true;
        }

        // Space
        if (e.getKeyCode() == 32)
        {
            if (!attacked)
            {
                attacked = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        // Space
        if (e.getKeyCode() == 32)
        {
            attacked = false;
            GameManager.getInstance().setScene(1);
        }
        // E
        if (e.getKeyCode() == 69)
        {
            Player.getInstance().interact();
            GameManager.getInstance().setScene(0);
        }

        // Prevents controlling the player character while in Menu
        if (GameManager.getInstance().notInGame())
            return;

        // W
        if (e.getKeyCode() == 87)
        {
            isMoveF = false;
        }
        // S
        if (e.getKeyCode() == 83)
        {
            isMoveB = false;
        }
        // A
        if (e.getKeyCode() == 65)
        {
            isMoveL = false;
        }
        // D
        if (e.getKeyCode() == 68)
        {
            isMoveR = false;
        }

        // Left Arrow
        if (e.getKeyCode() == 37)
        {
            isRotateL = false;
        }
        // Right Arrow
        if (e.getKeyCode() == 39)
        {
            isRotateR = false;
        }

        // Escape - Quits the game
        if (e.getKeyCode() == 27)
        {
            System.exit(0);
        }
    }
}
