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
        HEAL
    }

    private Bonus bonus;

    // ---

    public Pickable(int id, int x, int y)
    {
        super(id, x, y, "pickables/pickable");

        ResFileReader rfr = new ResFileReader();
        List<String> list = rfr.getFile("pickables/pickable" + id + "/pickable.txt");

        switch (list.get(0))
        {
            case "HEAL" -> bonus = Bonus.HEAL;
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
        }

        LevelManager.getInstance().destroyPickable(this);
    }
}
