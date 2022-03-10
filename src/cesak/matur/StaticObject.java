package cesak.matur;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Type of SceneObject which does not move or do anything. It stays in one position and can be destroyable.
 */
public class StaticObject extends SceneObject
{
    private int health;
    private boolean destructible;
    private String[] drops;

    public boolean isDestroyed()
    {
        return health == 0;
    }

    public boolean isDestructible()
    {
        return destructible;
    }

    private BufferedImage imgDestroyed;

    // ---

    public StaticObject(int id, int x, int y)
    {
        super(id, x, y, "sceneObjects/object");

        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("sceneObjects/object" + id + "/static.txt");

        health = Integer.parseInt(list.get(0));
    }

    // ---

    public void setDrops(String[] strings)
    {
        drops = strings;
    }

    public void getDamage(int d)
    {
        health -= d;

        if (health <= 0)
        {
            getDestroyed();
        }
    }

    /**
     * Method called after this object is destroyed
     */
    private void getDestroyed()
    {
        /*health = 0;
        setMyImage(imgDestroyed);
        for (String string : drops)
        {
            switch (string)
            {
                case "healingPotion" -> World.getInstance().createPickable(new Pickable("healingPotion", getX(), getY(), 0.5, 0, 0.35, Pickable.Bonus.HEAL));
                case "magicPotion" -> World.getInstance().createPickable(new Pickable("magicPotion", getX(), getY(), 0.5, 0, 0.35, Pickable.Bonus.MANA));
            }
        }*/
    }
}
