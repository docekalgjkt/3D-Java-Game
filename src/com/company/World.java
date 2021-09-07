package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class World {

    // region Singleton

    private static World world = null;

    public static World getInstance() {
        if (world == null) {
            world = new World();
        }
        return world;
    }

    // endregion

    private String[] defaultWallTexs = new String[] {
            "WallDefault0"
    };

    private BufferedImage wTex;

    public BufferedImage getWTex() {
        return wTex;
    }

    //    -Y
    // -X [ ] X
    //     Y
    private final String[] map = new String[] {
            //       0    5    10   15   20   25   30   35   40
            "##########################################", // 0
            "#...#.......#...##....##.......###########",
            "#...#.###.#.#......##.....#.#..###########",
            "#.....#.#.#.....##.#####...............###",
            "#######.#.########.#...#..#.#..##.####.###",
            "#...#..............#...#.......##.####.###", // 5
            "#...#...........##.##.#####.#####.##.....#",
            "#...#..#######..##...........#######..#..#",
            "##.##..#.....#..##.#########.#.......###.#",
            "##.##.....#.....##.####......#.#.###..#..#",
            "#...............##.#......####.#.###.....#", // 10
            "#.#####.#.###.####.#.##...####.#.#########",
            "#...#...#.......##.#.#######...#.#########",
            "#...#...#.#..#..##.#.##...##.###.#########",
            "#...#####.#..#..##.#.##......##...########",
            "#.........#..........##...####.....#######", // 15
            "#############.###############.......######",
            "#...........................##.....#######",
            "#...........................###...########",
            "#...........................####.#########",
            "#...........................##############", // 20
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "#...........................##############",
            "##########################################",
    };//     0    5    10   15   20   25   30   35   40
    public String[] getMap() {
        return map;
    }
    public String getTile(int y, int x) {
        return String.valueOf(map[y].toCharArray()[x]);
    }
    public void setTile(int y, int x, String what) {
        StringBuilder sb = new StringBuilder(map[y]);
        sb.replace(x, x + 1, what);
        map[y] = sb.toString();
    }

    void setUp(){
        /*
        for (int i = 0; i < creatures.length; i++) {
            System.out.println((int)Math.floor(creatures[i].getPos()[1]) + ", " + (int)Math.floor(creatures[i].getPos()[0]));
            System.out.println(getTile((int)Math.floor(creatures[i].getPos()[1]), (int)Math.floor(creatures[i].getPos()[0])));
            setTile((int)Math.floor(creatures[i].getPos()[1]), (int)Math.floor(creatures[i].getPos()[0]), "c");
            System.out.println(getTile((int)Math.floor(creatures[i].getPos()[1]), (int)Math.floor(creatures[i].getPos()[0])));
            System.out.println(creatures[i].getPos()[1] + " + " + creatures[i].getPos()[0]);
        }*/

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length(); x++) {
                boolean stored = true;
                if(getTile(y, x).equals("c")) {
                    stored = false;
                    for (int i = 0; i < creatures0.size(); i++) {
                        if(creatures0.get(i).isOnTile(x, y)) {
                            stored = true;
                            break;
                        }
                    }
                }

                if(!stored) {
                    creatures0.add(new Creature(x + 0.5, y + 0.5, "Beholder"));
                }
            }
        }

        try {
            wTex = ImageIO.read(getClass().getClassLoader().getResource("images/WallDefault0.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Creature> creatures0 = new ArrayList<>(); // {{}}

    public void creatureMove(int y0, int x0, int y1, int x1) {
        setTile(y1, x1, "c");
        boolean canRemove = true;
        for (int i = 0; i < creatures0.size(); i++) {
            if(creatures0.get(i).isOnTile(x0, y0)) {
                canRemove = false;
                break;
            }
        }
        if(canRemove) setTile(y0, x0, ".");
    }
}
