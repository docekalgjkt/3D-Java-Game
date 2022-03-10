package cesak.matur;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerWeapon
{
    private List<Weapon> weapons = new ArrayList<>();

    // ---

    private boolean[] weaponOwned;

    private int[] ammunition;

    private int equipped;

    // ---

    public void setUp()
    {
        getWeapons();
    }

    private void getWeapons()
    {
        ResFileReader rfr = new ResFileReader();

        int i = 0;

        List<String> list = rfr.getFile("weapons/Weapon" + i + ".txt");

        while (list != null)
        {
            assignNewWeapon(i, list);
            i++;
            list = rfr.getFile("weapons/Weapon" + i + ".txt");
        }
    }

    private void assignNewWeapon(int id, List<String> textLines)
    {
        int attackPower = Integer.parseInt(textLines.get(1));
        int ammunitionType = Integer.parseInt(textLines.get(2));

        System.out.println(attackPower);
        System.out.println(ammunitionType);

        Weapon newWeapon = new Weapon(id, attackPower, ammunitionType);
        weapons.add(newWeapon);
    }
}
