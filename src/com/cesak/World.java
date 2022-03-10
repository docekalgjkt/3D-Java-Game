package com.cesak;

import cesak.matur.Enemy;
import cesak.matur.Pickable;
import cesak.matur.Player;
import cesak.matur.StaticObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Class where is described the world as a map with walls and objects.
 */
public class World
{
    // TODO: Udelat World Class non-Singleton + prevest na cteni z textoveho souboru

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

    private String[][] map3D = new String[][]
            {
                    {
                            "00000",
                            "00000",
                            "00000",
                            "00040",
                            "00000"
                    },
                    {
                            "11111",
                            "1...1",
                            "1.3.1",
                            "1...1",
                            "11111"
                    },
                    {
                            "22222",
                            "22222",
                            "22322",
                            "22222",
                            "22222"
                    }
            };

    public String[][] getMap3D()
    {
        return map3D;
    }

    public String getTile3D(int y, int x, int z)
    {
        return (x < 0 || y < 0 || z < 0 || x >= map3D[z][y].length() || y >= map3D[z].length || z >= map3D.length)
                ? "1"
                : String.valueOf(map3D[z][y].toCharArray()[x]);
    }


    //    -Y
    // -X [ ] X
    //     Y

    private String[] map = new String[]
            {//    5    10   15
             "#############.......",    // 0
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
             "#.....##############",    // 20
             "#...#.#######.....##",
             "##-##.######.......#",
             "#...#.######.......#",
             "#...#.-............#",
             "#...########.......#",    // 25
             "##.#..######.......#",
             "###...#######.....##",
             "####################",
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

    /*
        void setUp()
        {
            betterMap = new String[map.length];
            for (int i = 0; i < map.length; i++)
            {
                betterMap[i] = map[i].replaceAll("#", "*").replaceAll("\\.", " ");
            }

            entities.add(new Enemy("wraith", 7.5, 5.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
            entities.add(new Enemy("wraith", 2.5, 5.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
            entities.add(new Enemy("wraith", 2.5, 8.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
            entities.add(new Enemy("wraith", 3.5, 2.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

            entities.add(new Enemy("wraith", 8.5, 12.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

            entities.add(new Enemy("wraith", 9.5, 17.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
            entities.add(new Enemy("wraith", 9.5, 19.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

            entities.add(new Enemy("wraith", 1.5, 16.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

            entities.add(new Enemy("wraith", 3.5, 12.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
            entities.add(new Enemy("wraith", 1.5, 13.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

            entities.add(new Enemy("wraith", 4.5, 20.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));
            entities.add(new Enemy("wraith", 1.5, 21.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

            entities.add(new Enemy("wraith", 2.5, 24.5, 1, 0, 0.25, 5, 4, 4, 20, 0.5));

            entities.add(new Enemy("wraith", 18.5, 24.5, 0.5, 0.5, 0.25, 4, 3, 3, 25, 0.5));
            entities.add(new Enemy("wraith", 15.5, 21.5, 0.5, 0.5, 0.25, 4, 3, 3, 25, 0.5));
            entities.add(new Enemy("wraith", 15.5, 27.5, 0.5, 0.5, 0.25, 4, 3, 3, 25, 0.5));

            for (int i = 0; i < entities.size(); i++)
            {
                entities.get(i).setLoot("magicPotion");
            }

            // Boss
            entities.add(new Enemy("wraith", 15.5, 24.5, 1.5, 0, 0.25, 12, 10, 10, 15, 0.4));

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
                int r = new Random().nextInt(4);

                if (r == 0)
                {
                    staticObjects.get(i).setDrops(new String[]{"healingPotion"});
                }
                else if (r == 1)
                {
                    staticObjects.get(i).setDrops(new String[]{"magicPotion"});
                }
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
            interactBlocks.add(new InteractBlock(6, 24, InteractBlock.Effect.WALL, new java.lang.Object[]{5, 22}));

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

            Player.getInstance().setPosition(1.5, 1.5);
        }
    */
    public void reset()
    {
        entities = new ArrayList<>();
        staticObjects = new ArrayList<>();
        pickables = new ArrayList<>();
        interactBlocks = new ArrayList<>();

        Player.getInstance().setHealth(Player.getInstance().getHealthMax());

        //setUp();
    }

    //region Entities

    private List<Enemy> entities = new ArrayList<>();

    public List<Enemy> getEntities()
    {
        return entities;
    }

    //endregion

    //region Static Objects

    private List<StaticObject> staticObjects = new ArrayList<>();

    public List<StaticObject> getStaticObjects()
    {
        return staticObjects;
    }

    //endregion

    //region Pickables

    private List<Pickable> pickables = new ArrayList<>();

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

    private List<InteractBlock> interactBlocks = new ArrayList<>();

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
