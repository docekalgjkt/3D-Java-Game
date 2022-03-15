package cesak.matur;

import java.util.List;

/**
 * Class which defines the Player
 */
public class Player
{
    // region Singleton

    private static final Player player = new Player();

    private Player()
    {

    }

    public static Player getInstance()
    {
        return player;
    }

    // endregion

    private int health = 50;
    private final int healthMax = 50;
    private final double speed = 30.0;
    private double x;
    private double y;
    private double angle = 0;
    private final double hitbox = 0.2;

    private PlayerWeapon myWeapon;

    // -----

    public double getHealthPercent()
    {
        return (double) health / healthMax;
    }

    public int getHealth()
    {
        return health;
    }

    public int getHealthMax()
    {
        return healthMax;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getAngle()
    {
        return angle;
    }

    public double getCamDistance()
    {
        return 10.0;
    }

    public double getNearClip()
    {
        double nearClip = 0.3;
        return nearClip * nearClip;
    }

    public PlayerWeapon getMyWeapon()
    {
        return myWeapon;
    }

    // -----

    public void setHealth(int h)
    {
        health = h;
    }

    // -----

    public void setUp()
    {
        health = healthMax;
        myWeapon = new PlayerWeapon();
    }

    /**
     * Tells the player to perform a move
     */
    public void move()
    {
        int dir = 0;

        if (GameController.getInstance().isMoveB()) dir = 180;
        if (GameController.getInstance().isMoveL()) dir = 270;
        if (GameController.getInstance().isMoveR()) dir = 90;
        if (GameController.getInstance().isMoveF() && GameController.getInstance().isMoveL()) dir = 315;
        if (GameController.getInstance().isMoveF() && GameController.getInstance().isMoveR()) dir = 45;
        if (GameController.getInstance().isMoveB() && GameController.getInstance().isMoveL()) dir = 225;
        if (GameController.getInstance().isMoveB() && GameController.getInstance().isMoveR()) dir = 135;

        if ((GameController.getInstance().isMoveF() || GameController.getInstance().isMoveB() || GameController.getInstance().isMoveL() || GameController.getInstance().isMoveR()) &&
                !(GameController.getInstance().isMoveF() && GameController.getInstance().isMoveB()) &&
                !(GameController.getInstance().isMoveL() && GameController.getInstance().isMoveR())
        )
            performMovement(dir);
    }

    /**
     * Tells the player to perform a rotation
     */
    public void rotate()
    {
        double dir = 0;

        if (GameController.getInstance().isRotateR()) dir = 1;
        if (GameController.getInstance().isRotateL()) dir = -1;

        if (dir != 0) performRotation(dir);
    }

    /**
     * Moves the player in X and Z axis
     *
     * @param a Angle which represents the direction in which the player will move
     */
    public void performMovement(int a)
    {
        double nextX = x + Math.cos((angle + a) / 180.0 * Math.PI) * (speed/* * modSpeed()*/ / (600));
        double nextY = y + Math.sin((angle + a) / 180.0 * Math.PI) * (speed/* * modSpeed()*/ / (600));

        x = nextX;
        y = nextY;

        //if (1 < 2) return;

        if (Collision.hitWall(x, y, hitbox))
        {
            if (x != Collision.getNextX()) x = Collision.getNextX();
            if (y != Collision.getNextY()) y = Collision.getNextY();
        }
        if (Collision.hitObject(x, y, hitbox))
        {
            if (x != Collision.getNextX()) x = Collision.getNextX();
            if (y != Collision.getNextY()) y = Collision.getNextY();
        }

        if (Collision.hitPickable(x, y, hitbox))
        {
            if (Collision.hitPickables.size() > 0)
            {
                for (int i = Collision.hitPickables.size() - 1; i >= 0; i--)
                {
                    Collision.hitPickables.get(i).pick();
                }
            }
        }
    }

    /**
     * Rotates the player around Y axis (left and right)
     *
     * @param dir Direction and also speed which the player will rotate with
     */
    public void performRotation(double dir)
    {
        angle += 2.5 * dir;
        if (angle >= 360) angle -= 360;
        else if (angle < 0) angle += 360;
    }

    /**
     * Makes the player attack if possible
     */
    public void attack()
    {
        myWeapon.attack();
    }

    /**
     * Sets the player position
     */
    public void setPosition(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void setRotation(double a)
    {
        angle = a;
    }

    public void interact()
    {
        // Coordinates of the point in front of you
        int nextX = (int) (x + (Math.cos(angle * Math.PI / 180) * 0.7));
        int nextY = (int) (y + (Math.sin(angle * Math.PI / 180) * 0.7));

        // Checking for the Level End
        if (nextX == LevelManager.getInstance().getLevelEnd()[0] && nextY == LevelManager.getInstance().getLevelEnd()[1])
        {
            LevelManager.getInstance().EndLevel();
            return;
        }

        // Interacting with doors
        List<Door> doors = LevelManager.getInstance().getDoors();
        for (Door door : doors)
        {
            if (door.getX() == nextX && door.getY() == nextY)
            {
                door.interact();
                break;
            }
        }
    }

    /**
     * Instant health gain for the player
     *
     * @param h Amount of health the player will get
     */
    public void getHeal(int h)
    {
        health += h;
        if (health > healthMax)
            health = healthMax;
    }

    /**
     * Damages the player
     *
     * @param dmg Amount of health the player will loose
     */
    public void takeDamage(int dmg)
    {
        health -= dmg;
        SoundManager.getInstance().playSound("hit");

        if (health <= 0)
        {
            die();
        }
    }

    /**
     * Method called when the player dies
     */
    private void die()
    {
        health = 0;
        GameController.getInstance().reset();
        GameManager.getInstance().stop();
        SceneManager.getInstance().setScene(0);
    }
}
