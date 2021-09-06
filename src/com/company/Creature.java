package com.company;

import java.awt.*;
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
        this.img = Toolkit.getDefaultToolkit().getImage("images\\" + img + ".png");
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
        }

        if((int)Math.floor(x) != mapX || (int)Math.floor(y) != mapY) {
            /*World.getInstance().setTile((int)Math.floor(y), (int)Math.floor(x), "c");
            World.getInstance().setTile(mapY, mapX, ".");*/
            World.getInstance().creatureMove(mapY, mapX, (int)Math.floor(y), (int)Math.floor(x));
            mapX = (int)Math.floor(x);
            mapY = (int)Math.floor(y);
        }
    }
}
