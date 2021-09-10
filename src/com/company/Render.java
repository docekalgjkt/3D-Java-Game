package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Render {

    // region Singleton

    private static Render render = null;

    public static Render getInstance() {
        if (render == null) {
            render = new Render();
        }
        return render;
    }

    // endregion

    private final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width / Game.getInstance().getScale();

    public void renderWorld() {

        what = new ArrayList<>();
        indexes = new ArrayList<>();

        creats = new ArrayList<>();
        cDists = new ArrayList<>();
        cXPos = new ArrayList<>();

        wallTexCol = new ArrayList<>();

        List<String> wallType = new ArrayList<>();

        double[] walls = new double[screenWidth];

        double fov = 90;
        double step = 0.05;

        for (int x = 0; x < screenWidth; x++) {

            double screenHalf = Math.tan((fov / 2) / 180 * Math.PI);
            double seg = screenHalf / (screenWidth / 2.0);

            double a = Math.atan(-Math.tan((fov / 2) / 180 * Math.PI) + (seg * x)) * 180 / Math.PI;
            double rayAngle = Player.getInstance().getAngle() + a;

            boolean hitWall = false;
            double rayDistance = 0;
            double wallDistance = 0;

            int prevX = -1, prevY = -1;

            // Ray pos increment by step by step (NOT GOOD)
            while (!hitWall && wallDistance <= Player.getInstance().getCamDistance()) {
                rayDistance += step;

                int rayX = (int)Math.floor(Player.getInstance().getX() + (Math.cos(rayAngle / 180 * Math.PI) * rayDistance));
                int rayY = (int)Math.floor(Player.getInstance().getY() + (Math.sin(rayAngle / 180 * Math.PI) * rayDistance));

                if(rayX == prevX && rayY == prevY) continue;

                wallDistance = Math.cos((rayAngle - Player.getInstance().getAngle()) / 180 * Math.PI) * rayDistance;

                if(rayX < 0 || rayY < 0 || rayY >= World.getInstance().getMap().length || rayX >= World.getInstance().getMap()[rayY].length()) {
                    hitWall = true;

                    walls[x] = 0;
                    wallTexCol.add(0.0);
                    wallType.add("out");
                }
                else {
                    // Entities
                    for (int xx = -1; xx <= 1; xx++) {
                        for (int yy = -1; yy <= 1; yy++) {
                            if(rayY + yy < 0 || rayX + xx < 0 || rayY + yy >= World.getInstance().getMap().length || rayX + xx >= World.getInstance().getMap()[rayY + yy].length()) continue;

                            if(World.getInstance().getTile(rayY + yy, rayX + xx).equals("c")) {
                                List<Creature> c = new ArrayList<>();
                                for (int i = 0; i < World.getInstance().getCreatures().size(); i++) {
                                    if(World.getInstance().getCreatures().get(i).isOnTile(rayX + xx, rayY + yy)) {
                                        c.add(World.getInstance().getCreatures().get(i));
                                    }
                                }
                                for (int i = 0; i < c.size(); i++) {
                                    if(!creats.contains(c.get(i))) {
                                        double[] cpos = c.get(i).getPos();
                                        cpos[0] -= Player.getInstance().getX();
                                        cpos[1] -= Player.getInstance().getY();

                                        double angle = (Math.atan(cpos[1] / cpos[0]) * 180 / Math.PI) + ((cpos[0] < 0) ? 180 : 0);

                                        if(Main.angleDist(angle, Player.getInstance().getAngle()) >= 90) continue;

                                        //double playerAngle = Player.getInstance().getAngle();
                                        /*if(angle - playerAngle < -180 || angle - playerAngle > 180) {
                                            if(playerAngle > 180) playerAngle -= 360;
                                            else playerAngle += 360;
                                        }*/
                                        double angleDif = (angle - Player.getInstance().getAngle());

                                        int xPos = (int)Math.floor((Math.tan((fov / 2) / 180 * Math.PI) + Math.tan(angleDif / 180 * Math.PI)) * (screenWidth * Game.getInstance().getScale()) / 2);

                                        double dist = cpos[0] / Math.cos(angle / 180 * Math.PI);

                                        int where = cDists.size();

                                        for (int i1 = 0; i1 < cDists.size(); i1++) {
                                            if(dist > cDists.get(i1)) {
                                                where = i1;
                                            }
                                        }

                                        creats.add(where, c.get(i));
                                        cDists.add(where, dist);
                                        cXPos.add(where, xPos);
                                    }
                                }
                            }
                        }
                    }


                    String s = World.getInstance().getTile(rayY, rayX);
                    // Walls
                    if(s.equals("#") || s.equals("+")) {
                        hitWall = true;

                        rayDistance -= step;

                        double smallStep = 50;

                        while((int)Math.floor(Player.getInstance().getX() + (Math.cos(rayAngle / 180 * Math.PI) * rayDistance)) != rayX ||
                                (int)Math.floor(Player.getInstance().getY() + (Math.sin(rayAngle / 180 * Math.PI) * rayDistance)) != rayY) {
                            rayDistance += step / smallStep;
                            if(World.getInstance().getTile((int)Math.floor(Player.getInstance().getY() + (Math.sin(rayAngle / 180 * Math.PI) * rayDistance)), (int)Math.floor(Player.getInstance().getX() + (Math.cos(rayAngle / 180 * Math.PI) * rayDistance))).equals("#")) {
                                rayY = (int)Math.floor(Player.getInstance().getY() + (Math.sin(rayAngle / 180 * Math.PI) * rayDistance));
                                rayX = (int)Math.floor(Player.getInstance().getX() + (Math.cos(rayAngle / 180 * Math.PI) * rayDistance));
                                break;
                            }
                        }

                        wallDistance = Math.cos((rayAngle - Player.getInstance().getAngle()) / 180.0 * Math.PI) * rayDistance;

                        walls[x] = wallDistance;

                        int xDif = prevX - rayX;
                        int yDif = prevY - rayY;

                        double xPos = Player.getInstance().getX() + (Math.cos(rayAngle / 180.0 * Math.PI) * rayDistance);
                        double yPos = Player.getInstance().getY() + (Math.sin(rayAngle / 180.0 * Math.PI) * rayDistance);

                        double col = 0.0;

                        if(xDif < 0) {
                            col = yPos - rayY;
                        }
                        else if(xDif > 0) {
                            col = 1.0 - (yPos - rayY);
                        }
                        else if(yDif < 0) {
                            col = 1.0 - (xPos - rayX);
                        }
                        else if(yDif > 0) {
                            col = xPos - rayX;
                        }
                        wallTexCol.add(Math.abs(col));
                        wallType.add((s.equals("#") ? "wall" : "door"));
                    }
                }

                prevX = rayX;
                prevY = rayY;
            }

            if(!hitWall && wallDistance > Player.getInstance().getCamDistance()) {
                walls[x] = 0;
                wallTexCol.add(0.0);
                wallType.add("far");
            }
        }

        List<Double> walls0 = new ArrayList<>(); // 8, 4, 5, 2, 3
        List<Integer> order0 = new ArrayList<>();

        for(int w = 0; w < walls.length; w++) {
            int where = 0;
            for(int i = 0; i < walls0.size(); i++) {
                where = i;
                if(walls[w] >= walls0.get(i)) {
                    break;
                }
            }
            walls0.add(where, walls[w]); // 4, 5, 8
            order0.add(where, w); // 1, 2, 0

            String s = wallType.get(w);
            wallType.remove(w);
            wallType.add(where, s);
        }

        double[] walls1 = new double[walls.length];
        order = new int[walls.length];

        List<Creature> renderedCreatures = new ArrayList<>();

        for (int i = 0; i < walls1.length; i++) {
            walls1[i] = walls0.get(i);
            order[i] = order0.get(i);

            for(int c = 0; c < creats.size(); c++) {
                if(cDists.get(c) > walls1[i]) {
                    if(!renderedCreatures.contains(creats.get(c))) {
                        what.add("creature");
                        indexes.add(c);
                        renderedCreatures.add(creats.get(c));
                    }
                }
            }

            what.add(wallType.get(i));
            indexes.add(i);
        }

        for(int c = 0; c < creats.size(); c++) {
            if(!renderedCreatures.contains(creats.get(c))) {
                what.add("creature");
                indexes.add(c);
                renderedCreatures.add(creats.get(c));
            }
        }

        this.walls = walls1;
    }

    public void render() {

        double fov = 90;
        double step = 0.05;

        what = new ArrayList<>();
        indexes = new ArrayList<>();

        creats = new ArrayList<>();
        cDists = new ArrayList<>();
        cXPos = new ArrayList<>();

        wallTexCol = new ArrayList<>();

        List<String> wallType = new ArrayList<>();

        double[] walls = new double[screenWidth];

        for (int x = 0; x < screenWidth; x++) {

            double screenHalf = Math.tan((fov / 2) / 180 * Math.PI);
            double seg = screenHalf / (screenWidth / 2.0);

            double a = Math.atan(-Math.tan((fov / 2) / 180 * Math.PI) + (seg * x)) * 180 / Math.PI;
            double rayAngle = Player.getInstance().getAngle() + a;

            boolean hitWall = false;

            double wallDistance = 0;

            double yPos = Player.getInstance().getY();
            double xPos = Player.getInstance().getX();

            while(!hitWall && wallDistance <= Player.getInstance().getCamDistance()) {

            }
        }
    }

    private List<String> what = new ArrayList<>();
    private List<Integer> indexes = new ArrayList<>();

    private List<Creature> creats = new ArrayList<>();
    private List<Double> cDists = new ArrayList<>();
    private List<Integer> cXPos = new ArrayList<>();

    private List<Double> wallTexCol = new ArrayList<>();

    public List<String> getWhat() {
        return what;
    }
    public List<Integer> getIndexes() {
        return indexes;
    }
    public List<Creature> getCreats() {
        return creats;
    }
    public List<Double> getCDists() {
        return cDists;
    }
    public List<Integer> getCXPos() {
        return cXPos;
    }
    public List<Double> getWallTexCol() {
        return wallTexCol;
    }

    private double[] walls;
    private int[] order;

    public int[] getOrder() {
        return order;
    }

    public double[] getWalls() {
        renderWorld();
        return walls;
    }
}
