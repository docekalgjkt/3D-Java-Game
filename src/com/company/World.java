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
            "wall",
            "door",
            };

    private BufferedImage[] wTexs = new BufferedImage[2];

    public BufferedImage getTex(String s)
    {
        return switch (s)
                {
                    case "#" -> wTexs[0];
                    case "-" -> wTexs[1];
                    default -> null;
                };

    }

    //    -Y
    // -X [ ] X
    //     Y
    private String[] map = new String[]{
            //    5    10   15
            "#############",    // 0
            "#########...#",
            "#.......#...#",
            "#.......##-##",
            "#.......#...#",
            "#...#.......#",    // 5
            "#.......#...#",
            "#.......###-#",
            "#.......#.#.#",
            "##-########.#",
            "#....#......#",    // 10
            "#....#...####",
            "#....#...-..#",
            "#......####.#",
            "###########.#",
            "#...#.#.....#",    // 15
            "#...###.....#",
            "#....-......#",
            "#.#####.....#",
            "#...#.#.....#",
            "#.....#########",    // 20
            "#...#.##.....##",
            "##-##.#.......#",
            "#...#.#.......#",
            "#...#.-.......#",
            "#...###.......#",    // 25
            "##.#..#.......#",
            "###...##.....##",
            "###############",
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

        entities.add(new Entity("wraith", 7.5, 5.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
        entities.add(new Entity("wraith", 2.5, 5.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
        entities.add(new Entity("wraith", 2.5, 8.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
        entities.add(new Entity("wraith", 3.5, 2.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

        entities.add(new Entity("wraith", 8.5, 12.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

        entities.add(new Entity("wraith", 9.5, 17.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
        entities.add(new Entity("wraith", 9.5, 19.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

        entities.add(new Entity("wraith", 1.5, 16.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

        entities.add(new Entity("wraith", 3.5, 12.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
        entities.add(new Entity("wraith", 1.5, 13.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

        entities.add(new Entity("wraith", 4.5, 20.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
        entities.add(new Entity("wraith", 1.5, 21.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

        entities.add(new Entity("wraith", 2.5, 24.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

        entities.add(new Entity("wraith", 13.5, 24.5, 0.5, 0.5, 0.25, 4, 3, 3, 25, 0.5));
        entities.add(new Entity("wraith", 10.5, 21.5, 1, 0.5, 0.25, 4, 3, 3, 25, 0.5));
        entities.add(new Entity("wraith", 10.5, 27.5, 0.5, 0.5, 0.25, 4, 3, 3, 25, 0.5));

        for (int i = 0; i < entities.size(); i++)
        {
            entities.get(i).setDrops(new String[]{"magicPotion"});
        }

        // Boss
        entities.add(new Entity("wraith", 10.5, 24.5, 2, 0, 0.25, 12, 10, 10, 15, 0.4));

        staticObjects.add(new StaticObject("barrel", 1.5, 2.5, 1, 0, 0.35, true));
        staticObjects.add(new StaticObject("barrel", 2.5, 2.5, 1, 0, 0.35, true));
        staticObjects.add(new StaticObject("barrel", 1.5, 3.5, 1, 0, 0.35, true));
        staticObjects.add(new StaticObject("barrel", 1.5, 8.5, 1, 0, 0.35, true));
        staticObjects.add(new StaticObject("barrel", 6.5, 8.5, 1, 0, 0.35, true));
        staticObjects.add(new StaticObject("barrel", 7.5, 8.5, 1, 0, 0.35, true));

        staticObjects.add(new StaticObject("barrel", 5.5, 5.5, 1, 0, 0.35, true));
        staticObjects.add(new StaticObject("barrel", 4.5, 4.5, 1, 0, 0.35, true));
        staticObjects.add(new StaticObject("barrel", 4.5, 6.5, 1, 0, 0.35, true));

        staticObjects.add(new StaticObject("barrel", 7.5, 12.5, 1, 0, 0.35, true));

        staticObjects.add(new StaticObject("barrel", 7.5, 15.5, 1, 0, 0.35, true));
        staticObjects.add(new StaticObject("barrel", 8.5, 15.5, 1, 0, 0.35, true));
        
        staticObjects.add(new StaticObject("barrel", 1.5, 15.5, 1, 0, 0.35, true));

        staticObjects.add(new StaticObject("barrel", 10.5, 19.5, 1, 0, 0.35, true));
        staticObjects.add(new StaticObject("barrel", 11.5, 19.5, 1, 0, 0.35, true));

        staticObjects.add(new StaticObject("barrel", 3.5, 25.5, 1, 0, 0.35, true));

        for (int i = 0; i < staticObjects.size(); i++)
        {
            staticObjects.get(i).setDrops(new String[]{(new Random().nextInt(2) == 0) ? "healingPotion" : "magicPotion"});
        }

        pickables.add(new Pickable("healingPotion", 4.5, 10.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL));
        pickables.add(new Pickable("healingPotion", 3.5, 5.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL));
        pickables.add(new Pickable("healingPotion", 6.5, 10.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL));
        pickables.add(new Pickable("healingPotion", 3.5, 15.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL));
        pickables.add(new Pickable("healingPotion", 5.5, 19.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL));

        interactBlocks.add(new InteractBlock(10, 3, InteractBlock.Effect.OPEN, new java.lang.Object[]{10, 3}));
        interactBlocks.add(new InteractBlock(2, 9, InteractBlock.Effect.OPEN, new java.lang.Object[]{2, 9}));
        interactBlocks.add(new InteractBlock(11, 7, InteractBlock.Effect.OPEN, new java.lang.Object[]{11, 7}));
        interactBlocks.add(new InteractBlock(9, 12, InteractBlock.Effect.OPEN, new java.lang.Object[]{9, 12}));
        interactBlocks.add(new InteractBlock(5, 17, InteractBlock.Effect.OPEN, new java.lang.Object[]{5, 17}));
        interactBlocks.add(new InteractBlock(2, 22, InteractBlock.Effect.OPEN, new java.lang.Object[]{2, 22}));
        interactBlocks.add(new InteractBlock(6, 24, InteractBlock.Effect.OPEN, new java.lang.Object[]{6, 24}));

        // Secret
        interactBlocks.add(new InteractBlock(2, 27, InteractBlock.Effect.OPEN, new java.lang.Object[]{2, 27}));

        pickables.add(new Pickable("healingPotion", 4.5, 27.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL));
        pickables.add(new Pickable("healingPotion", 5.5, 27.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL));
        pickables.add(new Pickable("healingPotion", 4.5, 26.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL));
        pickables.add(new Pickable("healingPotion", 5.5, 26.5, 0.5, 0, 0.35, Pickable.Bonus.HEAL));

        try
        {
            wTexs[0] = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + wallTexs[0] + ".png")));
            wTexs[1] = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + wallTexs[1] + ".png")));
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

    public void createEntity(Entity e)
    {
        entities.add(e);
    }

    public void destroyEntity(Entity e)
    {
        entities.remove(e);
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

    //region InteractBlock

    private final List<InteractBlock> interactBlocks = new ArrayList<>();

    public List<InteractBlock> getDoors()
    {
        return interactBlocks;
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
