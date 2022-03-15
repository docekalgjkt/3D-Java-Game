package cesak.matur;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PlayerWeapon
{
    /**
     * List containing all weapons
     */
    private final List<Weapon> weapons = new ArrayList<>();

    // ---

    /**
     * Boolean array which holds information about what weapons you have
     */
    private final boolean[] weaponOwned;

    /**
     * Integer array which holds your amount of ammunition of each types
     */
    private final int[] ammunition;

    /**
     * Index of the weapon you're holding at the moment
     */
    private int equipped;

    /**
     * Amount of time that has passed after last shot
     */
    private double timePassed;

    /**
     * How long will the shoot animation stay after the shot
     */
    private final double shootAnimDelay = 0.2;

    // ---

    public BufferedImage getWeaponAnimation()
    {
        if (timePassed < shootAnimDelay)
        {
            return weapons.get(equipped).getImageAttack();
        }
        else
        {
            return weapons.get(equipped).getImageDefault();
        }
    }

    public int getCurrentAmmunition()
    {
        return ammunition[equipped];
    }

    // ---

    public PlayerWeapon()
    {
        getWeapons();

        weaponOwned = new boolean[weapons.size()];
        ammunition = new int[weapons.size()];

        weaponOwned[0] = true;
        weaponOwned[1] = true;

        equipped = 1;

        timePassed = shootAnimDelay * (1.0 / weapons.get(equipped).getFireRate());
    }

    /**
     * Loads weapons from files to weapons list
     */
    private void getWeapons()
    {
        ResFileReader rfr = new ResFileReader();

        int i = 0;

        List<String> list = rfr.getFile("weapons/weapon" + i + "/weapon.txt");

        while (list != null)
        {
            assignNewWeapon(list);
            i++;
            list = rfr.getFile("weapons/weapon" + i + "/weapon.txt");
        }
    }

    /**
     * Creates new Weapon object which is assigned to the weapon list
     *
     * @param textLines List of Strings holding the information of the weapon we want to create and asign
     */
    private void assignNewWeapon(List<String> textLines)
    {
        int attackPower = 0;
        int ammunitionType = 0;
        double fireRate = 0;

        for (String string : textLines)
        {
            String[] line = string.replaceAll(" ", "").split(":");

            switch (line[0])
            {
                case "damage" -> attackPower = Integer.parseInt(line[1]);
                case "ammo" -> ammunitionType = Integer.parseInt(line[1]);
                case "fireRate" -> fireRate = Double.parseDouble(line[1]);
            }
        }

        Weapon newWeapon = new Weapon(weapons.size(), attackPower, ammunitionType, fireRate);
        weapons.add(newWeapon);
    }

    // ---

    /**
     * Checks whether the conditions for an attack are met and if so, the player will perform an attack
     */
    public void attack()
    {
        if (timePassed < shootAnimDelay || timePassed < 1.0 / weapons.get(equipped).getFireRate())
        {
            timePassed += 1.0 / GameManager.getInstance().frameRate();
        }

        if (equipped > 0 && ammunition[equipped] == 0)
            return;

        // Checks if the weapon is ready to perform an attack
        if (!shouldShoot())
            return;

        // If the player is not attacking then the attack will not be performed
        if (!GameController.getInstance().isAttacking())
            return;

        performAttack();
    }

    /**
     * @return Has passed enough time for player to be able to attack
     */
    private boolean shouldShoot()
    {
        return (timePassed >= 1.0 / weapons.get(equipped).getFireRate());
    }

    /**
     * Perform an attack that hit an enemy if there is an enemy standing in fornt of the player
     */
    private void performAttack()
    {
        timePassed = 0;
        if (equipped > 0) ammunition[equipped]--;

        // Sorts enemies by the distance from the player
        List<Enemy> enemies = new ArrayList<>(LevelManager.getInstance().getEnemies());
        for (int i = 1; i < enemies.size(); i++)
        {
            for (int i1 = 0; i1 < i; i1++)
            {
                if (enemies.get(i).distToPlayer() < enemies.get(i1).distToPlayer())
                {
                    enemies.add(i1, enemies.get(i));
                    enemies.remove(i + 1);
                }
            }
        }

        // Cycles through the enemies and damaging the nearest enemy standing in front of the player if there is such an enemy
        for (Enemy enemy : enemies)
        {
            if (enemy.isDead())
                continue;

            double screenX = enemy.getScreenX();
            double size = 1.0 / enemy.distToPlayerTan() * enemy.getSize();
            double distFromMiddleX = Math.abs(screenX - 0.5);

            if (distFromMiddleX <= size / 2)
            {
                if (screenX >= 0 && screenX < 1)
                    if (Renderer.getInstance().getWalls()[(int) (Renderer.getInstance().getWalls().length * screenX)] < Math.sqrt(enemy.distToPlayer()))
                        continue;

                enemy.takeDamage(weapons.get(equipped).getDamage());
                break;
            }
        }

        SoundManager.getInstance().playSound("fire");
    }

    /**
     * If the player owns the weapon with index w he/she will equip it
     *
     * @param w Index of the weapon we want to equip
     */
    public void equipWeapon(int w)
    {
        if (timePassed < shootAnimDelay)
            return;

        if (w >= weapons.size())
            return;

        if (weaponOwned[w])
            equipped = w;
    }

    /**
     * Gives the player the access to a new weapon
     */
    public void addWeapon(int index)
    {
        weaponOwned[index] = true;
    }

    /**
     * Adds certain amount of ammunition to specific type of ammunition
     *
     * @param type   Type of the ammunition we want to add
     * @param amount Amount of the ammunition we want to add
     */
    public void addAmmunition(int type, int amount)
    {
        if (type < ammunition.length)
            ammunition[type] += amount;
    }
}