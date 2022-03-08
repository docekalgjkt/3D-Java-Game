package cesak.matur;

import cesak.matur.GameManager;

public class Main
{

    public static void main(String[] args)
    {
        //Game.getInstance();
        //Window.getInstance().start();

        LevelManager.getInstance().setUp();
        GameManager.getInstance().start();
    }
}
