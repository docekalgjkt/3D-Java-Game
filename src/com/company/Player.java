package com.company;

import java.util.List;
import java.util.Random;

public class Player
{

    // region Singleton

    private static Player player = null;

    public static Player getInstance()
    {
        if (player == null)
        {
            player = new Player();
        }
        return player;
    }

    // endregion

    private int health = 15;
    private int healthMax = 15;
    private int magic = 50;
    private int magicMax = 50;
    private int healthRegen = 0;
    private int healthRegenFrame;
    private int healthRegenTick = 90;
    private int magicRegen = 1;
    private int magicRegenFrame;
    private int magicRegenTick = 6;
    private int magicCost = 15;
    private double speed = 30.0;
    private double x;
    private double y;
    private double angle = 0;
    private int angleY = 0;
    private int yRotLimit = Game.getInstance().getScreenHeight() / 2;
    private double nearClip = 0.3;
    private double hitbox = 0.2;
    private boolean sprinting;
    private boolean sneaking;

    // -----

    public double getHealthPercent()
    {
        return (double) health / healthMax;
    }

    public int getHealth()
    {
        return health;
    }

    public int getHealthMax()
    {
        return healthMax;
    }

    public double getMagicPercent()
    {
        return (double) magic / magicMax;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    private double modSpeed()
    {
        double res = 1;

        res *= (sprinting)
                ? 1.75
                : ((sneaking) ? 0.5 : 1);

        return res;
    }

    public double getAngle()
    {
        return angle;
    }

    public int getAngleY()
    {
        return angleY;
    }

    public double getCamDistance()
    {
        return 10.0;
    }

    public double getNearClip()
    {
        return nearClip * nearClip;
    }

    // -----

    public void setHealth(int h)
    {
        health = h;
    }

    // -----

    public void move(int a)
    {
        double nextX = x + Math.cos((angle + a) / 180.0 * Math.PI) * (speed/* * modSpeed()*/ / (600));
        double nextY = y + Math.sin((angle + a) / 180.0 * Math.PI) * (speed/* * modSpeed()*/ / (600));

        x = nextX;
        y = nextY;

        if (Collision.hitWall(x, y, hitbox))
        {
            if (x != Collision.getNextX()) x = Collision.getNextX();
            if (y != Collision.getNextY()) y = Collision.getNextY();
        }
        if (Collision.hitObject(x, y, hitbox))
        {
            if (x != Collision.getNextX()) x = Collision.getNextX();
            if (y != Collision.getNextY()) y = Collision.getNextY();
        }

        if (Collision.hitPickable(x, y, hitbox))
        {
            if (Collision.hitPickables.size() > 0)
            {
                for (int i = Collision.hitPickables.size() - 1; i >= 0; i--)
                {
                    Collision.hitPickables.get(i).pick();
                }
            }
        }
    }

    public void sprint(boolean b)
    {
        sprinting = b;
        sneaking = false;
    }

    public void sneak(boolean b)
    {
        sneaking = b;
        sprinting = false;
    }

    public void rotate(double dir)
    {
        angle += 2.5 * dir;
        if (angle >= 360) angle -= 360;
        else if (angle < 0) angle += 360;
    }

    public void rotateY(double dir)
    {
        angleY -= dir;
        if (angleY > yRotLimit) angleY = yRotLimit;
        else if (angleY < -yRotLimit) angleY = -yRotLimit;
    }

    public void place(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void healthRegen()
    {
        if (health == healthMax) return;
        healthRegenFrame++;
        if (healthRegenFrame == healthRegenTick)
        {
            healthRegenFrame = 0;
            getHeal(healthRegen);
        }
    }

    public void magicRegen()
    {
        if (magic == magicMax) return;
        magicRegenFrame++;
        if (magicRegenFrame == magicRegenTick)
        {
            magicRegenFrame = 0;
            getMagic(magicRegen);
        }
    }

    public void attack()
    {
        List<Entity> entities = World.getInstance().getEntities();
        for (int i = 1; i < entities.size(); i++)
        {
            for (int i1 = 0; i1 < i; i1++)
            {
                if (entities.get(i).distToPlayer() < entities.get(i1).distToPlayer())
                {
                    entities.add(i1, entities.get(i));
                    entities.remove(i + 1);
                }
            }
        }

        for (int i = 0; i < entities.size(); i++)
        {
            if (entities.get(i).isDead()) continue;

            if (entities.get(i).distToPlayer() <= 10 * 10)
            {
                double radius = 0.05;

                // TODO: Make radius to be same at all distances
                if (Math.abs(entities.get(i).getXPos() - 0.5) < radius)
                {
                    entities.get(i).getDamage(1);
                    break;
                }
            }
        }
    }

    public void castFireball()
    {
        if (magic < magicCost) return;
        Projectile fireball = new Projectile("fireball", Player.getInstance().getX(), Player.getInstance().getY(), 1, 1.0 / 16, 0.05, 50, angle, 0);
        fireball.setLit(true);
        fireball.setPower(2);
        World.getInstance().createProjectile(fireball);
        magic -= magicCost;
    }

    public void interact()
    {
        // Interact Blocks
        int nextX = (int) (x + (Math.cos(angle * Math.PI / 180) * 0.7));
        int nextY = (int) (y + (Math.sin(angle * Math.PI / 180) * 0.7));
        List<InteractBlock> interactBlocks = World.getInstance().getDoors();
        for (int i = 0; i < interactBlocks.size(); i++)
        {
            if (interactBlocks.get(i).getX() == nextX && interactBlocks.get(i).getY() == nextY)
            {
                interactBlocks.get(i).interact();
            }
        }
    }

    public void getHeal(int h)
    {
        health += h;
        if (health > healthMax)
            health = healthMax;
    }

    public void getMagic(int m)
    {
        magic += m;
        if (magic > magicMax)
        {
            magic = magicMax;
        }
    }

    public void getDamage(int dmg)
    {
        health -= dmg;
        System.out.println("Ouch, -" + dmg + " health");

        if (health <= 0)
        {
            die();
        }
    }

    private void die()
    {
        health = 0;
        World.getInstance().reset();
        System.out.println("You died!");
    }
}
