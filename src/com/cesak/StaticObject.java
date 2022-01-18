package com.cesak;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Type of Object which does not move or do anything. It stays in one position and can be destroyable.
 */
public class StaticObject extends Object
{
    private int health;
    private final boolean destroyable;
    private String[] drops;

    public boolean isDestroyed()
    {
        return health == 0;
    }

    public boolean isDestroyable()
    {
        return destroyable;
    }

    private BufferedImage imgDestroyed;

    public StaticObject(String img, double x, double y, double size, double yPos, double hitbox, boolean destroyable)
    {
        super(img, x, y, size, yPos, hitbox);

        health = 1;
        this.destroyable = destroyable;
        drops = new String[0];

        try
        {
            imgDestroyed = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + "barrel_destroyed" + ".png")));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

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
        health = 0;
        setMyImage(imgDestroyed);
        for (String string : drops)
        {
            switch (string)
            {
                case "healingPotion" -> World.getInstance().createPickable(new Pickable("healingPotion", getX(), getY(), 0.5, 0, 0.35, Pickable.Bonus.HEAL));
                case "magicPotion" -> World.getInstance().createPickable(new Pickable("magicPotion", getX(), getY(), 0.5, 0, 0.35, Pickable.Bonus.MAGIC));
            }
        }
    }
}
