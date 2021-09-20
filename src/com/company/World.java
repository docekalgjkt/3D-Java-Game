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
            "awal"
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
            "#######",
            "#.....#",
            "#.....#",
            "#..#..#",
            "#.....#",
            "#.....#",
            "#######",
            };

    /*

    {
            //    5    10   15   20   25   30   35   40
            "##########################################", // 0
            "#...#.......#...##....##.......###########",
            "#...#.###.#.#......##.....#.#..###########",
            "#.....#.#.#.....##.#####...............###",
            "#.#######.########.#...#..#.#..##.####.###",
            "#...#..............#...#.......#..####.###", // 5
            "#...#...........##.##.#####.####..##.....#",
            "#...#..#######..##...........#######..#..#",
            "##.##..#.....#..##.#########.#.......###.#",
            "##.##.....#.....##.####......#.#.###..#..#",
            "#...............##.#......####.#.###.....#", // 10
            "#.#####.#.###.####.#.##...####.#.#########",
            "#...#...#.......##.#.#######...#.###...###",
            "#...#...#.#..#..##.#.##...##.###.###...###",
            "#...#####.#..#..##.#.##......##...##...###",
            "#.........#..........##...####.....##.####", // 15
            "##.##########.##########.####.......#.####",
            "#..##.....#.....#.....##.#####.....##...##",
            "#.##.......................####...###...##",
            "#......................###....##.##.....##",
            "#####.....#.....#.....#######.......######", // 20
            "##########################################",
    };//     0    5    10   15   20   25   30   35   40

    */

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
        for (int y = 0; y < map.length; y++)
        {
            for (int x = 0; x < map[y].length(); x++)
            {
                boolean stored = true;
                if (getTile(y, x).equals("c"))
                {
                    stored = false;
                    for (Creature creature : creatures)
                    {
                        if (creature.isOnTile(x, y))
                        {
                            stored = true;
                            break;
                        }
                    }
                }

                if (!stored)
                {
                    creatures.add(new Creature(x + 0.5, y + 0.5, "Beholder"));
                }
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

    private final List<Creature> creatures = new ArrayList<>(); // {{}}

    public List<Creature> getCreatures()
    {
        return creatures;
    }

    public void creatureMove(int y0, int x0, int y1, int x1)
    {
        setTile(y1, x1, "c");
        boolean canRemove = true;
        for (Creature creature : creatures)
        {
            if (creature.isOnTile(x0, y0))
            {
                canRemove = false;
                break;
            }
        }
        if (canRemove) setTile(y0, x0, ".");
    }

    public void creatureDestroy(Creature c)
    {
        creatures.remove(c);
    }

    public String[] getDynamicMap()
    {
        String[] res = new String[map.length];

        int y = (int) Math.floor(Player.getInstance().getY());
        int x = (int) Math.floor(Player.getInstance().getX());

        StringBuilder sb = new StringBuilder(map[y]);
        sb.replace(x, x + 1, "@");

        for (int i = 0; i < map.length; i++)
        {
            res[i] = (i == y) ? sb.toString() : map[i];
            String s = res[i].replaceAll("\\.", " ").replaceAll("\\+", "=").replaceAll("#", "*");
            res[i] = s;
        }

        return res;
    }
}
