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

    private final int scale = 4; // 5

    public int getScale()
    {
        return scale;
    }

    int height/* = Toolkit.getDefaultToolkit().getScreenSize().height / scale*/;

    public Game()
    {
        if (game == null) game = this;

        setTitle("Program");
        setSize(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);

        height = getSize().height / scale;

        World.getInstance().setUp();
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
        Render.getInstance().renderAccurate(Toolkit.getDefaultToolkit().getScreenSize().width / scale);

        double[] walls = Render.getInstance().getWalls();
        int[] order = Render.getInstance().getOrder();
        List<String> what = Render.getInstance().getWhat();
        List<Integer> indexes = Render.getInstance().getIndexes();

        List<Creature> creats = Render.getInstance().getCreats();
        List<Double> cDists = Render.getInstance().getCDists();
        List<Integer> cXPos = Render.getInstance().getCXPos();

        List<Double> wallTexCol = Render.getInstance().getWallTexCol();

        g.clearRect(0, 0, getSize().width, getSize().height);

        // Ceiling
        g.setColor(Color.getHSBColor(0, 0, 0.0f)); // 0, 0, 0.1f
        g.fillRect(0, 0, getSize().width, getSize().height / 2);

        // Floor
        for (int f = 0; f < 10; f++)
        {
            g.setColor(Color.getHSBColor(0.08333333f, 0.4f, 0.1f * (f / 9.0f)));
            int offset = getSize().height / 2 / 10;
            g.fillRect(0, getSize().height / 2 + (offset * f), getSize().width, offset);
        }

        for (int i = 0; i < what.size(); i++)
        {
            switch (what.get(i))
            {
                case "wall", "door" -> {
                    if (walls[indexes.get(i)] == 0) continue;

                    String texIn = "#";
                    if ("door".equals(what.get(i)))
                    {
                        texIn = "+";
                    }

                    int lineHeight = (int) (height / walls[indexes.get(i)]);
                    //float shade = 1 - ((float)walls[i] * 2 / (float) Player.getInstance().getCamDistance());
                    float shade = 1 - (Math.round(walls[indexes.get(i)]) / /*10.0f*/ (float) Player.getInstance().getCamDistance());
                    shade = (shade < 0) ? 0 : shade;

                    int y0 = (height / 2) - (lineHeight / 2);
                    int x0 = (int) Math.floor((double) World.getInstance().getTex(texIn).getWidth() * wallTexCol.get(order[indexes.get(i)]));

                    int p = World.getInstance().getTex(texIn).getHeight();
                    double h = (double) lineHeight / p;

                    double ratio = (p > lineHeight) ? (double) p / lineHeight : 1;

                    for (int w = 0; w < p / ratio; w++)
                    {
                        int y1 = (int) Math.floor(y0 + (h * (int) Math.floor(w * ratio + 1)));

                        int pixel = World.getInstance().getTex(texIn).getRGB(x0, (int) Math.floor(w * ratio));
                        Color color = new Color(pixel, false);
                        Color shadedColor = new Color(((float) color.getRed() / 255) * shade, ((float) color.getGreen() / 255) * shade, ((float) color.getBlue() / 255) * shade);

                        g.setColor(shadedColor);
                        g.fillRect(order[indexes.get(i)] * scale, y1 * scale, scale, (int) Math.floor(h + 1) * scale);
                    }
                    //g.setColor(Color.getHSBColor(0, 0, shade));
                    //g.fillRect(order[indexes.get(i)] * scale, y0 * scale, scale, lineHeight * scale);
                }
                case "creature" -> {
                    int size = (int) ((getSize().height) / cDists.get(indexes.get(i)));
                    Image img = creats.get(indexes.get(i)).getImg();

                    for (int i1 = 0; i1 < 6; i1++)
                    {
                        img.getGraphics().setColor(img.getGraphics().getColor().darker());
                    }

                    creats.get(indexes.get(i)).setXPos(cXPos.get(indexes.get(i)));

                    g.drawImage(
                            img,
                            cXPos.get(indexes.get(i)) - size / 2,
                            getSize().height / 2 - size / 2,
                            size,
                            size,
                            null
                    );
                }
            }
        }
/*
        g.setColor(Color.blue);
        g.drawString(String.valueOf(Player.getInstance().getX()), 50, 50);
        g.drawString(String.valueOf(Player.getInstance().getY()), 100, 50);
        g.drawString(String.valueOf(Player.getInstance().getAngle()), 50, 75);
*/
        // FPS
        g.setColor(Color.green);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        g.drawString("FPS: " + Math.floor(getFPS()), getSize().width / 2, g.getFont().getSize()/*getSize().height - g.getFont().getSize()*/);
        prevTime = System.currentTimeMillis();

        if (!minimap) return;

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

    long prevTime = 0;
    boolean minimap = true;

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
            Player.getInstance().attack();
        }
    }

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
            dispose();
        }
    }

    boolean isMoveF = false;
    boolean isMoveB = false;
    boolean isMoveL = false;
    boolean isMoveR = false;

    boolean isRotateL = false;
    boolean isRotateR = false;

    void update()
    {
        java.util.Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
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


                for (int c = 0; c < World.getInstance().getCreatures().size(); c++)
                {
                    if (World.getInstance().getCreatures().get(c).distToPlayer() < 25)
                    {
                        World.getInstance().getCreatures().get(c).move();
                    }
                }

                repaint();
                //prevTime = System.currentTimeMillis();

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
