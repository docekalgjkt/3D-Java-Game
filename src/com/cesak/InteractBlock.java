package com.cesak;

import cesak.matur.Player;

/**
 * An Object the player is able to interact with
 * <br></br>
 * After interaction, it will do something
 */
public class InteractBlock
{
    public enum Effect
    {
        MOVE, OPEN, WALL
    }

    private Effect effect;

    private int x, y;

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    java.lang.Object[] params;

    public InteractBlock(int x, int y, Effect effect, java.lang.Object[] params)
    {
        this.x = x;
        this.y = y;

        this.effect = effect;
        this.params = params;
    }

    public void interact()
    {
        switch (effect)
        {
            case MOVE -> Player.getInstance().setPosition((double) params[0], (double) params[1]);
            case OPEN -> World.getInstance().setTile((int) params[1], (int) params[0], ".");
            case WALL -> World.getInstance().setTile((int) params[1], (int) params[0], "#");
        }
    }
}
