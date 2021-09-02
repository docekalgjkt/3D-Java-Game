package com.company;

public class Player {

    private static Player player = null;

    public static Player getInstance() {
        if (player == null) {
            player = new Player();
        }
        return player;
    }

    private double x = 3.5, y = 3.5;
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    private double speed = 40;

    private double angle = 0;
    public double getAngle() {
        return angle;
    }

    private double camDistance = 10;
    public double getCamDistance() {
        return camDistance;
    }

    public void move(int a) {
        double nextX = Math.round((x + Math.cos((angle + a) / 180.0 * Math.PI) * (speed / 1000.0)) * 100) / 100.0;
        double nextY = Math.round((y + Math.sin((angle + a) / 180.0 * Math.PI) * (speed / 1000.0)) * 100) / 100.0;

        if(Render.getInstance().getMap()[(int)Math.floor(nextY)][(int)Math.floor(nextX)] == 0) {
            x = nextX;
            y = nextY;
        }
    }

    public void rotate(int dir) {
        angle += 2.5 * dir;
        if(angle >= 360) angle -= 360;
        else if (angle < 0) angle += 360;
    }
}
