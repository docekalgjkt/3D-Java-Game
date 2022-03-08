package com.cesak;

import cesak.matur.Player;
import cesak.matur.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * Class which controls GUI and also User's Input
 */
public class Game extends JFrame implements KeyListener
{
    // region Singleton

    private static Game game = null;

    public static Game getInstance()
    {
        if (game == null)
        {
            game = new Game();
        }
        return game;
    }

    // endregion

    // hodnota, která mění rozlišení vykresleného obrázku podle vzorce: Resolution / scale
    private final int scale = 5; // 5

    private int height;
    private int width;

    public int getScreenHeight()
    {
        return height;
    }

    JPanel view;

    public Game()
    {
        if (game == null) game = this;

        setTitle("Program");
        setSize(1250, 800);
        //setSize(800, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width / 2 - getWidth() / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getHeight() / 2
                   );
        //setUndecorated(true);
        //setExtendedState(MAXIMIZED_BOTH);

        view = new RenderPanel();
        view.setPreferredSize(new Dimension(getSize().width, getSize().height));
        add(view);

        height = view.getPreferredSize().height / scale - ((isUndecorated()) ? 0 : 18);
        width = view.getPreferredSize().width / scale;

        World.getInstance().setUp();
        this.update();

        BufferedImage cImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cImg, new Point(0, 0), "blank");
        getContentPane().setCursor(cursor);

        addKeyListener(this);

        setVisible(true);
    }

    Color[] colors = new Color[]{
            new Color(255, 0, 0),
            new Color(0, 255, 0),
            new Color(0, 0, 255),
            new Color(200, 200, 0),
            new Color(175, 0, 255)
    };

    /**
     * Panel, na který je vykreslován pohled do scény
     */
    private class RenderPanel extends JPanel
    {
        public void paintComponent1(Graphics g)
        {
            int edgeUp = 0 / scale;
            int edgeDown = 0 / scale;
            int edgeLeft = 0 / scale;
            int edgeRight = 0 / scale;

            int offset = edgeDown / 2;

            cesak.matur.Renderer.getInstance().render(width - (edgeLeft + edgeRight));

            double[] walls = cesak.matur.Renderer.getInstance().getWalls();
            double[] texs = cesak.matur.Renderer.getInstance().getTexs();
            String[] what = cesak.matur.Renderer.getInstance().getWhat();

            g.setColor(new Color(40, 40, 40));
            g.fillRect(0, 0, width * scale, height * scale);

            //region Ceiling
            g.setColor(new Color(5, 5, 5)); // 0, 0, 0.1f
            //g.setColor(Color.getHSBColor(0.52f, 0.9f, 1f)); // 0, 0, 0.1f
            g.fillRect(edgeLeft * scale, edgeUp * scale, (width - (edgeLeft + edgeRight)) * scale, (height * scale / 2));
            //endregion

            //region Floors
            g.setColor(new Color(30, 30, 30));
            g.fillRect(edgeLeft * scale, (((height - edgeDown) * scale) / 2), (width - (edgeLeft + edgeRight)) * scale, ((height - edgeDown) * scale));
            //endregion

            //region Walls
            for (int i = 0; i < walls.length; i++)
            {
                if (walls[i] == 0) continue;

                int lineHeight = (int) (height / walls[i]);
                //float shade = 1 - ((float)walls[i] * 2 / (float) Player.getInstance().getCamDistance());
                float shade = 1 - (Math.round(walls[i]) / /*10.0f*/ (float) Player.getInstance().getCamDistance());
                shade = (shade < 0) ? 0 : shade;

                int y0 = (height / 2) - (lineHeight / 2);
                int x0 = (int) Math.floor((double) World.getInstance().getTex(what[i]).getWidth() * texs[i]);

                int p = World.getInstance().getTex(what[i]).getHeight();
                double h = (double) lineHeight / p;

                double ratio = (p > lineHeight) ? (double) p / lineHeight : 1;

                int prevY = -offset - 1;

                for (int w = 0; w < p / ratio; w++)
                {
                    int y1 = (int) (y0 + (h * (int) (w * ratio + 2/*HERE IS HEIGHT*/))) - offset;

                    if (w != 0 && y1 == prevY)
                    {
                        y1++;
                    }

                    prevY = y1;

                    int ySize = (int) Math.floor(h + 1) * scale;

                    if (y1 < edgeUp)
                    {
                        if (-y1 >= (int) Math.floor(h + 1))
                        {
                            continue;
                        }
                        else if (-y1 < (int) Math.floor(h + 1))
                        {
                            ySize += y1;
                            y1 = edgeUp;
                        }
                    }
                    else if ((y1 * scale) >= (height - edgeDown) * scale)
                    {
                        break;
                    }
                    else if ((y1 * scale) + ySize >= (height - edgeDown) * scale)
                    {
                        ySize = ((height - edgeDown) * scale) - (y1 * scale) - 1;
                    }

                    int pixel = World.getInstance().getTex(what[i]).getRGB(x0, (int) Math.floor(w * ratio));
                    Color color = new Color(pixel, false);
                    Color shadedColor = new Color(((float) color.getRed() / 255) * shade, ((float) color.getGreen() / 255) * shade, ((float) color.getBlue() / 255) * shade);

                    g.setColor(shadedColor);
                    g.fillRect((i + edgeLeft) * scale, y1 * scale, scale, ySize);
                }
            }
            //endregion

            //region Objects
            List<Object> objects = new ArrayList<>();

            objects.addAll(World.getInstance().getEntities());
            objects.addAll(World.getInstance().getStaticObjects());
            objects.addAll(World.getInstance().getProjectiles());
            objects.addAll(World.getInstance().getPickables());

            // Sorting objects (by distance)
            for (int i = 1; i < objects.size(); i++)
            {
                for (int i1 = 0; i1 < i; i1++)
                {
                    if (objects.get(i).distToPlayer() > objects.get(i1).distToPlayer())
                    {
                        objects.add(i1, objects.get(i));
                        objects.remove(i + 1);
                    }
                }
            }

            // Rendering objects
            for (Object object : objects)
            {
                if (!object.isRendered() || object.distToPlayer() <= Player.getInstance().getNearClip()) continue;

                int size = (int) ((height / (object.distToPlayerTan() / 1.1)) * object.getSize());
                int sizeX = (int) (((width / (object.distToPlayerTan() / 1.1)) / (16.0 / 9)) * object.getSize());

                for (int x = 0; x < sizeX; x++)
                {
                    int posX = -(sizeX / 2) + x + (int) (object.getXPos() * (width - (edgeLeft + edgeRight))) + edgeLeft;
                    double yPos = (1.0 + object.getYPos()) - ((1.0 / object.getSize()) * 0.5);
                    int y0 = (height / 2) - (int) (size * (yPos));

                    if (posX < edgeLeft || posX >= walls.length + edgeRight || (walls[posX - edgeLeft] < object.distToPlayerTan() && walls[posX - edgeLeft] != 0))
                        continue;

                    int p = object.getMyImage().getHeight();
                    double h = (double) size / p;

                    double ratio = (p > size) ? (double) p / size : 1;

                    int prevY = -offset - 1;

                    for (int y = 0; y < p / ratio; y++)
                    {
                        int y1 = (int) Math.floor(y0 + (h * (int) Math.floor(y * ratio + 2))) - offset;

                        if (y != 0 && y1 == prevY)
                        {
                            y1++;
                        }

                        prevY = y1;

                        int ySize = (int) Math.floor(h + 1) * scale;

                        if (y1 < edgeUp)
                        {
                            if (-y1 >= (int) Math.floor(h + 1))
                            {
                                continue;
                            }
                            else if (-y1 < (int) Math.floor(h + 1))
                            {
                                ySize += y1;
                                y1 = edgeUp;
                            }
                        }
                        else if ((y1 * scale) >= (height - edgeDown) * scale)
                        {
                            break;
                        }
                        else if ((y1 * scale) + ySize >= (height - edgeDown) * scale)
                        {
                            ySize = ((height - edgeDown) * scale) - (y1 * scale) - 1;
                        }

                        double imgW = object.getMyImage().getWidth() * ((double) x / sizeX);

                        int pixel = object.getMyImage().getRGB((int) imgW, (int) Math.floor(y * ratio));
                        if (pixel == 0) continue;

                        float shade = 1 - (Math.round(object.distToPlayerTan()) / (float) Player.getInstance().getCamDistance());
                        shade = (shade < 0) ? 0 : shade;

                        Color color = new Color(pixel, false);
                        Color shadedColor = new Color(((float) color.getRed() / 255) * shade, ((float) color.getGreen() / 255) * shade, ((float) color.getBlue() / 255) * shade);

                        g.setColor((object.isLit()) ? color : shadedColor);
                        g.fillRect(posX * scale, y1 * scale, scale, ySize);
                    }
                }
            }

            //endregion

            //region UI

            // FPS
            g.setColor(Color.green);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
            g.drawString("FPS: " + Math.floor(getFPS()), width * scale / 2, g.getFont().getSize() + edgeUp * scale);
            prevTime = System.currentTimeMillis();

            if (minimap)
            {
                g.setColor(Color.green);
                g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                String[] map = World.getInstance().getDynamicMap();
                for (int i = 0; i < World.getInstance().getDynamicMap().length; i++)
                {
                    g.drawString(
                            map[i],
                            width * scale - (map[0].length() * (int) (g.getFont().getSize() / 1.6)) - 25,
                            g.getFont().getSize() + (i * (g.getFont().getSize() - 3)) + 25);
                }
            }

            // Cursor

            g.setColor(Color.white);
            g.fillRect(width * scale / 2, (((height - edgeDown) * scale) - 1) / 2, 2, 4);
            g.fillRect(width * scale / 2 - 1, ((height - edgeDown) * scale) / 2, 1, 2);
            g.fillRect(width * scale / 2 + 2, ((height - edgeDown) * scale) / 2, 1, 2);

            // Health bar

            g.setColor(new Color(10, 10, 10));
            g.fillRect(20, (height - 80 / scale) * scale + 20, 300, 25);
            g.setColor(Color.red);
            g.fillRect(20, (height - 80 / scale) * scale + 20, (int) (300 * Player.getInstance().getHealthPercent()), 25);

            // Magic bar

            g.setColor(new Color(10, 10, 10));
            g.fillRect(20, (height - 80 / scale) * scale + 45, 300, 15);
            g.setColor(new Color(0, 40, 255));
            g.fillRect(20, (height - 80 / scale) * scale + 45, (int) (300 * Player.getInstance().getManaPercent()), 15);

            //endregion
        }

        public void paintComponent(Graphics g)
        {
            cesak.matur.Renderer.getInstance().render3D(Player.getInstance().getX(), Player.getInstance().getY(), 1.5, Player.getInstance().getAngle(), 0, Player.getInstance().getCamDistance(), width, height);

            String[][] textureHit = Renderer.getInstance().getTextureHit();
            //double[][][] hitPoint = Renderer.getInstance().getHitPoint();

            for (int y = 0; y < textureHit.length; y++)
            {
                for (int x = 0; x < textureHit[y].length; x++)
                {
                    g.setColor(colors[Integer.parseInt(textureHit[y][x])]);
                    g.fillRect(x * scale, y * scale, scale, scale);
                }
            }
        }
    }

    long prevTime = 0;
    boolean minimap = false;

    public double getFPS()
    {
        return 1000.0 / (System.currentTimeMillis() - prevTime);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        // W
        if (e.getKeyCode() == 87)
        {
            isMoveF = true;
        }
        // S
        if (e.getKeyCode() == 83)
        {
            isMoveB = true;
        }
        // A
        if (e.getKeyCode() == 65)
        {
            isMoveL = true;
        }
        // D
        if (e.getKeyCode() == 68)
        {
            isMoveR = true;
        }

        // Left Arrow
        if (e.getKeyCode() == 37)
        {
            isRotateL = true;
        }
        // Right Arrow
        if (e.getKeyCode() == 39)
        {
            isRotateR = true;
        }

        // Space
        if (e.getKeyCode() == 32)
        {
            if (!attacked)
            {
                attacked = true;
                Player.getInstance().castFireball();
            }
        }
    }

    boolean attacked = false;

    // UP, DOWN, LEFT, RIGHT
    // 38, 40, 37, 39

    //  W,  S,  A,  D,  Q,  E
    // 87, 83, 65, 68, 81, 69

    @Override
    public void keyReleased(KeyEvent e)
    {
        // W
        if (e.getKeyCode() == 87)
        {
            isMoveF = false;
        }
        // S
        if (e.getKeyCode() == 83)
        {
            isMoveB = false;
        }
        // A
        if (e.getKeyCode() == 65)
        {
            isMoveL = false;
        }
        // D
        if (e.getKeyCode() == 68)
        {
            isMoveR = false;
        }

        // Left Arrow
        if (e.getKeyCode() == 37)
        {
            isRotateL = false;
        }
        // Right Arrow
        if (e.getKeyCode() == 39)
        {
            isRotateR = false;
        }

        // M
        if (e.getKeyCode() == 77)
        {
            minimap = !minimap;
        }
        // Escape
        if (e.getKeyCode() == 27)
        {
            System.exit(0);
        }
        // Space
        if (e.getKeyCode() == 32)
        {
            attacked = false;
        }
        // E
        if (e.getKeyCode() == 69)
        {
            Player.getInstance().interact();
        }
    }

    boolean isMoveF = false;
    boolean isMoveB = false;
    boolean isMoveL = false;
    boolean isMoveR = false;

    boolean isRotateL = false;
    boolean isRotateR = false;

    /**
     * Method which updates the game each frame (default FPS is 60)
     */
    void update()
    {
        java.util.Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                Player.getInstance().healthRegen();
                Player.getInstance().manaRegen();

                //region Player Control

                int dir = 0;

                if (isRotateL) Player.getInstance().performRotation(-0.9);
                if (isRotateR) Player.getInstance().performRotation(0.9);

                if (isMoveB) dir = 180;
                if (isMoveL) dir = 270;
                if (isMoveR) dir = 90;
                if (isMoveF && isMoveL) dir = 315;
                if (isMoveF && isMoveR) dir = 45;
                if (isMoveB && isMoveL) dir = 225;
                if (isMoveB && isMoveR) dir = 135;

                if ((isMoveF || isMoveB || isMoveL || isMoveR) &&
                        !(isMoveF && isMoveB) &&
                        !(isMoveL && isMoveR)
                )
                    Player.getInstance().performMovement(dir);

                //endregion
/*
                //region Objects

                List<Entity> entities = World.getInstance().getEntities();
                for (int i = 0; i < entities.size(); i++)
                {
                    if (entities.get(i).isDead()) continue;

                    if (entities.get(i).distToPlayer() < 6 * 6
                            && entities.get(i).distToPlayer() > 0.75 * 0.75
                            && entities.get(i).getSpeed() != 0
                            && entities.get(i).isRendered())
                    {
                        entities.get(i).move();
                    }
                    else if (entities.get(i).isMoving())
                    {
                        entities.get(i).stopMove();
                    }

                    if (entities.get(i).distToPlayer() <= entities.get(i).getAttackRange() * entities.get(i).getAttackRange())
                    {
                        entities.get(i).startAttack();
                    }

                    entities.get(i).anim();
                }

                List<Projectile> projectiles = World.getInstance().getProjectiles();
                for (int i = 0; i < projectiles.size(); i++)
                {
                    projectiles.get(i).move();
                }

                //endregion
*/
                view.repaint();
            }
        }, 0, 1000 / 60);
    }
}
