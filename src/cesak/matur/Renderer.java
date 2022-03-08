package cesak.matur;

import com.cesak.MyMath;
import com.cesak.Object;
import cesak.matur.LevelManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which calculates where and what kind of tiles/blocks/walls the player sees
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

    /**
     * Array containing wall distances from the player for each ray cast.
     * <br></br>
     * Length of the array is based on number of rays cast.
     */
    private double[] walls;
    /**
     * When a wall is hit by a ray,
     * it is calculated in which spot the ray has hit the wall.
     * <br></br>
     * This Array contains a value from 0.0 to 1.0,
     * which corresponds to a spot's x-coordinate.
     * <br></br>
     * This value is then used to get a certain column of the texture,
     * that will be drawn on the wall.
     * <br></br>
     * Length of the array is based on number of rays cast.
     */
    private double[] texs;
    /**
     * Array containing the symbol of the tile/block, that has been hit.
     * <br></br>
     * Length of the array is based on number of rays cast.
     */
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
            double a = Math.atan(-screenHalf + (seg * x)) * 180 / Math.PI;
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
                    if (curY < 0 || curX < 0 || curY >= LevelManager.getInstance().getMap().length || curX >= LevelManager.getInstance().getMap()[(int) Math.floor(curY)].length())
                    {
                        hit = true;
                        what[x] = "#";
                        break;
                    }
                    // Checks if the ray hit a wall
                    else if (!LevelManager.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX) + ((stepX < 0) ? stepX : 0)).equals("."))
                    {
                        hit = true;
                        what[x] = LevelManager.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX) + ((stepX < 0) ? stepX : 0));
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
                    if (curY < 0 || curX < 0 || curY >= LevelManager.getInstance().getMap().length || curX >= LevelManager.getInstance().getMap()[(int) Math.floor(curY)].length())
                    {
                        hit = true;
                        what[x] = "#";
                    }
                    // Checks if the ray hit a wall
                    else if (!LevelManager.getInstance().getTile((int) Math.floor(curY) + ((stepY < 0) ? stepY : 0), (int) Math.floor(curX)).equals("."))
                    {
                        hit = true;
                        what[x] = LevelManager.getInstance().getTile((int) Math.floor(curY) + ((stepY < 0) ? stepY : 0), (int) Math.floor(curX));
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
                    if (curY < 0 || curX < 0 || curY >= LevelManager.getInstance().getMap().length || curX >= LevelManager.getInstance().getMap()[(int) Math.floor(curY)].length())
                    {
                        hit = true;
                        what[x] = "#";
                    }
                    else if (!LevelManager.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX)).equals("."))
                    {
                        hit = true;
                        what[x] = LevelManager.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX));
                    }
                    else if (!LevelManager.getInstance().getTile((int) Math.floor(curY) - 1, (int) Math.floor(curX)).equals("."))
                    {
                        hit = true;
                        what[x] = LevelManager.getInstance().getTile((int) Math.floor(curY) - 1, (int) Math.floor(curX));
                    }
                    else if (!LevelManager.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX) - 1).equals("."))
                    {
                        hit = true;
                        what[x] = LevelManager.getInstance().getTile((int) Math.floor(curY), (int) Math.floor(curX) - 1);
                    }
                    else if (!LevelManager.getInstance().getTile((int) Math.floor(curY) - 1, (int) Math.floor(curX) - 1).equals("."))
                    {
                        hit = true;
                        what[x] = LevelManager.getInstance().getTile((int) Math.floor(curY) - 1, (int) Math.floor(curX) - 1);
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

        objects.addAll(LevelManager.getInstance().getEntities());
        objects.addAll(LevelManager.getInstance().getStaticObjects());
        objects.addAll(LevelManager.getInstance().getProjectiles());
        objects.addAll(LevelManager.getInstance().getPickables());

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

    // --- Rendering Extended to 3rd Dimension ---

    private String[][] textureHit;
    private double[][][] hitPoint;

    public String[][] getTextureHit()
    {
        return textureHit;
    }

    public double[][][] getHitPoint()
    {
        return hitPoint;
    }

    public void render3D(double posX, double posY, double posZ, double xAngle, double yAngle, double max, int xPixels, int yPixels)
    {
        textureHit = new String[yPixels][xPixels];
        hitPoint = new double[yPixels][xPixels][2];

        // Wall rendering
        for (int y = 0; y < yPixels; y++)
        {
            for (int x = 0; x < xPixels; x++)
            {
                double xInPlane = ((double) x / xPixels) * 2 - 1;
                double yInPlane = ((double) y / yPixels) * 2 - 1;

                double xSideA = 1.0 / Math.cos(Math.atan(yInPlane));
                double ySideA = 1.0 / Math.cos(Math.atan(xInPlane));

                double xAngleOffset = Math.atan(xInPlane) * 180 / Math.PI;
                double yAngleOffset = Math.atan(yInPlane / ySideA) * 180 / Math.PI;

                double xRayAngle = (xAngle + xAngleOffset) / 180 * Math.PI;
                double yRayAngle = (yAngle + yAngleOffset) / 180 * Math.PI;

                // Steps that ray will take to reach next block (block = one square on map) (VALUES: -1, 0, 1)
                byte stepX = (Math.cos(xRayAngle) != 0) ? (byte) (Math.cos(xRayAngle) / Math.abs(Math.cos(xRayAngle))) : 0;
                byte stepY = (Math.sin(xRayAngle) != 0) ? (byte) (Math.sin(xRayAngle) / Math.abs(Math.sin(xRayAngle))) : 0;
                byte stepZ = (Math.sin(yRayAngle) != 0) ? (byte) (Math.sin(yRayAngle) / Math.abs(Math.sin(yRayAngle))) : 0;

                // Distances from origin to current pos of the ray
                double X = (stepX != 0)
                        ? ((stepX > 0) ? Math.floor(posX + 1) - posX : Math.floor(posX) - posX)
                        : 0;
                double Y = (stepY != 0)
                        ? ((stepY > 0) ? Math.floor(posY + 1) - posY : Math.floor(posY) - posY)
                        : 0;
                double Z = (stepZ != 0)
                        ? ((stepZ > 0) ? Math.floor(posZ + 1) - posZ : Math.floor(posZ) - posZ)
                        : 0;

                // Current position of the ray
                double curX = posX;
                double curY = posY;
                double curZ = posZ;

                // Distances from origin
                double distX = Math.abs(X / Math.cos(xRayAngle));
                double distY = Math.abs(Y / Math.sin(xRayAngle));
                double distZ = Math.abs(Z / Math.sin(yRayAngle));

                // Final calculated distance
                double distance;

                // Did this ray hit something?
                boolean hit = false;

                textureHit[y][x] = "1";

                // Ray-casting Iteration
                while (!hit && (distX <= max || distY <= max || distZ <= max))
                {
                    double dist = Math.min(distX, distY);
                    distance = Math.min(dist, distZ);

                    if (distX < distY && distX < distZ)
                    {
                        curX = posX + X;
                        curY = posY + (Math.abs(distance * Math.sin(xRayAngle)) * stepY);
                        curZ = posZ + (Math.abs(distance * Math.sin(yRayAngle)) * stepZ);

                        // Ray hits the edge of the map
                        if (curX < 0 || curY < 0 || curZ < 0 || curX >= LevelManager.getInstance().getMap3D()[(int) Math.floor(curZ)][(int) Math.floor(curY)].length() || curY >= LevelManager.getInstance().getMap3D()[(int) Math.floor(curZ)].length || curZ >= LevelManager.getInstance().getMap3D().length)
                        {
                            hit = true;
                            textureHit[y][x] = "1";
                            break;
                        }
                        // Checks if the ray hit a wall
                        else if (!LevelManager.getInstance().getTile3D((int) Math.floor(curX) + ((stepX < 0) ? stepX : 0), (int) Math.floor(curY), (int) Math.floor(curZ)).equals("."))
                        {
                            hit = true;
                            textureHit[y][x] = LevelManager.getInstance().getTile3D((int) Math.floor(curX) + ((stepX < 0) ? stepX : 0), (int) Math.floor(curY), (int) Math.floor(curZ));
                        }
                        // Ray did not hit a wall, so we send him to next X pos
                        else
                        {
                            X += stepX;
                            distX = Math.abs(X / Math.cos(xRayAngle));
                        }
                    }
                    else if (distY < distX && distY < distZ)
                    {
                        curX = posX + (Math.abs(distance * Math.cos(xRayAngle)) * stepX);
                        curY = posY + Y;
                        curZ = posZ + (Math.abs(distance * Math.sin(yRayAngle)) * stepZ);

                        // Ray hits the edge of the map
                        if (curX < 0 || curY < 0 || curZ < 0 || curX >= LevelManager.getInstance().getMap3D()[(int) Math.floor(curZ)][(int) Math.floor(curY)].length() || curY >= LevelManager.getInstance().getMap3D()[(int) Math.floor(curZ)].length || curZ >= LevelManager.getInstance().getMap3D().length)
                        {
                            hit = true;
                            textureHit[y][x] = "1";
                            break;
                        }
                        // Checks if the ray hit a wall
                        else if (!LevelManager.getInstance().getTile3D((int) Math.floor(curX), (int) Math.floor(curY) + ((stepY < 0) ? stepY : 0), (int) Math.floor(curZ)).equals("."))
                        {
                            hit = true;
                            textureHit[y][x] = LevelManager.getInstance().getTile3D((int) Math.floor(curX), (int) Math.floor(curY) + ((stepY < 0) ? stepY : 0), (int) Math.floor(curZ));
                        }
                        // Ray did not hit a wall, so we send him to next X pos
                        else
                        {
                            Y += stepY;
                            distY = Math.abs(Y / Math.sin(xRayAngle));
                        }
                    }
                    else if (distZ < distX && distZ < distY)
                    {
                        curX = posX + (Math.abs(distance * Math.cos(xRayAngle)) * stepX);
                        curY = posY + (Math.abs(distance * Math.sin(xRayAngle)) * stepY);
                        curZ = posZ + Z;

                        // Ray hits the edge of the map
                        if (curX < 0 || curY < 0 || curZ < 0 || curX >= LevelManager.getInstance().getMap3D()[(int) Math.floor(curZ)][(int) Math.floor(curY)].length() || curY >= LevelManager.getInstance().getMap3D()[(int) Math.floor(curZ)].length || curZ >= LevelManager.getInstance().getMap3D().length)
                        {
                            hit = true;
                            textureHit[y][x] = "1";
                            break;
                        }
                        // Checks if the ray hit a wall
                        else if (!LevelManager.getInstance().getTile3D((int) Math.floor(curX), (int) Math.floor(curY), (int) Math.floor(curZ) + ((stepZ < 0) ? stepZ : 0)).equals("."))
                        {
                            hit = true;
                            textureHit[y][x] = LevelManager.getInstance().getTile3D((int) Math.floor(curX), (int) Math.floor(curY), (int) Math.floor(curZ) + ((stepZ < 0) ? stepZ : 0));
                        }
                        // Ray did not hit a wall, so we send him to next X pos
                        else
                        {
                            Z += stepZ;
                            distZ = Math.abs(Z / Math.sin(yRayAngle));
                        }
                    }
                    else
                    {
                        hit = true;
                    }
                }

                if (hit)
                {
                    // Getting texture of the wall
                    double col = 0.0;
                    double row = 0.0;

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
                        row = curZ - Math.floor(curZ);
                    }
                    else if (curY % 1 == 0)
                    {
                        if (stepY > 0)
                        {
                            col = 1 - (curX - Math.floor(curX));
                        }
                        else if (stepY < 0)
                        {
                            col = curX - Math.floor(curX);
                        }
                        row = curZ - Math.floor(curZ);
                    }
                    else if (curZ % 1 == 0)
                    {
                        if (stepZ > 0)
                        {
                            col = curX - Math.floor(curX);
                        }
                        else if (stepZ < 0)
                        {
                            col = 1 - (curX - Math.floor(curX));
                        }
                        row = curY - Math.floor(curY);
                    }

                    hitPoint[y][x][0] = row;
                    hitPoint[y][x][1] = col;
                }
                else
                {
                    // Not hitting anything
                }
            }
        }
    }
}