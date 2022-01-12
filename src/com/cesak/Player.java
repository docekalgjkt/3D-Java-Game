package com.cesak;

import java.util.List;

/**
 * Třída popisující objekt hráče
 */
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
    private int mana = 50;
    private int manaMax = 50;
    private int healthRegen = 0;
    private int healthRegenFrame;
    private int healthRegenTick = 90;
    private int manaRegen = 1;
    private int manaRegenFrame;
    private int magicRegenTick = 6;
    private int magicCost = 15;
    private double speed = 30.0;
    private double x;
    private double y;
    private double angle = 0;
    private double nearClip = 0.3;
    private double hitbox = 0.2;

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
        return (double) mana / manaMax;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
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

    // -----

    public void setHealth(int h)
    {
        health = h;
    }

    // -----

    /**
     * Moves the player in X and Z axis
     *
     * @param a Angle which represents the direction in which the player will move
     */
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

    /**
     * Rotates the player around Y axis (left and right)
     *
     * @param dir Direction and also speed which the player will rotate with
     */
    public void rotate(double dir)
    {
        angle += 2.5 * dir;
        if (angle >= 360) angle -= 360;
        else if (angle < 0) angle += 360;
    }

    /**
     * Sets player position
     */
    public void place(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Method called each frame, regenerating some health points to player every X-th frame
     */
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

    /**
     * Method called each frame, regenerating some mana points to player every X-th frame
     */
    public void magicRegen()
    {
        if (mana == manaMax) return;
        manaRegenFrame++;
        if (manaRegenFrame == magicRegenTick)
        {
            manaRegenFrame = 0;
            getMana(manaRegen);
        }
    }

    /**
     * Spawns a fireball that flies in the player's look direction damaging enemies on hit
     */
    public void castFireball()
    {
        if (mana < magicCost) return;

        Projectile fireball = new Projectile("fireball", Player.getInstance().getX(), Player.getInstance().getY(), 1, 1.0 / 16, 0.05, 50, angle, 0);
        fireball.setLit(true);
        fireball.setPower(2);
        World.getInstance().createProjectile(fireball);
        mana -= magicCost;
    }

    public void interact()
    {
        // Interacting with a Wall-like Object
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

    /**
     * Instant health gain for the player
     *
     * @param h Amount of health the player will get
     */
    public void getHeal(int h)
    {
        health += h;
        if (health > healthMax)
            health = healthMax;
    }

    /**
     * Instant mana gain for the player
     *
     * @param m Amount of mana the player will get
     */
    public void getMana(int m)
    {
        mana += m;
        if (mana > manaMax)
        {
            mana = manaMax;
        }
    }

    /**
     * Damages the player
     *
     * @param dmg Amount of health the player will loose
     */
    public void getDamage(int dmg)
    {
        health -= dmg;
        System.out.println("Ouch, -" + dmg + " health");

        if (health <= 0)
        {
            die();
        }
    }

    /**
     * Method called when the player dies
     */
    private void die()
    {
        health = 0;
        World.getInstance().reset();
        System.out.println("You died!");
    }
}
