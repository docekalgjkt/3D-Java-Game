package com.cesak;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to get values which is then a picture rendered from
 */
public class Renderer
{

    // region Singleton

    private static Renderer renderer = null;

    public static Renderer getInstance()
    {
        if (renderer == null)
        {
            renderer = new Renderer();
        }
        return renderer;
    }

    // endregion

    public void render(int screenWidth)
    {
        walls = new double[screenWidth];
        texs = new double[screenWidth];
        what = new String[screenWidth];

        double fov = 90;

        double screenHalf = Math.tan((fov / 2) / 180 * Math.PI);
        double seg = screenHalf / (screenWidth / 2.0);

        // Wall rendering
        for (int x = 0; x < screenWidth; x++)
        {
            // Calculating ray angle
            double a = Math.atan(-Math.tan((fov / 2) / 180 * Math.PI) + (seg * x)) * 180 / Math.PI;
            double rayAngle = Player.getInstance().getAngle() + a;

            rayAngle = rayAngle / 180 * Math.PI;

            // Origin of the ray
            double posX = Player.getInstance().getX();
            double posY = Player.getInstance().getY();

            // Steps that ray will take (-1, 0, 1)
            byte stepX = (Math.cos(rayAngle) != 0) ? (byte) (Math.cos(rayAngle) / Math.abs(Math.cos(rayAngle))) : 0;
            byte stepY = (Math.sin(rayAngle) != 0) ? (byte) (Math.sin(rayAngle) / Math.abs(Math.sin(rayAngle))) : 0;

            // X, Y distances from origin to current pos of the ray (0 - 1)
            double X = (stepX != 0)
                    ? ((stepX > 0) ? Math.floor(posX + 1) - posX : Math.floor(posX) - posX)
                    : 0;
            double Y = (stepY != 0)
                    ? ((stepY > 0) ? Math.floor(posY + 1) - posY : Math.floor(posY) - posY)
                    : 0;

            // Current position of the ray
            double curX = posX;
            double curY = posY;

            // Distances from origin to ray current position
            double distX = Math.abs(X / Math.cos(rayAngle));
            double distY = Math.abs(Y / Math.sin(rayAngle));

            // Final calculated distance
            double dist = 0;

            boolean hit = false;

            what[x] = "#";

            // Ray cycle
            while (!hit && (distX <= Player.getInstance().getCamDistance() || distY <= Player.getInstance().getCamDistance()))
            {
                dist = Math.min(distX, distY);

                if (distX < distY)
                {
                    curX = posX + X;
                    curY = posY + (Math.abs(dist * Math.sin(rayAngle)) * stepY);

                    // Ray hits edge of the map
                    if (curY < 0 || curX < 0 || curY >= World.getInstance().getMap().length || curX >= World.getInstance().getMap()[(int) Math.floor(curY)].length())
                    {
                        hit = true;
                        what[x] = "#";
                        break;
                    }
                    // Checks if the ray hit a wall
                    else if (!World.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX) + ((stepX < 0) ? stepX : 0)).equals("."))
                    {
                        hit = true;
                        what[x] = World.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX) + ((stepX < 0) ? stepX : 0));
                    }
                    // Ray didn't hit a wall, so we send him to next X pos :D
                    {
                        X += stepX;

                        distX = Math.abs(X / Math.cos(rayAngle));
                    }
                }
                else if (distY < distX)
                {
                    curY = posY + Y;
                    curX = posX + (Math.abs(dist * Math.cos(rayAngle)) * stepX);

                    // Ray hits edge of the map
                    if (curY < 0 || curX < 0 || curY >= World.getInstance().getMap().length || curX >= World.getInstance().getMap()[(int) Math.floor(curY)].length())
                    {
                        hit = true;
                        what[x] = "#";
                    }
                    // Checks if the ray hit a wall
                    else if (!World.getInstance().getTile((int) Math.floor(curY) + ((stepY < 0) ? stepY : 0), (int) Math.floor(curX)).equals("."))
                    {
                        hit = true;
                        what[x] = World.getInstance().getTile((int) Math.floor(curY) + ((stepY < 0) ? stepY : 0), (int) Math.floor(curX));
                    }
                    // Ray didn't hit a wall, so we send him to next Y pos :D
                    else
                    {
                        Y += stepY;

                        distY = Math.abs(Y / Math.sin(rayAngle));
                    }
                }
                else
                {
                    curX = posX + X;
                    curY = posY + Y;

                    // Ray hits edge of the map
                    if (curY < 0 || curX < 0 || curY >= World.getInstance().getMap().length || curX >= World.getInstance().getMap()[(int) Math.floor(curY)].length())
                    {
                        hit = true;
                        what[x] = "#";
                    }
                    else if (!World.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX)).equals("."))
                    {
                        hit = true;
                        what[x] = World.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX));
                    }
                    else if (!World.getInstance().getTile((int) Math.floor(curY) - 1, (int) Math.floor(curX)).equals("."))
                    {
                        hit = true;
                        what[x] = World.getInstance().getTile((int) Math.floor(curY) - 1, (int) Math.floor(curX));
                    }
                    else if (!World.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX) - 1).equals("."))
                    {
                        hit = true;
                        what[x] = World.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX) - 1);
                    }
                    else if (!World.getInstance().getTile((int) Math.floor(curY) - 1, (int) Math.floor(curX) - 1).equals("."))
                    {
                        hit = true;
                        what[x] = World.getInstance().getTile((int) Math.floor(curY) - 1, (int) Math.floor(curX) - 1);
                    }
                    else
                    {
                        X += stepX;
                        Y += stepY;

                        distX = Math.abs(X / Math.cos(rayAngle));
                        distY = Math.abs(Y / Math.sin(rayAngle));
                    }
                }
            }

            if (hit)
            {
                // Calculating distance of a wall
                double wallDistance = Math.cos((rayAngle - (Player.getInstance().getAngle() / 180 * Math.PI))) * dist;
                walls[x] = wallDistance;

                // Getting texture of the wall
                double col = 0.0;

                if (curX % 1 == 0)
                {
                    if (stepX > 0)
                    {
                        col = curY - Math.floor(curY);
                    }
                    else if (stepX < 0)
                    {
                        col = 1 - (curY - Math.floor(curY));
                    }
                }
                else
                {
                    if (stepY > 0)
                    {
                        col = 1 - (curX - Math.floor(curX));
                    }
                    else if (stepY < 0)
                    {
                        col = curX - Math.floor(curX);
                    }
                }

                texs[x] = Math.abs(col);
            }
            else
            {
                walls[x] = Player.getInstance().getCamDistance();
                what[x] = "#";
                texs[x] = 0.0;
            }
        }

        // Object rendering
        List<Object> objects = new ArrayList<>();

        objects.addAll(World.getInstance().getEntities());
        objects.addAll(World.getInstance().getStaticObjects());
        objects.addAll(World.getInstance().getProjectiles());
        objects.addAll(World.getInstance().getPickables());

        for (Object object : objects)
        {
            object.setRenderd(false);

            double[] opos = new double[2];
            opos[0] = object.getX() - Player.getInstance().getX();
            opos[1] = object.getY() - Player.getInstance().getY();

            double angle = ((opos[0] == 0)
                    ? ((opos[1] > 0) ? 90 : 270)
                    : (Math.atan(opos[1] / opos[0])) * 180 / Math.PI) + ((opos[0] < 0) ? 180 : 0);
            double angleDif = MyMath.angleDist(angle, Player.getInstance().getAngle());

            if (Math.abs(angleDif) >= fov || object.distToPlayer() > Player.getInstance().getCamDistance() * Player.getInstance().getCamDistance())
                continue;

            double xPos = (Math.tan(angleDif / 180 * Math.PI) + screenHalf) / (screenHalf * 2);

            object.setXPos(xPos);
            object.setRenderd(true);
        }
    }

    private double[] walls;
    private double[] texs;
    private String[] what;

    public double[] getWalls()
    {
        return walls;
    }

    public double[] getTexs()
    {
        return texs;
    }

    public String[] getWhat()
    {
        return what;
    }
}