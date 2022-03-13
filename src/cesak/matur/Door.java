package cesak.matur;

import cesak.matur.LevelManager;

/**
 * An SceneObject the player is able to interact with
 * <br></br>
 * After interaction, it will do something
 */
public class Door
{
    private final int x, y;

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public Door(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void interact()
    {
        LevelManager.getInstance().setTile(y, x, ".");
    }
}
