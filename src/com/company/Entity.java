package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Entity extends Object
{
    private int health = 1;

    private final double speed; // 12

    private BufferedImage imgDefault;
    private BufferedImage imgDead;
    private BufferedImage[] imgMove;

    public double getSpeed()
    {
        return speed;
    }

    public boolean isDead()
    {
        return health == 0;
    }
    // -----

    public Entity(double x, double y, double yPos, double speed, String img)
    {
        super(x, y, yPos, img);

        this.speed = speed;

        try
        {
            imgDefault = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + ".png")));
            imgDead = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + "barrel_destroyed" + ".png")));
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

    public void move()
    {
        if (!isMoving) isMoving = true;

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

        boolean hitWallX = false;
        boolean hitWallY = false;

        double hitBoxRange = 0.5;
        for (int xx = 0; xx < 2; xx++)
        {
            if (World.getInstance().getTile((int) Math.floor(y), (int) Math.floor(x + Math.cos((180.0 * xx) / 180.0 * Math.PI) * hitBoxRange)).equals("#"))
            {
                if (Math.abs(Main.angleDist(angle, (180.0 * xx))) < 90) hitWallX = true;
            }
        }
        for (int yy = 0; yy < 2; yy++)
        {
            if (World.getInstance().getTile((int) Math.floor(y + Math.sin((90 + (180.0 * yy)) / 180.0 * Math.PI) * hitBoxRange), (int) Math.floor(x)).equals("#"))
            {
                if (Math.abs(Main.angleDist(angle, (90 + (180.0 * yy)))) < 90) hitWallY = true;
            }
        }

        for (int i = 0; i < 4; i++)
        {
            if (World.getInstance().getTile((int) Math.floor(y + Math.sin((double) (45 + (i * 90)) / 180.0 * Math.PI) * hitBoxRange), (int) Math.floor(x + Math.cos((double) (45 + (i * 90)) / 180.0 * Math.PI) * hitBoxRange)).equals("#"))
            {
                if (Math.abs(Main.angleDist(angle, 45 + (i * 90))) < 45)
                {
                    hitWallX = true;
                    hitWallY = true;
                }
            }
        }

        if (!hitWallX) x = nextX;
        if (!hitWallY) y = nextY;

        setX(x);
        setY(y);

        moveAnim();
    }

    private boolean isMoving;
    private int moveFrame;
    private int moveImg;

    public boolean isMoving()
    {
        return isMoving;
    }

    private void moveAnim()
    {
        moveFrame++;
        if (moveFrame % 30 == 0)
        {
            moveFrame = 0;
            moveImg = (moveImg == 0) ? 1 : 0;
            setMyImage(imgMove[moveImg]);
        }
    }

    public void stopMove()
    {
        moveFrame = 0;
        isMoving = false;
        setMyImage(imgDefault);
    }

    public void getDamage(int dmg)
    {
        health -= dmg;

        if (health <= 0)
        {
            die();
        }
    }

    private void die()
    {
        health = 0;
        setMyImage(imgDead);
    }
}
