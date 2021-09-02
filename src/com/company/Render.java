package com.company;

import java.awt.*;

public class Render {

    private static Render render = null;

    public static Render getInstance() {
        if (render == null) {
            render = new Render();
        }
        return render;
    }

    //    -Y
    // -X [ ] X
    //     Y
    private final int[][] map = new int[][] {
            {1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 1, 1},
            {1, 0, 0, 1, 0, 1, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 1, 1},
            {1, 0, 0, 0, 1, 1, 1},
            {1, 0, 0, 0, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1}
    };

    private final String[] map0 = new String[] {
            ".....",
            ".....",
            ".....",
            "...#.",
            ".....",
    };

    public int[][] getMap() {
        return map;
    }

    public int[][] map() {


        return null;
    }

    private final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;

    public void renderWorld() {

        double[] walls = new double[screenWidth];

        double fov = 90;

        for (int x = 0; x < screenWidth; x++) {

            double rayAngle = (Player.getInstance().getAngle() - (fov / 2.0)) + (((double)x / (double) screenWidth) * fov);

            boolean hitWall = false;
            double rayDistance = 0;

            while (!hitWall) {
                rayDistance += 0.1;

                int rayX = (int)Math.floor(Player.getInstance().getX() + (Math.cos(rayAngle / 180 * Math.PI) * rayDistance));
                int rayY = (int)Math.floor(Player.getInstance().getY() + (Math.sin(rayAngle / 180 * Math.PI) * rayDistance));

                if(rayX < 0 || rayY < 0 || rayY >= map.length || rayX >= map[0].length) {
                    hitWall = true;
                }
                else if(map[rayY][rayX] == 1) {
                    hitWall = true;

                    if(rayDistance <= Player.getInstance().getCamDistance()) {
                        walls[x] = rayDistance;
                    }
                }
            }
        }

        this.walls = walls;
    }

    private double[] walls;

    public double[] getWalls() {
        renderWorld();
        return walls;
    }
}
