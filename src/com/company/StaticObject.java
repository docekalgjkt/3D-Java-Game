package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class StaticObject extends Object
{
    private int health;
    private final boolean destroyable;


    public boolean isDestroyed()
    {
        return health == 0;
    }

    public boolean isDestroyable()
    {
        return destroyable;
    }

    private BufferedImage imgDestroyed;

    public StaticObject(double x, double y, double size, double yPos, double hitbox, boolean destroyable, String img)
    {
        super(x, y, size, yPos, hitbox, img);

        health = 1;
        this.destroyable = destroyable;

        try
        {
            imgDestroyed = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + "barrel_destroyed" + ".png")));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void getDamage(int d)
    {
        health -= d;

        if (health <= 0)
        {
            getDestroyed();
        }
    }

    private void getDestroyed()
    {
        health = 0;
        setMyImage(imgDestroyed);
    }
}
