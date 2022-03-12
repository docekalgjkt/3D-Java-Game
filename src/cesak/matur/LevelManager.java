package cesak.matur;

import com.cesak.*;

import javax.imageio.ImageIO;
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

    private String[] map;

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

    // ---

    public void setUp()
    {
        currentLevel = 1;

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
        placeStatics();
        placePickables();
        placeEnemies();
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
        List<String> list = rfr.getFile("levels/level" + currentLevel + "/playerStart.txt");

        String[] playerStartPos = list.get(0).split(",");

        double playerStartX = Integer.parseInt(playerStartPos[0]) + 0.5;
        double playerStartY = Integer.parseInt(playerStartPos[1]) + 0.5;

        Player.getInstance().setPosition(playerStartX, playerStartY);
    }

    private void placeStatics()
    {
        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("levels/level" + currentLevel + "/staticObjects.txt");

        for (int i = 0; i < list.size(); i++)
        {
            String[] objectProps = list.get(i).split("-");
            String[] objectPos = objectProps[1].split(",");

            int id = Integer.parseInt(objectProps[0]);
            int x = Integer.parseInt(objectPos[0]);
            int y = Integer.parseInt(objectPos[1]);

            staticObjects.add(new StaticObject(id, x, y));
        }
    }

    private void placePickables()
    {
        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("levels/level" + currentLevel + "/pickables.txt");

        for (int i = 0; i < list.size(); i++)
        {
            String[] objectProps = list.get(i).split("-");
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

        for (int i = 0; i < list.size(); i++)
        {
            String[] objectProps = list.get(i).split("-");
            String[] objectPos = objectProps[1].split(",");

            int id = Integer.parseInt(objectProps[0]);
            int x = Integer.parseInt(objectPos[0]);
            int y = Integer.parseInt(objectPos[1]);

            enemies.add(new Enemy(id, x, y));
        }
    }

    // ---

    //region Entities

    private List<Enemy> enemies = new ArrayList<>();

    public List<Enemy> getEnemies()
    {
        return enemies;
    }

    public void destroyEnemy(Enemy enemy)
    {
        enemies.remove(enemy);
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
}
