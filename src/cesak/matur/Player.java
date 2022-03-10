package cesak.matur;

import com.cesak.Collision;
import com.cesak.InteractBlock;
import com.cesak.World;

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

    private int health = 15;
    private int healthMax = 15;
    private double speed = 20.0;
    private double x;
    private double y;
    private double angle = 0;
    private double nearClip = 0.3;
    private double hitbox = 0.2;

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
        return nearClip * nearClip;
    }

    // -----

    public void setHealth(int h)
    {
        health = h;
    }

    // -----

    public void setUp()
    {
        myWeapon = new PlayerWeapon();
    }

    /**
     * Tells the player to perform a move
     */
    public void move()
    {
        int dir = 0;

        if (Controller.getInstance().isMoveB()) dir = 180;
        if (Controller.getInstance().isMoveL()) dir = 270;
        if (Controller.getInstance().isMoveR()) dir = 90;
        if (Controller.getInstance().isMoveF() && Controller.getInstance().isMoveL()) dir = 315;
        if (Controller.getInstance().isMoveF() && Controller.getInstance().isMoveR()) dir = 45;
        if (Controller.getInstance().isMoveB() && Controller.getInstance().isMoveL()) dir = 225;
        if (Controller.getInstance().isMoveB() && Controller.getInstance().isMoveR()) dir = 135;

        if ((Controller.getInstance().isMoveF() || Controller.getInstance().isMoveB() || Controller.getInstance().isMoveL() || Controller.getInstance().isMoveR()) &&
                !(Controller.getInstance().isMoveF() && Controller.getInstance().isMoveB()) &&
                !(Controller.getInstance().isMoveL() && Controller.getInstance().isMoveR())
        )
            performMovement(dir);
    }

    /**
     * Tells the player to perform a rotation
     */
    public void rotate()
    {
        double dir = 0;

        if (Controller.getInstance().isRotateR()) dir = 0.9;
        if (Controller.getInstance().isRotateL()) dir = -0.9;

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
     * Sets the player position
     */
    public void setPosition(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    // TODO: Add reset() after death

    public void interact()
    {
        // Interacting with a Wall-like SceneObject
        int nextX = (int) (x + (Math.cos(angle * Math.PI / 180) * 0.7));
        int nextY = (int) (y + (Math.sin(angle * Math.PI / 180) * 0.7));

        List<InteractBlock> interactBlocks = World.getInstance().getDoors();
        for (int i = 0; i < interactBlocks.size(); i++)
        {
            if (interactBlocks.get(i).getX() == nextX && interactBlocks.get(i).getY() == nextY)
            {
                interactBlocks.get(i).interact();
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
        World.getInstance().reset();
    }
}
