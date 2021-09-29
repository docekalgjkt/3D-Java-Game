package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Projectile extends Object
{
    private double speed;
    private double angle;

    private BufferedImage[] imgAnim;

    public Projectile(double x, double y, double yPos, double speed, double angle, String img, int animCount)
    {
        super(x, y, yPos, img);

        this.speed = speed;
        this.angle = angle;

        try
        {
            imgAnim = new BufferedImage[animCount];
            for (int i = 0; i < imgAnim.length; i++)
            {
                imgAnim[i] = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + "_anim" + i + ".png")));
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void move()
    {
        double x = getX();
        double y = getY();

        double nextX = x + Math.cos(angle / 180.0 * Math.PI) * (speed / (600));
        double nextY = y + Math.sin(angle / 180.0 * Math.PI) * (speed / (600));

        x = nextX;
        y = nextY;

        if (World.getInstance().getTile((int) y, (int) x).equals("#"))
        {
            World.getInstance().destroyProjectile(this);
        }

        setX(x);
        setY(y);

        List<Entity> entities = World.getInstance().getEntities();
        for (Entity entity : entities)
        {
            if (entity.isDead()) continue;

            double dist = (x - entity.getX()) * (x - entity.getX()) + (y - entity.getY()) * (y - entity.getY());
            if (dist <= 0.4 * 0.4)
            {
                entity.getDamage(1);
                World.getInstance().destroyProjectile(this);
                return;
            }
        }

        List<StaticObject> staticObjects = World.getInstance().getStaticObjects();
        for (StaticObject staticObject : staticObjects)
        {
            if (!staticObject.isDestroyable() || staticObject.isDestroyed()) continue;

            double dist = (x - staticObject.getX()) * (x - staticObject.getX()) + (y - staticObject.getY()) * (y - staticObject.getY());
            if (dist <= 0.4 * 0.4)
            {
                staticObject.getDamage(1);
                World.getInstance().destroyProjectile(this);
                return;
            }
        }
    }
}
