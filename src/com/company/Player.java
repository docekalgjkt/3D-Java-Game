package com.company;

public class Player {

    private static Player player = null;

    public static Player getInstance() {
        if (player == null) {
            player = new Player();
        }
        return player;
    }

    private double x = 5.5, y = 1.5;
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    private final double speed = 50.0;

    private double modSpeed() {
        double res = 1;

        res *= (sprinting) ? 1.5 : 1;

        return res;
    }

    private boolean sprinting = false;

    private double angle = 45;
    public double getAngle() {
        return angle;
    }

    private final double camDistance = 10.0;
    public double getCamDistance() {
        return camDistance;
    }

    public void move(int a) {
        double nextX = Math.round((x + Math.cos((angle + a) / 180.0 * Math.PI) * (speed * modSpeed() / 1000.0)) * 1000) / 1000.0;
        double nextY = Math.round((y + Math.sin((angle + a) / 180.0 * Math.PI) * (speed * modSpeed() / 1000.0)) * 1000) / 1000.0;

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
    }

    public void rotate(double dir) {
        angle += 2.5 * dir;
        if(angle >= 360) angle -= 360;
        else if (angle < 0) angle += 360;
    }

    public void sprint(boolean b) {
        sprinting = b;
    }
}
