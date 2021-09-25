package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class World
{

    // region Singleton

    private static World world = null;

    public static World getInstance()
    {
        if (world == null)
        {
            world = new World();
        }
        return world;
    }

    // endregion

    private final String[] wallTexs = new String[]{
            "DungeonWall0"
    };

    private final String[] doorTexs = new String[]{
            "DungeonWall0"
    };

    private BufferedImage wTex;

    private final BufferedImage[] dTexs = new BufferedImage[1];

    public BufferedImage getTex(String s)
    {
        return switch (s)
                {
                    case "#" -> wTex;
                    case "+" -> dTexs[0];
                    default -> null;
                };

    }

    //    -Y
    // -X [ ] X
    //     Y
    private final String[] map = new String[]{
            //    5    10   15
            "#########",        // 0
            "#.......#",
            "#.......#####",
            "#.......#..o#",
            "#...#..o....#",
            "#.......#...#",    // 5
            "#.......###.#",
            "#o....oo#.#.#",
            "##.########.#",
            "#....#o.....#",
            "#....#...####",    // 10
            "#....#..o#",
            "#......###",
            "########",
            //    5    10   15
    };

    public String[] getMap()
    {
        return map;
    }

    public String getTile(int y, int x)
    {
        return (y < 0 || y >= map.length || x < 0 || x >= map[y].length()) ? "#" : String.valueOf(map[y].toCharArray()[x]);
    }

    public void setTile(int y, int x, String what)
    {
        StringBuilder sb = new StringBuilder(map[y]);
        sb.replace(x, x + 1, what);
        map[y] = sb.toString();
    }

    void setUp()
    {
        for (int i = 0; i < objects.size(); i++)
        {
            Object c = objects.get(i);
            if (!getTile(c.getTilePos()[1], c.getTilePos()[0]).equals("o") && getTile(c.getTilePos()[1], c.getTilePos()[0]).equals("."))
            {
                setTile(c.getTilePos()[1], c.getTilePos()[0], "o");
            }
        }

        try
        {
            wTex = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + wallTexs[0] + ".png")));
            dTexs[0] = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + doorTexs[0] + ".png")));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private final List<Object> objects = new ArrayList<>()
    {
        {
            new Object(7.5, 4.5, "Beholder");

            new Object(1.5, 7.5, "Barrel");
            new Object(6.5, 7.5, "Barrel");
            new Object(7.5, 7.5, "Barrel");
        }
    };

    public List<Object> getObjects()
    {
        return objects;
    }

    public void objectMove(int y0, int x0, int y1, int x1)
    {
        setTile(y1, x1, "c");
        boolean canRemove = true;
        for (Object object : objects)
        {
            if (object.isOnTile(x0, y0))
            {
                canRemove = false;
                break;
            }
        }
        if (canRemove) setTile(y0, x0, ".");
    }

    public void objectDestroy(Object c)
    {
        setTile(c.getTilePos()[1], c.getTilePos()[0], ".");
        objects.remove(c);
    }

    public String[] getDynamicMap()
    {
        String[] res = new String[map.length];

        int y = (int) Math.floor(Player.getInstance().getY());
        int x = (int) Math.floor(Player.getInstance().getX());

        StringBuilder sb = new StringBuilder(map[y]);
        String p;
        double a = Player.getInstance().getAngle();

        if (a > 315 || a <= 45)
        {
            p = ">";
        }
        else if (a > 45 && a <= 135)
        {
            p = "V";
        }
        else if (a > 135 && a <= 225)
        {
            p = "<";
        }
        else
        {
            p = "A";
        }

        sb.replace(x, x + 1, p);

        for (int i = 0; i < map.length; i++)
        {
            res[i] = (i == y) ? sb.toString() : map[i];
            String s = res[i].replaceAll("\\.", " ").replaceAll("\\+", "-").replaceAll("#", "*");
            res[i] = s;
        }

        return res;
    }
}
