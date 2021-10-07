package com.company;

public class Pickable extends Object
{
    public enum Bonus
    {
        HEAL, MAGIC
    }

    private Bonus bonus;

    public Pickable(String img, double x, double y, double size, double yPos, double hitbox, Bonus bonus)
    {
        super(img, x, y, size, yPos, hitbox);

        this.bonus = bonus;
    }

    public void pick()
    {
        switch (bonus)
        {
            case HEAL -> {
                if (Player.getInstance().getHealthPercent() < 1) Player.getInstance().getHeal(2);
                else return;
            }
            case MAGIC -> {
                if (Player.getInstance().getMagicPercent() < 1) Player.getInstance().getMagic(10);
                else return;
            }
        }

        World.getInstance().destroyPickable(this);
    }
}
