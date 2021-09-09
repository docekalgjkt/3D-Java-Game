package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class Game extends JFrame implements KeyListener {

    // region Singleton

    private static Game game = null;

    public static Game getInstance() {
        if (game == null) {
            game = new Game();
        }
        return game;
    }

    // endregion

    private Graphics g;

    private final int scale = 5;
    public int getScale() {
        return scale;
    }

    int width = Toolkit.getDefaultToolkit().getScreenSize().width / scale, height = Toolkit.getDefaultToolkit().getScreenSize().height / scale;

    public Game(){
        setTitle("Program");
        setSize(Toolkit.getDefaultToolkit().getScreenSize().width / scale, Toolkit.getDefaultToolkit().getScreenSize().height / scale);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        World.getInstance().setUp();
        this.update();

        addKeyListener(this);
        setVisible(true);

        /*i = 0;
        java.util.Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, 1);*/
    }
/*
    int i;
    double[] walls;
    int[] order;*/

    public void paint(Graphics g){
        this.g = g;

        double[] walls = Render.getInstance().getWalls();
        int[] order = Render.getInstance().getOrder();
        List<String> what = Render.getInstance().getWhat();
        List<Integer> indexes = Render.getInstance().getIndexes();

        List<Creature> creats = Render.getInstance().getCreats();
        List<Double> cDists = Render.getInstance().getCDists();
        List<Integer> cXPos = Render.getInstance().getCXPos();

        List<Double> wallTexCol = Render.getInstance().getWallTexCol();

        g.clearRect(0, 0, getSize().width, getSize().height);

        int floorOffset = 0; // 50

        // Ceiling
        g.setColor(Color.getHSBColor(0, 0, 0.0f)); // 0, 0, 0.1f
        g.fillRect(0, 0, getSize().width, getSize().height / 2 + floorOffset);

        // Floor
        /*g.setColor(Color.getHSBColor(0, 0, 0.1f));
        g.fillRect(0, getSize().height / 2 + floorOffset, getSize().width, getSize().height / 2 - floorOffset);*/
        for (int f = 0; f < 10; f++) {
            g.setColor(Color.getHSBColor(0, 0, 0.1f * (f / 10.0f)));
            int offset = getSize().height / 2 / 10;
            g.fillRect(0, getSize().height / 2 + (offset * f), getSize().width, offset);
        }

        for (int i = 0; i < what.size(); i++) {
            switch (what.get(i)) {
                case "wall", "door": {
                    if(walls[indexes.get(i)] == 0) continue;

                    String texIn = "#";
                    if ("door".equals(what.get(i))) {
                        texIn = "+";
                    }

                    int lineHeight = (int)(height / walls[indexes.get(i)]);
                    //float shade = 1 - ((float)walls[i] * 2 / (float) Player.getInstance().getCamDistance());
                    float shade = 1 - (Math.round(walls[indexes.get(i)]) / 11.0f);
                    shade = (shade < 0) ? 0 : shade;

                    int y0 = (height / 2) - (lineHeight / 2);
                    int x0 = (int)Math.floor((double)World.getInstance().getTex(texIn).getWidth() * wallTexCol.get(order[indexes.get(i)]));

                    int p = World.getInstance().getTex(texIn).getHeight();
                    double h = (double)lineHeight / p;

                    for (int w = 0; w < p; w++) {
                        int y1 = (int)Math.floor(y0 + (h * w));

                        int pixel = World.getInstance().getTex(texIn).getRGB(x0, w);
                        Color color = new Color(pixel, false);
                        Color shadedColor = new Color(((float) color.getRed() / 255) * shade, ((float) color.getGreen() / 255) * shade, ((float) color.getBlue() / 255) * shade);

                        g.setColor(shadedColor);
                        g.fillRect(order[indexes.get(i)] * scale, y1 * scale, scale, (int)Math.floor(h + 1) * scale);
                    }
                    break;
                }
                case "creature": {
                    int size = (int)((getSize().height) / cDists.get(indexes.get(i)));
                    g.drawImage(
                            creats.get(indexes.get(i)).getImg(),
                            cXPos.get(indexes.get(i)) - size / 2,
                            getSize().height / 2 - size / 2,
                            size,
                            size,
                            null
                    );
                    break;
                }
            }
        }
/*
        // Draw Weapon
        int size = (int)Math.floor(getSize().width);

        try {
            g.drawImage(
                    ImageIO.read(getClass().getClassLoader().getResource("images/IMAGE.png")),
                    getSize().width / 2 - (size / 2),
                    getSize().height - (size / 2),
                    size,
                    size / 2,
                    null
            );
        } catch (IOException e) {
            e.printStackTrace();
        }*/

/*
        if(walls[i] != 0) {
            if(walls[i] < 2 && !beholder) {
                Image img = Toolkit.getDefaultToolkit().getImage("D:\\BOOOM\\IntelliJ\\3D-rendering\\images\\Beholder.png");
                g.drawImage(img, getSize().width / 2 - 250, getSize().height / 2 - 250, 500, 500, null);
                beholder = true;
            }

            int lineHeight = (int)((getSize().height) / walls[i]);
            //float shade = 1 - ((float)walls[i] * 2 / (float) Player.getInstance().getCamDistance());
            float shade = 1 - (Math.round(walls[i]) / 10.0f);

            g.setColor(Color.getHSBColor(0, 0, shade < 0 ? 0 : shade));
            g.fillRect(order[i], getSize().height / 2 - (lineHeight / 2), 1, lineHeight);
        }

        i++;

        if(i == getSize().width) {
            i = 0;
        }*/

        g.setColor(Color.blue);
        g.drawString(String.valueOf(Player.getInstance().getX()), 50, 50);
        g.drawString(String.valueOf(Player.getInstance().getY()), 100, 50);
        g.drawString(String.valueOf(Player.getInstance().getAngle()), 50, 75);

        g.setColor(Color.green);
        for (int i = 0; i < World.getInstance().getDynamicMap0().length; i++) {
            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            g.drawString(
                    World.getInstance().getDynamicMap0()[i],
                    getSize().width - (World.getInstance().getDynamicMap0()[0].length() * (int)(g.getFont().getSize() / 1.6)),
                    g.getFont().getSize() + (i * g.getFont().getSize()));
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyCode() == 122) {
            setUndecorated(!isUndecorated());
            /*if(isUndecorated())setExtendedState(JFrame.MAXIMIZED_BOTH);
            else setExtendedState(JFrame.NORMAL);*/
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == 87) {
            isMoveF = true;
        }
        if(e.getKeyCode() == 83) {
            isMoveB = true;
        }
        if(e.getKeyCode() == 81) {
            isMoveL = true;
        }
        if(e.getKeyCode() == 69) {
            isMoveR = true;
        }

        if(e.getKeyCode() == 65) {
            isRotateL = true;
        }
        if(e.getKeyCode() == 68) {
            isRotateR = true;
        }

        // L Shift
        if(e.getKeyCode() == 16) {
            Player.getInstance().sprint(true);
        }
        // Space
        if(e.getKeyCode() == 32) {

        }
        // R Ctrl
        if(e.getKeyCode() == 17) {

        }
    }

    // UP, DOWN, LEFT, RIGHT
    // 38, 40, 37, 39

    //  W,  S,  A,  D
    // 87, 83, 65, 68

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == 87) {
            isMoveF = false;
        }
        if(e.getKeyCode() == 83) {
            isMoveB = false;
        }
        if(e.getKeyCode() == 81) {
            isMoveL = false;
        }
        if(e.getKeyCode() == 69) {
            isMoveR = false;
        }

        if(e.getKeyCode() == 65) {
            isRotateL = false;
        }
        if(e.getKeyCode() == 68) {
            isRotateR = false;
        }

        // L Shift
        if(e.getKeyCode() == 16) {
            Player.getInstance().sprint(false);
        }
    }

    boolean isMoveF = false;
    boolean isMoveB = false;
    boolean isMoveL = false;
    boolean isMoveR = false;

    boolean isRotateL = false;
    boolean isRotateR = false;

    public void start() {
        update();
    }

    void update(){
        java.util.Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                int dir = 0;

                if (isRotateL) Player.getInstance().rotate(-1);
                if (isRotateR) Player.getInstance().rotate(1);

                if(isMoveB) dir = 180;
                if(isMoveL) dir = 270;
                if(isMoveR) dir = 90;
                if(isMoveF && isMoveL) dir = 315;
                if(isMoveF && isMoveR) dir = 45;
                if(isMoveB && isMoveL) dir = 225;
                if(isMoveB && isMoveR) dir = 135;

                if((isMoveF || isMoveB || isMoveL || isMoveR) &&
                        !(isMoveF && isMoveB) &&
                        !(isMoveL && isMoveR)
                )
                    Player.getInstance().move(dir);

                /*
                for (int c = 0; c < World.getInstance().getCreatures().size(); c++) {
                    if(World.getInstance().getCreatures().get(c).distToPlayer() < 10) {
                        World.getInstance().getCreatures().get(c).move();
                    }
                }*/

                repaint();

            }
        }, 0, 1000/60);
    }
}
