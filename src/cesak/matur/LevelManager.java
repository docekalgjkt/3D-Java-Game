package cesak.matur;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LevelManager
{
    // region Singleton

    private static LevelManager levelManager = null;

    public static LevelManager getInstance()
    {
        if (levelManager == null)
        {
            levelManager = new LevelManager();
        }
        return levelManager;
    }

    // endregion

    public BufferedImage getTexture(int i)
    {
        return wallTextures.get(i);
    }

    private final List<BufferedImage> wallTextures = new ArrayList<>();

    public List<BufferedImage> getWallTextures()
    {
        return wallTextures;
    }

    // ---

    private int currentLevel;

    private int lastLevel()
    {
        ResFileReader rfr = new ResFileReader();

        int i = 0;

        List<String> list = rfr.getFile("levels/level" + i + "/map.txt");

        while (list != null)
        {
            i++;
            list = rfr.getFile("levels/level" + i + "/map.txt");
        }

        return i - 1;
    }

    private String[] map;

    public String[] getMap()
    {
        return map;
    }

    public String getTile(int y, int x)
    {
        return (y < 0 || y >= map.length || x < 0 || x >= map[y].length()) ? "0" : String.valueOf(map[y].toCharArray()[x]);
    }

    public void setTile(int y, int x, String what)
    {
        StringBuilder sb = new StringBuilder(map[y]);
        sb.replace(x, x + 1, what);
        map[y] = sb.toString();
    }

    private int[] levelEnd;

    public int[] getLevelEnd()
    {
        return levelEnd;
    }

    // ---

    public void loadLevel(int level)
    {
        currentLevel = level;
        setUp();
    }

    public void setUp()
    {
        getLevelMap();

        try
        {
            int i = 0;

            URL url = getClass().getClassLoader().getResource("textures/walls/wall" + i + ".png");

            while (url != null)
            {
                wallTextures.add(ImageIO.read(Objects.requireNonNull(url)));

                i++;
                url = getClass().getClassLoader().getResource("textures/walls/wall" + i + ".png");
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        setPlayerStartPos();
        placeObjects();
        placePickables();
        placeEnemies();
        placeDoors();
    }

    private void getLevelMap()
    {
        ResFileReader rfr = new ResFileReader();

        List<String> list = rfr.getFile("levels/level" + currentLevel + "/map.txt");
        map = list.toArray(new String[0]);
    }

    private void setPlayerStartPos()
    {
        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("levels/level" + currentLevel + "/startEnd.txt");

        String[] playerStartPos = list.get(0).split(",");

        double playerStartX = Integer.parseInt(playerStartPos[0]) + 0.5;
        double playerStartY = Integer.parseInt(playerStartPos[1]) + 0.5;
        double playerStartAngle = Double.parseDouble(playerStartPos[2]);

        Player.getInstance().setPosition(playerStartX, playerStartY);
        Player.getInstance().setRotation(playerStartAngle);

        levelEnd = new int[2];
        levelEnd[0] = Integer.parseInt(list.get(1).split(",")[0]);
        levelEnd[1] = Integer.parseInt(list.get(1).split(",")[1]);
    }

    private void placeObjects()
    {
        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("levels/level" + currentLevel + "/objects.txt");

        objects = new ArrayList<>();

        for (String line : list)
        {
            String[] objectProps = line.split("-");
            String[] objectPos = objectProps[1].split(",");

            int id = Integer.parseInt(objectProps[0]);
            int x = Integer.parseInt(objectPos[0]);
            int y = Integer.parseInt(objectPos[1]);

            objects.add(new LevelObject(id, x, y, "objects/object"));
        }
    }

    private void placePickables()
    {
        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("levels/level" + currentLevel + "/pickables.txt");

        pickables = new ArrayList<>();

        for (String line : list)
        {
            String[] objectProps = line.split("-");
            String[] objectPos = objectProps[1].split(",");

            int id = Integer.parseInt(objectProps[0]);
            int x = Integer.parseInt(objectPos[0]);
            int y = Integer.parseInt(objectPos[1]);

            pickables.add(new Pickable(id, x, y));
        }
    }

    private void placeEnemies()
    {
        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("levels/level" + currentLevel + "/enemies.txt");

        enemies = new ArrayList<>();

        for (String line : list)
        {
            String[] objectProps = line.split("-");
            String[] objectPos = objectProps[1].split(",");

            int id = Integer.parseInt(objectProps[0]);
            int x = Integer.parseInt(objectPos[0]);
            int y = Integer.parseInt(objectPos[1]);

            enemies.add(new Enemy(id, x, y));
        }
    }

    private void placeDoors()
    {
        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("levels/level" + currentLevel + "/doors.txt");

        doors = new ArrayList<>();

        for (String line : list)
        {
            String[] objectPos = line.split(",");

            int x = Integer.parseInt(objectPos[0]);
            int y = Integer.parseInt(objectPos[1]);

            doors.add(new Door(x, y));
        }
    }

    public void EndLevel()
    {
        GameController.getInstance().reset();

        String[] options = new String[]{
                "Next Level"
        };

        int n = JOptionPane.showOptionDialog(null, "LEVEL FINISHED!", "Good job!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 0);

        if (n == 0)
        {
            if (currentLevel != lastLevel())
            {
                loadLevel(currentLevel + 1);
            }
        }
    }

    // ---

    //region Entities

    private List<Enemy> enemies = new ArrayList<>();

    public List<Enemy> getEnemies()
    {
        return enemies;
    }

    //endregion

    //region Objects

    private List<LevelObject> objects = new ArrayList<>();

    public List<LevelObject> getObjects()
    {
        return objects;
    }

    //endregion

    //region Pickables

    private List<Pickable> pickables = new ArrayList<>();

    public List<Pickable> getPickables()
    {
        return pickables;
    }

    public void destroyPickable(Pickable p)
    {
        pickables.remove(p);
    }

    //endregion

    //region Door

    private List<Door> doors = new ArrayList<>();

    public List<Door> getDoors()
    {
        return doors;
    }

    //endregion
}
