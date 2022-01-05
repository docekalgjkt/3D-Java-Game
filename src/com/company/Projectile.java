package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Projectile extends Object
{
    private BufferedImage[] imgAnim;

    private int power;
    private double speed;
    private double angle;

    public void setPower(int p)
    {
        power = p;
    }

    public Projectile(String img, double x, double y, double size, double yPos, double hitbox, double speed, double angle, int animCount)
    {
        super(img, x, y, size, yPos, hitbox);

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

            double dist = Math.sqrt((x - entity.getX()) * (x - entity.getX()) + (y - entity.getY()) * (y - entity.getY()));
            double hitbox = Math.sqrt((getHitbox() + entity.getHitbox()) * (getHitbox() + entity.getHitbox()));
            if (dist <= hitbox)
            {
                entity.getDamage(power);
                World.getInstance().destroyProjectile(this);
                return;
            }
        }

        List<StaticObject> staticObjects = World.getInstance().getStaticObjects();
        for (StaticObject staticObject : staticObjects)
        {
            if (!staticObject.isDestroyable() || staticObject.isDestroyed()) continue;

            double dist = Math.sqrt((x - staticObject.getX()) * (x - staticObject.getX()) + (y - staticObject.getY()) * (y - staticObject.getY()));
            double hitbox = Math.sqrt((getHitbox() + staticObject.getHitbox()) * (getHitbox() + staticObject.getHitbox()));
            if (dist <= hitbox)
            {
                staticObject.getDamage(power);
                World.getInstance().destroyProjectile(this);
                return;
            }
        }

        if (Collision.hitWall(x, y, getHitbox()))
        {
            World.getInstance().destroyProjectile(this);
        }
    }
}
