package com.company;

import java.util.List;

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

    private int health = 100;
    private int healthMax = 100;
    private int magic = 100;
    private int magicMax = 100;
    private double x = 1.5;
    private double y = 1.5;
    private double angle = 0;
    private double nearClip = 0.3;
    private double hitbox = 0.2;
    private boolean sprinting;
    private boolean sneaking;

    public double getHealthPercent()
    {
        return (double) health / healthMax;
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

    public double getCamDistance()
    {
        return 10.0;
    }

    public double getNearClip()
    {
        return nearClip * nearClip;
    }


    public void move(int a)
    {
        double speed = 30.0;
        double nextX = x + Math.cos((angle + a) / 180.0 * Math.PI) * (speed * modSpeed() / (600));
        double nextY = y + Math.sin((angle + a) / 180.0 * Math.PI) * (speed * modSpeed() / (600));

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

    int healthRegenFrame;

    public void healthRegen()
    {
        if (health == healthMax) return;
        healthRegenFrame++;
        if (healthRegenFrame == 90)
        {
            healthRegenFrame = 0;
            health++;
        }
    }

    int magicRegenFrame;

    public void magicRegen()
    {
        if (magic == magicMax) return;
        magicRegenFrame++;
        if (magicRegenFrame == 5)
        {
            magicRegenFrame = 0;
            magic++;
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

    int magicCost = 30;

    public void castFireball()
    {
        if (magic < magicCost) return;
        Projectile fireball = new Projectile(Player.getInstance().getX(), Player.getInstance().getY(), 1, 1.0 / 16, 0.05, 50, Player.getInstance().getAngle(), "fireball", 0);
        fireball.setLit(true);
        fireball.setPower(2);
        World.getInstance().createProjectile(fireball);
        magic -= magicCost;
    }

    public void getHeal(int h)
    {
        health += h;
        if (health > healthMax)
            health = healthMax;
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
        System.out.println("You died!");
    }
}
