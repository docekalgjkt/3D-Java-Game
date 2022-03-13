package cesak.matur;

/**
 * An object that explodes and damages anyone who stands nearby when shot
 * After the explosion, the object is destroyed
 */
public class Explosive extends SceneObject
{
    private int health;

    public boolean isDestroyed()
    {
        return health == 0;
    }

    // ---

    public Explosive(int id, int x, int y)
    {
        super(id, x, y, "objects/object");

        health = 1;
    }

    // ---

    public void getShot()
    {
        health = 0;
    }
}
