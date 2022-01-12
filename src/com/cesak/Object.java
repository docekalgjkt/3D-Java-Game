package com.cesak;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Object
{
    private double x, y;
    private double xPos, yPos;
    private double hitbox;
    private double size;
    private int mapX, mapY;
    private boolean rendered;
    private boolean lit;


    private BufferedImage myImage;

    public BufferedImage getMyImage()
    {
        return myImage;
    }

    public void setMyImage(BufferedImage img)
    {
        myImage = img;
    }


    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getSize()
    {
        return size;
    }

    public boolean isLit()
    {
        return lit;
    }

    public boolean isRendered()
    {
        return rendered;
    }

    public int[] getTilePos()
    {
        return new int[]{mapX, mapY};
    }

    public double getXPos()
    {
        return xPos;
    }

    public double getYPos()
    {
        return yPos;
    }

    public double getHitbox()
    {
        return hitbox;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public void setXPos(double xPos)
    {
        this.xPos = xPos;
    }

    public void setYPos(double yPos)
    {
        this.yPos = yPos;
    }

    public void setLit(boolean lit)
    {
        this.lit = lit;
    }

    public void setRenderd(boolean b)
    {
        rendered = b;
    }


    public Object(String img, double x, double y, double size, double yPos, double hitbox)
    {
        this.x = x;
        this.y = y;

        this.size = size;

        this.hitbox = hitbox;

        mapX = (int) Math.floor(x);
        mapY = (int) Math.floor(y);

        this.yPos = yPos;

        try
        {
            myImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + ".png")));

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isOnTile(int x, int y)
    {
        return (int) Math.floor(this.x) == x && (int) Math.floor(this.y) == y;
    }

    public double distToPlayer()
    {
        return (getX() - Player.getInstance().getX()) * (getX() - Player.getInstance().getX()) + (getY() - Player.getInstance().getY()) * (getY() - Player.getInstance().getY());
    }

    public double distToPlayerTan()
    {
        double angle = (getXPos() * 90) - 45;
        double pos = Math.abs(Math.cos(angle / 180 * Math.PI));

        if (getXPos() < 0 || getXPos() > 1) pos = Math.cos(45.0 / 180 * Math.PI);

        return Math.sqrt(distToPlayer()) * pos;
    }
}
