package com.company;

import java.awt.*;

public class Creature {

    private double x, y;
    private double angle = 0;
    private double speed = 12;

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
        this.img = Toolkit.getDefaultToolkit().getImage(img);
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

        angle = Math.atan(yDif / xDif) * 180 / Math.PI + ((xDif < 0) ? 180 : 0);

        double nextX = Math.round((x + Math.cos(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;
        double nextY = Math.round((y + Math.sin(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;

        if(!Render.getInstance().getTile((int)Math.floor(nextY), (int)Math.floor(nextX)).equals("#")) {
            x = nextX;
            y = nextY;
        }
        else {
            if(!Render.getInstance().getTile((int)Math.floor(y), (int)Math.floor(nextX)).equals("#")) {
                x = nextX;
            }
            if(!Render.getInstance().getTile((int)Math.floor(nextY), (int)Math.floor(x)).equals("#")) {
                y = nextY;
            }
        }

        if((int)Math.floor(x) != mapX || (int)Math.floor(y) != mapY) {
            Render.getInstance().setTile((int)Math.floor(y), (int)Math.floor(x), "c");
            Render.getInstance().setTile(mapY, mapX, ".");
            mapX = (int)Math.floor(x);
            mapY = (int)Math.floor(y);
        }
    }
}
