package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
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

    JButton button = new JButton();

    public Game(){
        setTitle("Program");
        setSize(Toolkit.getDefaultToolkit().getScreenSize().width / 5 * 4, Toolkit.getDefaultToolkit().getScreenSize().height / 5 * 4);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setUndecorated(true);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.update();
        World.getInstance().setUp();

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

        // Ceiling
        g.setColor(Color.getHSBColor(0, 0, 0.1f)); // 0, 0, 0.1f
        g.fillRect(0, 0, getSize().width, getSize().height / 2);

        // Floor
        g.setColor(Color.getHSBColor(0, 0, 0.2f)); // 0, 0, 0.2f
        g.fillRect(0, getSize().height / 2, getSize().width, getSize().height / 2);

        for (int i = 0; i < what.size(); i++) {
            switch (what.get(i)) {
                case "wall": {
                    if(walls[indexes.get(i)] == 0) continue;

                    int lineHeight = (int)((getSize().height) / walls[indexes.get(i)]);
                    //float shade = 1 - ((float)walls[i] * 2 / (float) Player.getInstance().getCamDistance());
                    float shade = 1 - (Math.round(walls[indexes.get(i)]) / 15.0f);

                    int y0 = (getSize().height / 2) - (lineHeight / 2);
                    int x0 = (int)Math.floor((double)World.getInstance().getWTex().getWidth() * wallTexCol.get(order[indexes.get(i)]));

                    int p = World.getInstance().getWTex().getHeight();
                    double h = (double)lineHeight / p;

                    for (int w = 0; w < p; w++) {
                        int y1 = (int)Math.floor(y0 + (h * (double)w));

                        int pixel = World.getInstance().getWTex().getRGB(x0, w);
                        Color color = new Color(pixel, true);

                        g.setColor(color);
                        g.fillRect(order[indexes.get(i)], y1, 1, (int)Math.floor(h + 1));
                    }

                    //g.setColor(Color.getHSBColor(0, 0, shade < 0 ? 0 : shade));
                    //g.fillRect(order[indexes.get(i)], getSize().height / 2 - (lineHeight / 2), 1, lineHeight);

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
                    ImageIO.read(getClass().getClassLoader().getResource("images/Wooden-Bow2x.png")),
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
        if(e.getKeyCode() == 37) {
            isRotateL = true;
        }
        if(e.getKeyCode() == 39) {
            isRotateR = true;
        }

        if(e.getKeyCode() == 65) {
            isMoveL = true;
        }
        if(e.getKeyCode() == 68) {
            isMoveR = true;
        }

        // L Shift
        if(e.getKeyCode() == 16) {
            Player.getInstance().sprint(true);
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
        if(e.getKeyCode() == 37) {
            isRotateL = false;
        }
        if(e.getKeyCode() == 39) {
            isRotateR = false;
        }

        if(e.getKeyCode() == 65) {
            isMoveL = false;
        }
        if(e.getKeyCode() == 68) {
            isMoveR = false;
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

    void update(){
        java.util.Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (isRotateL) Player.getInstance().rotate(-1);
                if (isRotateR) Player.getInstance().rotate(1);

                int dir = 0;

                if(isMoveB) dir = 180;
                if(isMoveL) dir = 270;
                if(isMoveR) dir = 90;
                if(isMoveF && isMoveL) dir = 315;
                if(isMoveF && isMoveR) dir = 45;
                if(isMoveB && isMoveL) dir = 225;
                if(isMoveB && isMoveR) dir = 135;

                if((isMoveF || isMoveB || isMoveL || isMoveR) && !(isMoveF && isMoveB) && !(isMoveL && isMoveR))
                    Player.getInstance().move(dir);

                for (int c = 0; c < World.getInstance().creatures0.size(); c++) {
                    if(Render.getInstance().getCreats().contains(World.getInstance().creatures0.get(c))) World.getInstance().creatures0.get(c).move();
                }

                repaint();

            }
        }, 10, 1000/60);
    }
}
