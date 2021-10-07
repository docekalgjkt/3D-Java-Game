package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
            "wall"
    };

    private final String[] doorTexs = new String[]{
            "wall"
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
    private String[] map = new String[]{
            //    5    10   15
            "#########",        // 0
            "#.......#",
            "#.......#####",
            "#.......#...#",
            "#...#.......#",
            "#.......#...#",    // 5
            "#.......###.#",
            "#.......#.#.#",
            "##.########.#",
            "#....#......#",
            "#....#...####",    // 10
            "#....#......#",
            "#......####.#",
            "###########.#",
            "#...#.#.....#",
            "#...###.....#",    // 15
            "#...........#",
            "#.#####.....#",
            "#...###.....#",
            "#.....#######",
            "#...#.#.....#",    // 20
            "##.##.#.....#",
            "#...#.......#",
            "#...#.#.....#",
            "##.####.....#",
            "#############",    // 25
            //    5    10   15
    };

    public String[] getMap()
    {
        return map;
    }

    private String[] betterMap;

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
        /*
        map = new String[150];

        for (int i = 0; i < map.length; i++)
        {
            StringBuilder sb = new StringBuilder();

            sb.append("#");

            for (int i1 = 1; i1 < map.length; i1++)
            {
                if (i == 0 || i == map.length - 1)
                {
                    sb.append("#");
                }
                else
                {
                    if (new Random().nextDouble() < 0.1)
                    {
                        sb.append("#");
                    }
                    else
                    {
                        sb.append(".");
                    }
                }
            }

            sb.append("#");

            map[i] = sb.toString();
        }

        */

        betterMap = new String[map.length];
        for (int i = 0; i < map.length; i++)
        {
            betterMap[i] = map[i].replaceAll("#", "*").replaceAll("\\.", " ");
        }

        entities.add(new Entity(/*       */7.5, 4.5, 1, 0, 0.25, 7.5, 1.5, "wraith"));
        entities.add(new Entity(/*       */9.5, 16.5, 1, 0, 0.25, 7.5, 1.5, "wraith"));
        entities.add(new Entity(/*       */9.5, 18.5, 1, 0, 0.25, 7.5, 1.5, "wraith"));

        staticObjects.add(new StaticObject(1.5, 7.5, 1, 0, 0.35, true, "barrel"));
        staticObjects.add(new StaticObject(6.5, 7.5, 1, 0, 0.35, true, "barrel"));
        staticObjects.add(new StaticObject(7.5, 7.5, 1, 0, 0.35, true, "barrel"));
        staticObjects.add(new StaticObject(7.5, 11.5, 1, 0, 0.35, true, "barrel"));
        staticObjects.add(new StaticObject(8.5, 11.5, 1, 0, 0.35, true, "barrel"));

        pickables.add(new Pickable(/*    */9.5, 3.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL, "healingPotion"));
        pickables.add(new Pickable(/*    */10.5, 3.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL, "healingPotion"));
        pickables.add(new Pickable(/*    */11.5, 3.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL, "healingPotion"));

        try
        {
            wTex = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + wallTexs[0] + ".png")));
            dTexs[0] = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + doorTexs[0] + ".png")));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //region Entities

    private final List<Entity> entities = new ArrayList<>();

    public List<Entity> getEntities()
    {
        return entities;
    }

    //endregion

    //region Static Objects

    private final List<StaticObject> staticObjects = new ArrayList<>();

    public List<StaticObject> getStaticObjects()
    {
        return staticObjects;
    }

    //endregion

    //region Projectiles

    private final List<Projectile> projectiles = new ArrayList<>();

    public List<Projectile> getProjectiles()
    {
        return projectiles;
    }

    public void createProjectile(Projectile p)
    {
        projectiles.add(p);
    }

    public void destroyProjectile(Projectile p)
    {
        projectiles.remove(p);
    }

    //endregion

    //region Pickables

    private final List<Pickable> pickables = new ArrayList<>();

    public List<Pickable> getPickables()
    {
        return pickables;
    }

    public void createPickable(Pickable p)
    {
        pickables.add(p);
    }

    public void destroyPickable(Pickable p)
    {
        pickables.remove(p);
    }

    //endregion

    public String[] getDynamicMap()
    {
        String[] res = new String[betterMap.length];

        int y = (int) Math.floor(Player.getInstance().getY());
        int x = (int) Math.floor(Player.getInstance().getX());

        StringBuilder sb = new StringBuilder(betterMap[y]);
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

        for (int i = 0; i < betterMap.length; i++)
        {
            res[i] = (i == y) ? sb.toString() : betterMap[i];
            //String s = res[i].replaceAll("\\.", " ").replaceAll("\\+", "-").replaceAll("#", "*");
            //res[i] = s;
        }

        return res;
    }
}
