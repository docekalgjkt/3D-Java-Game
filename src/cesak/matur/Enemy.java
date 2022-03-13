package cesak.matur;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Class defining individual living beings (for now hostile)
 */
public class Enemy extends SceneObject
{
    // Images for each enemy state
    private BufferedImage imgDefault;
    private BufferedImage imgAttack;
    private BufferedImage imgHit;
    private BufferedImage imgDead;
    private BufferedImage[] imgMove;

    private int health;
    private int damage;
    // Movement speed
    private double speed; // 12
    // How fast this enemy attacks
    private double attackSpeed;
    // At what distance will this enemy start attacking
    private double attackRange = 0.9;
    // What will drop from this enemy when killed
    private String loot;

    public double getAttackRange()
    {
        return attackRange;
    }

    public boolean isDead()
    {
        return health == 0;
    }

    // -----

    public Enemy(int id, int x, int y)
    {
        super(id, x, y, "enemies/enemy");

        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("enemies/enemy" + id + "/enemy.txt");

        for (String string : list)
        {
            String[] line = string.replaceAll(" ", "").split(":");

            switch (line[0])
            {
                case "health" -> health = Integer.parseInt(line[1]);
                case "damage" -> damage = Integer.parseInt(line[1]);
                case "speed" -> speed = Double.parseDouble(line[1]);
                case "attackSpeed" -> attackSpeed = Double.parseDouble(line[1]);
                case "attackRange" -> attackRange = Double.parseDouble(line[1]);
                case "loot" -> loot = line[1];
            }
        }

        try
        {
            imgDefault = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("enemies/enemy" + id + "/def.png")));
            imgHit = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("enemies/enemy" + id + "/images/hit.png")));
            imgDead = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("enemies/enemy" + id + "/images/dead.png")));
            imgAttack = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("enemies/enemy" + id + "/images/attack.png")));

            imgMove = new BufferedImage[2];
            for (int i = 0; i < imgMove.length; i++)
            {
                imgMove[i] = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("enemies/enemy" + id + "/images/move" + i + ".png")));
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // --- Movement ---

    private int moveImg;

    public void move()
    {
        if (isBeingHit || isAttacking) return;
        if (!isMoving)
        {
            animsOff();
            isMoving = true;
        }

        double x = getX();
        double y = getY();

        double xDif = Player.getInstance().getX() - x;
        double yDif = Player.getInstance().getY() - y;

        double angle = Math.atan(yDif / xDif) * 180 / Math.PI + ((xDif < 0) ? 180 : 0);
/*
        double nextX = Math.round((x + Math.cos(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;
        double nextY = Math.round((y + Math.sin(angle / 180.0 * Math.PI) * (speed / 1000.0)) * 1000) / 1000.0;*/

        double nextX = x + Math.cos(angle / 180.0 * Math.PI) * (speed / (600));
        double nextY = y + Math.sin(angle / 180.0 * Math.PI) * (speed / (600));

        setX(nextX);
        setY(nextY);

        if (Collision.hitWall(getX(), getY(), getHitbox()))
        {
            if (getX() != Collision.getNextX()) setX(Collision.getNextX());
            if (getY() != Collision.getNextY()) setY(Collision.getNextY());
        }
        if (Collision.hitObject(getX(), getY(), getHitbox()))
        {
            if (getX() != Collision.getNextX()) setX(Collision.getNextX());
            if (getY() != Collision.getNextY()) setY(Collision.getNextY());
        }
    }

    public boolean isMoving()
    {
        return isMoving;
    }

    public void stopMove()
    {
        frame = 0;
        isMoving = false;
        setMyImage(imgDefault);
    }

    // --- Attacking ---

    public void attack()
    {
        if (!isAttacking && !isBeingHit)
        {
            startAttack();
        }
    }

    private void startAttack()
    {
        animsOff();
        isAttacking = true;
    }

    private void performAttack()
    {
        if (distToPlayer() <= attackRange * attackRange)
        {
            Player.getInstance().takeDamage(damage);
        }
    }

    public void stopAttack()
    {
        frame = 0;
        isAttacking = false;
        setMyImage(imgDefault);
    }

    // --- Animating ---

    private boolean isMoving;
    private boolean isAttacking;
    private boolean isBeingHit;

    private int frame;

    public void anim()
    {
        if (isMoving)
        {
            if (frame == 30)
            {
                frame = 0;
                moveImg = (moveImg == 0) ? 1 : 0;
                setMyImage(imgMove[moveImg]);
            }
        }
        else if (isAttacking)
        {
            if (frame == (60 * attackSpeed))
            {
                setMyImage(imgAttack);
                performAttack();
            }
            else if (frame == (60 * attackSpeed) + 25)
            {
                stopAttack();
            }
        }
        else if (isBeingHit)
        {
            if (frame == 12)
            {
                frame = 0;
                isBeingHit = false;
                setMyImage(imgDefault);
            }
        }
        else
        {
            frame = 0;
            return;
        }

        frame++;
    }

    private void animsOff()
    {
        isMoving = false;
        isAttacking = false;
        isBeingHit = false;
    }

    // --- Health ---

    public void takeDamage(int dmg)
    {
        if (isDead())
            return;

        setMyImage(imgHit);
        animsOff();
        frame = 0;
        isBeingHit = true;

        health -= dmg;

        if (health <= 0)
        {
            die();
        }
    }

    private void die()
    {
        health = 0;
        //setYPos(0);
        setMyImage(imgDead);
    }
}
