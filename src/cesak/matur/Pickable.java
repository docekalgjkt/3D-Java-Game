package cesak.matur;

import cesak.matur.Player;
import cesak.matur.ResFileReader;
import cesak.matur.SceneObject;
import com.cesak.World;

import java.util.List;

/**
 * Type of SceneObject triggering an effect when the player collides with it
 */
public class Pickable extends SceneObject
{
    public enum Bonus
    {
        HEAL, AMMO, GUN
    }

    private Bonus bonus;

    private int type;
    private int amount;

    // ---

    public Pickable(int id, int x, int y)
    {
        super(id, x, y, "pickables/pickable");

        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("pickables/pickable" + id + "/pickable.txt");

        switch (list.get(0))
        {
            case "HEAL" -> bonus = Bonus.HEAL;
            case "AMMO" -> bonus = Bonus.AMMO;
            case "GUN" -> bonus = Bonus.GUN;
        }

        for (String s : list)
        {
            String[] line = s.replaceAll(" ", "").split(":");

            switch (line[0])
            {
                case "type" -> type = Integer.parseInt(line[1]);
                case "amount" -> amount = Integer.parseInt(line[1]);
                case "bonus" -> bonus = Bonus.valueOf(line[1]);
            }
        }
    }

    // ---

    /**
     * Method called when this SceneObject is "Picked Up"
     */
    public void pick()
    {
        switch (bonus)
        {
            case HEAL -> {
                if (Player.getInstance().getHealthPercent() < 1) Player.getInstance().getHeal(2);
                else return;
            }
            case AMMO -> Player.getInstance().getMyWeapon().addAmmunition(type, amount);
            case GUN -> {
                Player.getInstance().getMyWeapon().addWeapon(type);
                Player.getInstance().getMyWeapon().addAmmunition(type, amount);
            }
        }

        LevelManager.getInstance().destroyPickable(this);
    }
}
