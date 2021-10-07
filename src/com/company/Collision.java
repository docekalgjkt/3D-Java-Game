package com.company;

import java.util.ArrayList;
import java.util.List;

public class Collision
{
    private static double nextX, nextY;

    public static double getNextX()
    {
        return nextX;
    }

    public static double getNextY()
    {
        return nextY;
    }

    public static List<Pickable> hitPickables;


    public static boolean hitWall(double x, double y, double hitbox)
    {
        boolean result = false;

        // r = positive, l = negative
        double xr = Math.floor(x + 1) - x;
        double xl = x - Math.floor(x);

        double yr = Math.floor(y + 1) - y;
        double yl = y - Math.floor(y);

        nextX = x;
        nextY = y;

        if (xr < hitbox)
        {
            boolean onlyCorner = true;

            if (!World.getInstance().getTile((int) Math.floor(y), (int) Math.floor(x) + 1).equals("."))
            {
                nextX -= hitbox - xr;
                onlyCorner = false;
                result = true;
            }
            if (yr < hitbox && !World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x)).equals("."))
            {
                nextY -= hitbox - yr;
                onlyCorner = false;
                result = true;
            }
            else if (yl < hitbox && !World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x)).equals("."))
            {
                nextY += hitbox - yl;
                onlyCorner = false;
                result = true;
            }

            if (onlyCorner)
            {
                if (yr < hitbox && !World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x) + 1).equals("."))
                {
                    if (xr > yr)
                    {
                        nextX -= hitbox - xr;
                    }
                    else if (xr < yr)
                    {
                        nextY -= hitbox - yr;
                    }
                    else
                    {
                        nextX -= hitbox - xr;
                        nextY -= hitbox - yr;
                    }
                    result = true;
                }
                else if (yl < hitbox && !World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x) + 1).equals("."))
                {
                    if (xr > yl)
                    {
                        nextX -= hitbox - xr;
                    }
                    else if (xr < yl)
                    {
                        nextY += hitbox - yl;
                    }
                    else
                    {
                        nextX -= hitbox - xr;
                        nextY += hitbox - yl;
                    }
                    result = true;
                }
            }
        }
        else if (xl < hitbox)
        {
            boolean onlyCorner = true;

            if (!World.getInstance().getTile((int) Math.floor(y), (int) Math.floor(x) - 1).equals("."))
            {
                nextX += hitbox - xl;
                onlyCorner = false;
                result = true;
            }
            if (yr < hitbox && !World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x)).equals("."))
            {
                nextY -= hitbox - yr;
                onlyCorner = false;
                result = true;
            }
            else if (yl < hitbox && !World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x)).equals("."))
            {
                nextY += hitbox - yl;
                onlyCorner = false;
                result = true;
            }

            if (onlyCorner)
            {
                if (yr < hitbox && !World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x) - 1).equals("."))
                {
                    if (xl > yr)
                    {
                        nextX += hitbox - xl;
                    }
                    else if (xl < yr)
                    {
                        nextY -= hitbox - yr;
                    }
                    else
                    {
                        nextX += hitbox - xl;
                        nextY -= hitbox - yr;
                    }
                    result = true;
                }
                else if (yl < hitbox && !World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x) - 1).equals("."))
                {
                    if (xl > yl)
                    {
                        nextX += hitbox - xl;
                    }
                    else if (xl < yl)
                    {
                        nextY += hitbox - yl;
                    }
                    else
                    {
                        nextX += hitbox - xl;
                        nextY += hitbox - yl;
                    }
                    result = true;
                }
            }
        }
        else
        {
            if (yr < hitbox && !World.getInstance().getTile((int) Math.floor(y) + 1, (int) Math.floor(x)).equals("."))
            {
                nextY -= hitbox - yr;
                result = true;
            }
            if (yl < hitbox && !World.getInstance().getTile((int) Math.floor(y) - 1, (int) Math.floor(x)).equals("."))
            {
                nextY += hitbox - yl;
                result = true;
            }
        }

        return result;
    }

    public static boolean hitObject(double x, double y, double hitbox)
    {
        List<StaticObject> objects = World.getInstance().getStaticObjects();

        for (StaticObject object : objects)
        {
            if (object.isDestroyed()) continue;

            double difX = x - object.getX();
            double difY = y - object.getY();

            double dist = difX * difX +
                    difY * difY;

            double hitDist = (hitbox + object.getHitbox()) * (hitbox + object.getHitbox());

            if (dist < hitDist)
            {
                double sqrtDist = Math.sqrt(dist);
                double triggerDist = Math.abs(Math.sqrt(hitDist) - sqrtDist);

                nextX = x + (difX / sqrtDist) * triggerDist;
                nextY = y + (difY / sqrtDist) * triggerDist;

                return true;
            }
        }

        return false;
    }

    public static boolean hitPickable(double x, double y, double hitbox)
    {
        boolean result = false;

        List<Pickable> pickables = World.getInstance().getPickables();
        hitPickables = new ArrayList<>();

        for (Pickable pickable : pickables)
        {
            double difX = x - pickable.getX();
            double difY = y - pickable.getY();

            double dist = difX * difX +
                    difY * difY;

            double hitDist = (hitbox + pickable.getHitbox()) * (hitbox + pickable.getHitbox());

            if (dist < hitDist)
            {
                hitPickables.add(pickable);
                if (!result) result = true;
            }
        }

        return result;
    }
}
