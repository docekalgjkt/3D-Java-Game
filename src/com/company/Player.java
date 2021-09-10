package com.company;

import java.util.List;

public class Player {

    // region Singleton

    private static Player player = null;

    public static Player getInstance() {
        if (player == null) {
            player = new Player();
        }
        return player;
    }

    // endregion


    private double x = 5.5, y = 1.5;
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    private final double speed = 35.0;

    private double modSpeed() {
        double res = 1;

        res *= (sprinting) ? 1.75 : 1;

        return res;
    }

    private boolean sprinting = false;

    private double angle = 0;
    public double getAngle() {
        return angle;
    }

    public double getCamDistance() {
        return 20.0;
    }

    public void move(int a) {

        double nextX = Math.round((x + Math.cos((angle + a) / 180.0 * Math.PI) * (speed * modSpeed() / 1000.0)) * 1000) / 1000.0;
        double nextY = Math.round((y + Math.sin((angle + a) / 180.0 * Math.PI) * (speed * modSpeed() / 1000.0)) * 1000) / 1000.0;

        boolean hitWallX = false;
        boolean hitWallY = false;

        double hitBoxRange = 0.2;
        for (int xx = 0; xx < 2; xx++) {
            if(World.getInstance().getTile((int)Math.floor(y), (int)Math.floor(x + Math.cos((180.0 * xx) / 180.0 * Math.PI) * hitBoxRange)).equals("#")) {
                if(Main.angleDist(angle + a, (180.0 * xx)) < 90) hitWallX = true;
            }
        }
        for (int yy = 0; yy < 2; yy++) {
            if(World.getInstance().getTile((int)Math.floor(y + Math.sin((90 + (180.0 * yy)) / 180.0 * Math.PI) * hitBoxRange), (int)Math.floor(x)).equals("#")) {
                if(Main.angleDist(angle + a, (90 + (180.0 * yy))) < 90) hitWallY = true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if(World.getInstance().getTile((int)Math.floor(y + Math.sin((double)(45 + (i * 90)) / 180.0 * Math.PI) * hitBoxRange), (int)Math.floor(x + Math.cos((double)(45 + (i * 90)) / 180.0 * Math.PI) * hitBoxRange)).equals("#")) {
                if(Main.angleDist(angle + a, 45 + (i * 90)) < 45) {
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
    }

    public void sprint(boolean b) {
        sprinting = b;
    }

    public void rotate(double dir) {
        angle += 2.5 * dir;
        if(angle >= 360) angle -= 360;
        else if (angle < 0) angle += 360;
    }
}
