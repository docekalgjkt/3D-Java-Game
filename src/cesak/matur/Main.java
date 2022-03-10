package cesak.matur;

public class Main
{

    public static void main(String[] args)
    {
        //Game.getInstance();
        //Window.getInstance().start();

        LevelManager.getInstance().setUp();
        GameManager.getInstance().start();

        //PlayerWeapon pw = new PlayerWeapon();
        //pw.setUp();
    }
}
