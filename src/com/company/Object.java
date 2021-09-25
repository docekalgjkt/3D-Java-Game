package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class Object
{
    private int health = 1;

    private double x, y;
    private double speed = 5; // 12

    public double getSpeed()
    {
        return speed;
    }

    public int mapX, mapY;

    public int[] getTilePos()
    {
        return new int[]{mapX, mapY};
    }

    private double xPos;

    public double getXPos()
    {
        return xPos;
    }

    public void setXPos(double xPos)
    {
        this.xPos = xPos;
    }

    private boolean toRender;

    public boolean getToRender()
    {
        return toRender;
    }

    public void setToRender(boolean b)
    {
        toRender = b;
    }

    private BufferedImage img;

    public BufferedImage getImg()
    {
        return img;
    }


    public Object(double x, double y, String img)
    {
        this.x = x;
        this.y = y;
        mapX = (int) Math.floor(x);
        mapY = (int) Math.floor(y);

        try
        {
            this.img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + ".png")));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        speed += new Random().nextDouble() * 2 - 1;
    }


    public boolean isOnTile(int x, int y)
    {
        return (int) Math.floor(this.x) == x && (int) Math.floor(this.y) == y;
    }

    public double[] getPos()
    {
        return new double[]{x, y};
    }


    public void move()
    {

        double xDif = Player.getInstance().getX() - x;
        double yDif = Player.getInstance().getY() - y;

        double angle = Math.atan(yDif / xDif) * 180 / Math.PI + ((xDif < 0) ? 180 : 0);

        double nextX = Math.round((x + Math.cos(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;
        double nextY = Math.round((y + Math.sin(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;

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

        if (!hitWallX)
        {
            x = nextX;
        }
        if (!hitWallY)
        {
            y = nextY;
        }

        if ((int) Math.floor(x) != mapX || (int) Math.floor(y) != mapY)
        {
            World.getInstance().objectMove(mapY, mapX, (int) Math.floor(y), (int) Math.floor(x));
            mapX = (int) Math.floor(x);
            mapY = (int) Math.floor(y);
        }
    }

    public double distToPlayer()
    {
        return (x - Player.getInstance().getX()) * (x - Player.getInstance().getX()) + (y - Player.getInstance().getY()) * (y - Player.getInstance().getY());
    }

    public double distToPlayerTan()
    {
        double angle = (xPos * 90) - 45;
        double pos = Math.abs(Math.cos(angle / 180 * Math.PI));

        if (xPos < 0 || xPos > 1) pos = Math.cos(45.0 / 180 * Math.PI);

        return Math.sqrt(distToPlayer()) * pos;
    }

    public void getDamage(int dmg)
    {
        health -= dmg;
        if (health <= 0)
        {
            World.getInstance().objectDestroy(this);
        }
    }
}
