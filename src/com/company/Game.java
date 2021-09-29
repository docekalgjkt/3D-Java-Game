package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class Game extends JFrame implements KeyListener, MouseMotionListener
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

    private final int scale = 5; // 5

    int height;
    int width;

    public Game()
    {
        if (game == null) game = this;

        setTitle("Program");
        setSize(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);

        height = getSize().height / scale;
        width = getSize().width / scale;

        World.getInstance().setUp();
        frame = 0;
        this.update();

        BufferedImage cImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cImg, new Point(0, 0), "blank");
        getContentPane().setCursor(cursor);

        addKeyListener(this);
        addMouseMotionListener(this);

        setVisible(true);
    }

    public void paint(Graphics g)
    {
        Render.getInstance().render(Toolkit.getDefaultToolkit().getScreenSize().width / scale);

        double[] walls = Render.getInstance().getWalls();
        double[] texs = Render.getInstance().getTexs();

        int edgeUp = 0;
        int edgeDown = 0 / scale;
        int offset = edgeDown / 2;

        // Screen Reset
        //g.clearRect(0, 0, getSize().width, getSize().height);

        // Ceiling
        g.setColor(Color.getHSBColor(0, 0, 0.0f)); // 0, 0, 0.1f
        g.fillRect(0, 0, getSize().width, getSize().height / 2);

        // Floor
        for (int f = 0; f < 10; f++)
        {
            g.setColor(Color.getHSBColor(0.08333333f, 0.4f, 0.1f * (f / 9.0f)));
            int step = (getSize().height - edgeDown * scale) / 2 / 10;
            g.fillRect(0, (getSize().height - edgeDown * scale) / 2 + (step * f), getSize().width, step);
        }

        // Walls
        for (int i = 0; i < walls.length; i++)
        {
            if (walls[i] == 0) continue;

            int lineHeight = (int) (height / walls[i]);
            //float shade = 1 - ((float)walls[i] * 2 / (float) Player.getInstance().getCamDistance());
            float shade = 1 - (Math.round(walls[i]) / /*10.0f*/ (float) Player.getInstance().getCamDistance());
            shade = (shade < 0) ? 0 : shade;

            int y0 = (height / 2) - (lineHeight / 2);
            int x0 = (int) Math.floor((double) World.getInstance().getTex("#").getWidth() * texs[i]);

            int p = World.getInstance().getTex("#").getHeight();
            double h = (double) lineHeight / p;

            double ratio = (p > lineHeight) ? (double) p / lineHeight : 1;

            int prevY = -offset - 1;

            for (int w = 0; w < p / ratio; w++)
            {
                int y1 = (int) Math.floor(y0 + (h * (int) Math.floor(w * ratio/*HERE IS HEIGHT*/))) - offset;

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

                int pixel = World.getInstance().getTex("#").getRGB(x0, (int) Math.floor(w * ratio));
                Color color = new Color(pixel, false);
                Color shadedColor = new Color(((float) color.getRed() / 255) * shade, ((float) color.getGreen() / 255) * shade, ((float) color.getBlue() / 255) * shade);

                g.setColor(shadedColor);
                g.fillRect(i * scale, y1 * scale, scale, ySize);
            }
        }

        // Objects
        List<Object> objects = World.getInstance().getObjects();

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
            if (!object.isRenderd() || object.distToPlayer() <= 0.04) continue;

            int size = (int) (height / object.distToPlayerTan());

            for (int x = 0; x < size; x++)
            {
                int posX = -(size / 2) + x + (int) (object.getXPos() * width);
                int y0 = (height / 2) - (int) (size * object.getYPos());

                if (posX < 0 || posX >= walls.length || (walls[posX] < object.distToPlayerTan() && walls[posX] != 0))
                    continue;

                int p = object.getMyImage().getHeight();
                double h = (double) size / p;

                double ratio = (p > size) ? (double) p / size : 1;

                int prevY = -offset - 1;

                for (int y = 0; y < p / ratio; y++)
                {
                    int y1 = (int) Math.floor(y0 + (h * (int) Math.floor(y * ratio))) - offset;

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

                    double imgW = object.getMyImage().getWidth() * ((double) x / size);

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

        // FPS
        g.setColor(Color.green);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        g.drawString("FPS: " + Math.floor(getFPS()), getSize().width / 2, g.getFont().getSize());
        prevTime = System.currentTimeMillis();

        if (minimap)
        {
            g.setColor(Color.green);
            for (int i = 0; i < World.getInstance().getDynamicMap().length; i++)
            {
                g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                g.drawString(
                        World.getInstance().getDynamicMap()[i],
                        getSize().width - (World.getInstance().getDynamicMap()[0].length() * (int) (g.getFont().getSize() / 1.6)) - 25,
                        g.getFont().getSize() + (i * (g.getFont().getSize() - 3)) + 25);
            }
        }

        // Cursor

        g.setColor(Color.white);
        g.fillRect(getSize().width / 2, (getSize().height - (edgeDown * scale) - 1) / 2, 2, 4);
        g.fillRect(getSize().width / 2 - 1, (getSize().height - (edgeDown * scale)) / 2, 1, 2);
        g.fillRect(getSize().width / 2 + 2, (getSize().height - (edgeDown * scale)) / 2, 1, 2);
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
        if (e.getKeyCode() == 87)
        {
            isMoveF = true;
        }
        if (e.getKeyCode() == 83)
        {
            isMoveB = true;
        }
        if (e.getKeyCode() == 65)
        {
            isMoveL = true;
        }
        if (e.getKeyCode() == 68)
        {
            isMoveR = true;
        }

        if (e.getKeyCode() == 37)
        {
            isRotateL = true;
        }
        if (e.getKeyCode() == 39)
        {
            isRotateR = true;
        }

        // L Shift
        if (e.getKeyCode() == 16)
        {
            Player.getInstance().sprint(true);
        }
        if (e.getKeyCode() == 32)
        {
            if (!attacked)
            {
                attacked = true;
                Object object = new Object(Player.getInstance().getX(), Player.getInstance().getY(), 0.5, 50, false, false, false, "fireball");
                object.setAngle(Player.getInstance().getAngle());
                object.setLit(true);
                World.getInstance().createObject(object);
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
        if (e.getKeyCode() == 87)
        {
            isMoveF = false;
        }
        if (e.getKeyCode() == 83)
        {
            isMoveB = false;
        }
        if (e.getKeyCode() == 65)
        {
            isMoveL = false;
        }
        if (e.getKeyCode() == 68)
        {
            isMoveR = false;
        }

        if (e.getKeyCode() == 37)
        {
            isRotateL = false;
        }
        if (e.getKeyCode() == 39)
        {
            isRotateR = false;
        }

        // L Shift
        if (e.getKeyCode() == 16)
        {
            Player.getInstance().sprint(false);
        }
        if (e.getKeyCode() == 77)
        {
            minimap = !minimap;
        }
        if (e.getKeyCode() == 27)
        {
            //dispose();
        }
        if (e.getKeyCode() == 32)
        {
            attacked = false;
        }
    }

    boolean isMoveF = false;
    boolean isMoveB = false;
    boolean isMoveL = false;
    boolean isMoveR = false;

    boolean isRotateL = false;
    boolean isRotateR = false;

    private int frame;

    void update()
    {
        java.util.Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                frame++;
                if (frame == 60) frame = 0;

                int dir = 0;

                if (isRotateL) Player.getInstance().rotate(-1);
                if (isRotateR) Player.getInstance().rotate(1);

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
                    Player.getInstance().move(dir);

                List<Object> objects = World.getInstance().getObjects();
                for (Object object : objects)
                {
                    if (object.isAlive())
                    {
                        if (object.distToPlayer() < 25 && object.getSpeed() != 0)
                        {
                            object.walk();
                        }
                    }
                    else
                    {
                        if (object.getSpeed() != 0)
                        {
                            object.move();
                        }
                    }
                }

                repaint();
            }
        }, 0, 1000 / 60);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {

    }

    int mouseX;

    @Override
    public void mouseMoved(MouseEvent e)
    {
        if (e.getX() == 0 || e.getX() == Toolkit.getDefaultToolkit().getScreenSize().width - 1)
        {
            try
            {
                Robot robot = new Robot();
                if (e.getX() == 0)
                {
                    robot.mouseMove(Toolkit.getDefaultToolkit().getScreenSize().width - 2, e.getY());
                    mouseX = Toolkit.getDefaultToolkit().getScreenSize().width - 2;
                }
                else if (e.getX() == Toolkit.getDefaultToolkit().getScreenSize().width - 1)
                {
                    robot.mouseMove(1, e.getY());
                    mouseX = 1;
                }
            } catch (AWTException awtException)
            {
                awtException.printStackTrace();
            }

            return;
        }

        if (e.getX() > 0 && e.getX() < Toolkit.getDefaultToolkit().getScreenSize().width - 1)
        {
            if (e.getX() != mouseX) Player.getInstance().rotate((double) (e.getX() - mouseX) / 12.0);
            mouseX = e.getX();
        }
    }
}
