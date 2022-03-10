package cesak.matur;

public class Weapon
{
    // Index of the weapon used to pick this weapon by it
    private int id;

    // Amount of damage this weapon deals
    private int attackPower;
    // Type of ammunition this weapon uses
    private int ammunitionType;

    // --- Getters ---

    public int getId()
    {
        return id;
    }

    public int getAttackPower()
    {
        return attackPower;
    }

    public int getAmmunitionType()
    {
        return ammunitionType;
    }

    // ---

    public Weapon(int _id, int _attackPower, int _ammunitionType)
    {
        id = _id;
        attackPower = _attackPower;
        ammunitionType = _ammunitionType;
    }
}
