package cesak.matur;

import cesak.matur.Player;
import cesak.matur.ResFileReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Class which defines every type of object the player can see
 * <br></>
 * It is used as a Superclass for classes defining specific types of objects (Static, Projectile, Enemy, Pickable).
 */
public class SceneObject
{
    // Position in the scene
    private double x, y;
    // Position on the screen
    private double screenX, screenY;
    // Hitbox size
    private double hitbox;
    // Object size in the scene
    private double size;
    // On which tile this object is standing
    private int mapX, mapY;

    // ---

    private BufferedImage myImage;

    public BufferedImage getMyImage()
    {
        return myImage;
    }

    public void setMyImage(BufferedImage img)
    {
        myImage = img;
    }

    // ---

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

    public int[] getTilePos()
    {
        return new int[]{mapX, mapY};
    }

    public double getScreenX()
    {
        return screenX;
    }

    public double getScreenY()
    {
        return screenY;
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
        this.screenX = xPos;
    }

    public void setYPos(double yPos)
    {
        this.screenY = yPos;
    }

    public SceneObject(int id, int x, int y, String path)
    {
        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile(path + id + "/object.txt");

        this.x = x + 0.5;
        this.y = y + 0.5;
        mapX = x;
        mapY = y;

        for (String string : list)
        {
            String[] line = string.replaceAll(" ", "").split(":");

            switch (line[0])
            {
                case "size" -> size = Double.parseDouble(line[1]);
                case "screenY" -> screenY = Double.parseDouble(line[1]);
                case "hitbox" -> hitbox = Double.parseDouble(line[1]);
            }
        }


        try
        {
            myImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path + id + "/def.png")));

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isOnTile(int x, int y)
    {
        return (int) Math.floor(this.x) == x && (int) Math.floor(this.y) == y;
    }

    // Returns distance of this SceneObject from the Player
    public double distToPlayer()
    {
        return (getX() - Player.getInstance().getX()) * (getX() - Player.getInstance().getX()) + (getY() - Player.getInstance().getY()) * (getY() - Player.getInstance().getY());
    }

    // Returns distance to the Player in camera view
    public double distToPlayerTan()
    {
        double angle = (getScreenX() * 90) - 45;
        double pos = Math.abs(Math.cos(angle / 180 * Math.PI));

        if (getScreenX() < 0 || getScreenX() > 1) pos = Math.cos(45.0 / 180 * Math.PI);

        return Math.sqrt(distToPlayer()) * pos;
    }
}
