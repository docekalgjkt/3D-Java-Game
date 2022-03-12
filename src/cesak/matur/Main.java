package cesak.matur;

public class Main
{

    public static void main(String[] args)
    {
        //Game.getInstance();
        //Window.getInstance().start();

        LevelManager.getInstance().setUp();
        Player.getInstance().setUp();
        Player.getInstance().getMyWeapon().addAmmunition(1, 10);
        Player.getInstance().getMyWeapon().addAmmunition(2, 50);
        GameManager.getInstance().start();
    }
}
