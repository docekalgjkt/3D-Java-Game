package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class Creature {

    private double x, y;
    private double speed = 5; // 12

    private int xPos;

    public int getxPos() {
        return xPos;
    }
    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    private Image img;
    public Image getImg() {
        return img;
    }

    public Creature(double x, double y, String img) {
        this.x = x; this.y = y;
        mapX = (int)Math.floor(x);
        mapY = (int)Math.floor(y);
        try {
            this.img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/" + img + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        speed += new Random().nextDouble() * 2 - 1;
    }

    public boolean isOnTile(int x, int y) {
        return (int)Math.floor(this.x) == x && (int)Math.floor(this.y) == y;
    }

    public double[] getPos() {
        return new double[] {x, y};
    }
    public int mapX, mapY;

    public void move() {

        double xDif = Player.getInstance().getX() - x;
        double yDif = Player.getInstance().getY() - y;

        double angle = Math.atan(yDif / xDif) * 180 / Math.PI + ((xDif < 0) ? 180 : 0);

        double nextX = Math.round((x + Math.cos(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;
        double nextY = Math.round((y + Math.sin(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;
/*
        if(!World.getInstance().getTile((int)Math.floor(nextY), (int)Math.floor(nextX)).equals("#")) {
            x = nextX;
            y = nextY;
        }
        else {
            if(!World.getInstance().getTile((int)Math.floor(y), (int)Math.floor(nextX)).equals("#")) {
                x = nextX;
            }
            if(!World.getInstance().getTile((int)Math.floor(nextY), (int)Math.floor(x)).equals("#")) {
                y = nextY;
            }
        }*/

        boolean hitWallX = false;
        boolean hitWallY = false;

        double hitBoxRange = 0.2;
        for (int xx = 0; xx < 2; xx++) {
            if(World.getInstance().getTile((int)Math.floor(y), (int)Math.floor(x + Math.cos((180.0 * xx) / 180.0 * Math.PI) * hitBoxRange)).equals("#")) {
                if(Main.angleDist(angle, (180.0 * xx)) < 90) hitWallX = true;
            }
        }
        for (int yy = 0; yy < 2; yy++) {
            if(World.getInstance().getTile((int)Math.floor(y + Math.sin((90 + (180.0 * yy)) / 180.0 * Math.PI) * hitBoxRange), (int)Math.floor(x)).equals("#")) {
                if(Main.angleDist(angle, (90 + (180.0 * yy))) < 90) hitWallY = true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if(World.getInstance().getTile((int)Math.floor(y + Math.sin((double)(45 + (i * 90)) / 180.0 * Math.PI) * hitBoxRange), (int)Math.floor(x + Math.cos((double)(45 + (i * 90)) / 180.0 * Math.PI) * hitBoxRange)).equals("#")) {
                if(Main.angleDist(angle, 45 + (i * 90)) < 45) {
                    hitWallX = true;
                    hitWallY = true;
                }
            }
        }

        if(!hitWallX) {
            x = nextX;
        }
        if(!hitWallY) {
            y = nextY;
        }

        if((int)Math.floor(x) != mapX || (int)Math.floor(y) != mapY) {
            World.getInstance().creatureMove(mapY, mapX, (int)Math.floor(y), (int)Math.floor(x));
            mapX = (int)Math.floor(x);
            mapY = (int)Math.floor(y);
        }
    }

    public double distToPlayer() {
        return Math.abs(x - Player.getInstance().getX()) + Math.abs(y - Player.getInstance().getY());
    }
}
