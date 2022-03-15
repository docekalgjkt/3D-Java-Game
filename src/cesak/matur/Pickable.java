package cesak.matur;

import java.util.List;

/**
 * Type of LevelObject triggering an effect when the player collides with it
 */
public class Pickable extends LevelObject
{
    public enum Bonus
    {
        HEAL, GUN
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
     * Method called when this LevelObject is "Picked Up"
     */
    public void pick()
    {
        switch (bonus)
        {
            case HEAL -> {
                if (Player.getInstance().getHealthPercent() < 1) Player.getInstance().getHeal(2);
                else return;
            }
            case GUN -> {
                Player.getInstance().getMyWeapon().addWeapon(type);
                Player.getInstance().getMyWeapon().addAmmunition(type, amount);
            }
        }

        SoundManager.getInstance().playSound("pickup");
        LevelManager.getInstance().destroyPickable(this);
    }
}
