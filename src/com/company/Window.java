package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Window extends JFrame implements KeyListener {

    private Graphics g;

    public Window() {
        setTitle("3D-rendering");
        //setSize(800, 800);
        setSize(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
        setBackground(Color.black);
        addKeyListener(this);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.update();
    }

    public void paint(Graphics g) {
        this.g = g;
        walls = Render.getInstance().getWalls();
        g.clearRect(0, 0, getSize().width, getSize().height);

        // Floor
        g.setColor(Color.getHSBColor(0, 0, 0.2f));
        g.fillRect(0, getSize().height / 2, getSize().width, getSize().height / 2);

        for (int i = 0; i < getSize().width; i++) {
            if(walls[i] == 0) continue;
            int lineHeight = (int)((getSize().height) / walls[i]);
            //float shade = 1 - ((float)walls[i] * 2 / (float) Player.getInstance().getCamDistance());
            float shade = 1 - (Math.round(walls[i]) / 10.0f);
            g.setColor(Color.getHSBColor(0, 0, shade < 0 ? 0 : shade));
            g.fillRect(i, getSize().height / 2 - (lineHeight / 2), 1, lineHeight);
        }

        g.setColor(Color.blue);
        g.drawString(String.valueOf(Player.getInstance().getX()), 50, 50);
        g.drawString(String.valueOf(Player.getInstance().getY()), 100, 50);
        g.drawString(String.valueOf(Player.getInstance().getAngle()), 50, 75);
    }

    private double[] walls;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == 38) {
            isMoveF = true;
            moveF = 0;
        }
        if(e.getKeyCode() == 40) {
            isMoveF = true;
            moveF = 180;
        }
        if(e.getKeyCode() == 37) {
            isRotate = true;
            rotate = -1;
        }
        if(e.getKeyCode() == 39) {
            isRotate = true;
            rotate = 1;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == 38) {
            isMoveF = false;
        }
        if(e.getKeyCode() == 40) {
            isMoveF = false;
        }
        if(e.getKeyCode() == 37) {
            isRotate = false;
        }
        if(e.getKeyCode() == 39) {
            isRotate = false;
        }
    }

    boolean isMoveF = false;
    int moveF = 0;
    boolean isMoveS = false;
    int moveS = 0;

    boolean isRotate = false;
    int rotate = 0;

    void update() {
        java.util.Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isMoveF) Player.getInstance().move(moveF);
                if (isMoveS) Player.getInstance().move(moveS);
                if (isRotate) Player.getInstance().rotate(rotate);
                repaint();
            }
        }, 10, 1000/60);
    }
}
