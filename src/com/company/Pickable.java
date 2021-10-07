package com.company;

public class Pickable extends Object
{
    public enum Bonus
    {
        HEAL
    }

    private Bonus bonus;

    public Pickable(double x, double y, double size, double yPos, double hitbox, Bonus bonus, String img)
    {
        super(x, y, size, yPos, hitbox, img);

        this.bonus = bonus;
    }

    public void pick()
    {
        switch (bonus)
        {
            case HEAL -> {
                if (Player.getInstance().getHealthPercent() < 1) Player.getInstance().getHeal(10);
                else return;
            }
        }

        World.getInstance().destroyPickable(this);
    }
}
