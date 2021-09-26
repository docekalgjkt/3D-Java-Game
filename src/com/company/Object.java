package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Object
{
    private int health = 1;

    private double x, y;
    private double speed; // 12

    private double angle;

    public int mapX, mapY;

    private double xPos;
    private double yPos;

    private boolean renderd;
    private boolean pickable;
    private boolean destroyable;
    private boolean alive;
    private boolean lit;

    private BufferedImage myImage;

    private BufferedImage imgDefault;
    private BufferedImage imgDestroyed;
    private BufferedImage imgDamaged;
    private BufferedImage[] imgMove;

    public double[] getPos()
    {
        return new double[]{x, y};
    }

    public int[] getTilePos()
    {
        return new int[]{mapX, mapY};
    }

    public double getSpeed()
    {
        return speed;
    }

    public double getXPos()
    {
        return xPos;
    }

    public void setXPos(double xPos)
    {
        this.xPos = xPos;
    }

    public double getYPos()
    {
        return yPos;
    }


    public boolean isRenderd()
    {
        return renderd;
    }

    public boolean isDestroyable()
    {
        return destroyable;
    }

    public boolean isPickable()
    {
        return pickable;
    }

    public boolean isDestroyed()
    {
        return health == 0;
    }

    public boolean isAlive()
    {
        return alive;
    }

    public boolean isLit()
    {
        return lit;
    }


    public BufferedImage getMyImage()
    {
        return myImage;
    }


    public void setRenderd(boolean b)
    {
        renderd = b;
    }

    public void setLit(boolean lit)
    {
        this.lit = lit;
    }

    public void setAngle(double a)
    {
        angle = a;
    }

    // -----

    public Object(double x, double y, double yPos, double speed, boolean dest, boolean pick, boolean alive, String img)
    {
        this.x = x;
        this.y = y;
        mapX = (int) Math.floor(x);
        mapY = (int) Math.floor(y);

        this.yPos = yPos;
        destroyable = dest;
        pickable = pick;
        this.alive = alive;

        try
        {
            imgDefault = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + ".png")));
            imgDestroyed = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + "barrel_destroyed" + ".png")));
            if (alive)
            {
                imgMove = new BufferedImage[2];
                for (int i = 0; i < imgMove.length; i++)
                {
                    imgMove[i] = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + "_move" + i + ".png")));
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        this.speed = speed;
        myImage = imgDefault;
    }


    public boolean isOnTile(int x, int y)
    {
        return (int) Math.floor(this.x) == x && (int) Math.floor(this.y) == y;
    }

    public void walk()
    {
        double xDif = Player.getInstance().getX() - x;
        double yDif = Player.getInstance().getY() - y;

        angle = Math.atan(yDif / xDif) * 180 / Math.PI + ((xDif < 0) ? 180 : 0);
/*
        double nextX = Math.round((x + Math.cos(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;
        double nextY = Math.round((y + Math.sin(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;*/

        double nextX = x + Math.cos(angle / 180.0 * Math.PI) * (speed / (600));
        double nextY = y + Math.cos(angle / 180.0 * Math.PI) * (speed / (600));

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
    }

    public void move()
    {
        double nextX = x + Math.cos(angle / 180.0 * Math.PI) * (speed / (600));
        double nextY = y + Math.sin(angle / 180.0 * Math.PI) * (speed / (600));

        x = nextX;
        y = nextY;

        if (World.getInstance().getTile((int) y, (int) x).equals("#"))
        {
            World.getInstance().destroyObject(this);
        }

        List<Object> objects = World.getInstance().getObjects();
        for (Object o : objects)
        {
            if (!o.isDestroyable() || o.isDestroyed()) continue;

            double dist = (x - o.getPos()[0]) * (x - o.getPos()[0]) + (y - o.getPos()[1]) * (y - o.getPos()[1]);
            if (dist <= 0.4 * 0.4)
            {
                o.getDamage(1);
                World.getInstance().destroyObject(this);
                break;
            }
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
            getDestroyed();
        }
    }

    private void getDestroyed()
    {
        health = 0;
        myImage = imgDestroyed;
    }
}
