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


    private double x = 1.5;
    private double y = 1.5;
    private double angle = 0;
    private double nearClip = 0.3;

    private double hitbox = 0.2;

    private boolean sprinting;
    private boolean sneaking;

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
        //double nextX = Math.round((x + Math.cos((angle + a) / 180.0 * Math.PI) * (speed * modSpeed() / 1000.0)) * 1000) / 1000.0;
        //double nextY = Math.round((y + Math.sin((angle + a) / 180.0 * Math.PI) * (speed * modSpeed() / 1000.0)) * 1000) / 1000.0;
        double nextY = y + Math.sin((angle + a) / 180.0 * Math.PI) * (speed * modSpeed() / (600));


        x = nextX;
        y = nextY;

        // Box Hitbox Calculation

        double xr = Math.floor(x + 1) - x;
        double xl = x - Math.floor(x);

        double yr = Math.floor(y + 1) - y;
        double yl = y - Math.floor(y);

        if (xr < hitbox)
        {
            boolean onlyCorner = true;

            if (World.getInstance().getTile((int) Math.floor(y), (int) Math.floor(x) + 1).equals("#"))
            {
                x -= hitbox - xr;
                onlyCorner = false;
            }
            if (yr < hitbox && World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x)).equals("#"))
            {
                y -= hitbox - yr;
                onlyCorner = false;
            }
            else if (yl < hitbox && World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x)).equals("#"))
            {
                y += hitbox - yl;
                onlyCorner = false;
            }

            if (onlyCorner)
            {
                if (yr < hitbox && World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x) + 1).equals("#"))
                {
                    if (xr > yr)
                    {
                        x -= hitbox - xr;
                    }
                    else if (xr < yr)
                    {
                        y -= hitbox - yr;
                    }
                    else
                    {
                        x -= hitbox - xr;
                        y -= hitbox - yr;
                    }
                }
                else if (yl < hitbox && World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x) + 1).equals("#"))
                {
                    if (xr > yl)
                    {
                        x -= hitbox - xr;
                    }
                    else if (xr < yl)
                    {
                        y += hitbox - yl;
                    }
                    else
                    {
                        x -= hitbox - xr;
                        y += hitbox - yl;
                    }
                }
            }
        }
        else if (xl < hitbox)
        {
            boolean onlyCorner = true;

            if (World.getInstance().getTile((int) Math.floor(y), (int) Math.floor(x) - 1).equals("#"))
            {
                x += hitbox - xl;
                onlyCorner = false;
            }
            if (yr < hitbox && World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x)).equals("#"))
            {
                y -= hitbox - yr;
                onlyCorner = false;
            }
            else if (yl < hitbox && World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x)).equals("#"))
            {
                y += hitbox - yl;
                onlyCorner = false;
            }

            if (onlyCorner)
            {
                if (yr < hitbox && World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x) - 1).equals("#"))
                {
                    if (xl > yr)
                    {
                        x += hitbox - xl;
                    }
                    else if (xl < yr)
                    {
                        y -= hitbox - yr;
                    }
                    else
                    {
                        x += hitbox - xl;
                        y -= hitbox - yr;
                    }
                }
                else if (yl < hitbox && World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x) - 1).equals("#"))
                {
                    if (xl > yl)
                    {
                        x += hitbox - xl;
                    }
                    else if (xl < yl)
                    {
                        y += hitbox - yl;
                    }
                    else
                    {
                        x += hitbox - xl;
                        y += hitbox - yl;
                    }
                }
            }
        }
        else
        {
            if (yr < hitbox && World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x)).equals("#"))
            {
                y -= hitbox - yr;
            }
            if (yl < hitbox && World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x)).equals("#"))
            {
                y += hitbox - yl;
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
}
