package com.cesak;

import cesak.matur.Player;

/**
 * Type of Object triggering an effect when the player collides with it
 */
public class Pickable extends Object
{
    public enum Bonus
    {
        HEAL, MANA
    }

    private Bonus bonus;

    public Pickable(String img, double x, double y, double size, double yPos, double hitbox, Bonus bonus)
    {
        super(img, x, y, size, yPos, hitbox);

        this.bonus = bonus;
    }

    /**
     * Method called when this Object is "Picked Up"
     */
    public void pick()
    {
        switch (bonus)
        {
            case HEAL -> {
                if (Player.getInstance().getHealthPercent() < 1) Player.getInstance().getHeal(2);
                else return;
            }
            case MANA -> {
                if (Player.getInstance().getManaPercent() < 1) Player.getInstance().getMana(10);
                else return;
            }
        }

        World.getInstance().destroyPickable(this);
    }
}
