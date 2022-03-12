package cesak.matur;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Weapon
{
    /**
     * Amount of damage this weapon deals
     */
    private final int damage;

    /**
     * Type of ammunition this weapon uses
     */
    private final int ammunitionType;

    /**
     * Rate at which the weapon is attacking/shooting
     */
    private final double fireRate;

    private BufferedImage imageDefault;
    private BufferedImage imageAttack;

    // --- Getters ---

    public int getDamage()
    {
        return damage;
    }

    public int getAmmunitionType()
    {
        return ammunitionType;
    }

    public double getFireRate()
    {
        return fireRate;
    }

    public BufferedImage getImageDefault()
    {
        return imageDefault;
    }

    public BufferedImage getImageAttack()
    {
        return imageAttack;
    }

    // ---

    public Weapon(int id, int _attackPower, int _ammunitionType, double _fireRate)
    {
        damage = _attackPower;
        ammunitionType = _ammunitionType;
        fireRate = _fireRate;

        try
        {
            imageDefault = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("weapons/weapon" + id + "/def.png")));
            imageAttack = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("weapons/weapon" + id + "/attack.png")));
        } catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }
}
