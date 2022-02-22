package com.cesak;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

/**
 * Class defining individual living beings (for now hostile)
 */
public class Entity extends Object
{

    // Obrázky k jednotlivým stavům
    private BufferedImage imgDefault;
    private BufferedImage imgAttack;
    private BufferedImage imgHit;
    private BufferedImage imgDead;
    private BufferedImage[] imgMove;

    private int health;
    private int minDmg;
    private int maxDmg;
    private final double speed; // 12
    private final double attackSpeed;
    private final double attackRange = 0.9;
    private String[] drops;

    public double getAttackRange()
    {
        return attackRange;
    }

    public boolean isDead()
    {
        return health == 0;
    }
    // -----

    public Entity(String img, double x, double y, double size, double yPos, double hitbox, int health, int minDmg, int maxDmg, double speed, double attackSpeed)
    {
        super(img, x, y, size, yPos, hitbox * size);

        this.health = health;
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
        this.speed = speed;
        this.attackSpeed = attackSpeed;
        drops = new String[0];

        try
        {
            imgDefault = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + ".png")));
            imgHit = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + "_hit" + ".png")));
            imgDead = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + "_dead" + ".png")));
            imgMove = new BufferedImage[2];
            for (int i = 0; i < imgMove.length; i++)
            {
                imgMove[i] = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + "_move" + i + ".png")));
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setDrops(String[] strings)
    {
        drops = strings;
    }

    //region Movement

    private int moveImg;

    public void move()
    {
        if (isBeingHit || isAttacking) return;
        if (!isMoving)
        {
            animsOff();
            isMoving = true;
        }

        double x = getX();
        double y = getY();

        double xDif = Player.getInstance().getX() - x;
        double yDif = Player.getInstance().getY() - y;

        double angle = Math.atan(yDif / xDif) * 180 / Math.PI + ((xDif < 0) ? 180 : 0);
/*
        double nextX = Math.round((x + Math.cos(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;
        double nextY = Math.round((y + Math.sin(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;*/

        double nextX = x + Math.cos(angle / 180.0 * Math.PI) * (speed / (600));
        double nextY = y + Math.sin(angle / 180.0 * Math.PI) * (speed / (600));

        setX(nextX);
        setY(nextY);

        if (Collision.hitWall(getX(), getY(), getHitbox()))
        {
            if (getX() != Collision.getNextX()) setX(Collision.getNextX());
            if (getY() != Collision.getNextY()) setY(Collision.getNextY());
        }
        if (Collision.hitObject(getX(), getY(), getHitbox()))
        {
            if (getX() != Collision.getNextX()) setX(Collision.getNextX());
            if (getY() != Collision.getNextY()) setY(Collision.getNextY());
        }
    }

    public boolean isMoving()
    {
        return isMoving;
    }

    public void stopMove()
    {
        frame = 0;
        isMoving = false;
        setMyImage(imgDefault);
    }

    //endregion

    //region Attacking

    public boolean isAttacking()
    {
        return isAttacking;
    }

    public void startAttack()
    {
        if (!isAttacking && !isBeingHit)
        {
            animsOff();
            isAttacking = true;
        }
    }

    private void attack()
    {
        if (distToPlayer() <= attackRange * attackRange)
        {
            int dmg;
            if (minDmg == maxDmg) dmg = minDmg;
            else dmg = new Random().nextInt(maxDmg - minDmg + 1) + minDmg;
            Player.getInstance().getDamage(dmg);
        }
    }

    public void stopAttack()
    {
        frame = 0;
        isAttacking = false;
        setMyImage(imgDefault);
    }

    //endregion

    private boolean isMoving;
    private boolean isAttacking;
    private boolean isBeingHit;

    private int frame;

    public void anim()
    {
        if (isMoving)
        {
            if (frame == 30)
            {
                frame = 0;
                moveImg = (moveImg == 0) ? 1 : 0;
                setMyImage(imgMove[moveImg]);
            }
        }
        else if (isAttacking)
        {
            if (frame == (60 * attackSpeed))
            {
                setMyImage(imgHit);
                attack();
            }
            else if (frame == (60 * attackSpeed) + 25)
            {
                stopAttack();
            }
        }
        else if (isBeingHit)
        {
            if (frame == 12)
            {
                frame = 0;
                isBeingHit = false;
                setMyImage(imgDefault);
            }
        }
        else
        {
            frame = 0;
            return;
        }

        frame++;
    }

    private void animsOff()
    {
        isMoving = false;
        isAttacking = false;
        isBeingHit = false;
    }

    public void getDamage(int dmg)
    {
        setMyImage(imgHit);
        animsOff();
        frame = 0;
        isBeingHit = true;

        health -= dmg;

        if (health <= 0)
        {
            die();
        }
    }

    private void die()
    {
        health = 0;
        setYPos(0);
        setMyImage(imgDead);
        for (String string : drops)
        {
            switch (string)
            {
                case "healingPotion" -> World.getInstance().createPickable(new Pickable("healingPotion", getX(), getY(), 0.5, 0, 0.35, Pickable.Bonus.HEAL));
                case "magicPotion" -> World.getInstance().createPickable(new Pickable("magicPotion", getX(), getY(), 0.5, 0, 0.35, Pickable.Bonus.MANA));
            }
        }
        //World.getInstance().destroyEntity(this);
    }
}
