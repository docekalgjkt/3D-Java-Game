package com.company;

public class Main
{

    public static void main(String[] args)
    {
        Game.getInstance();
    }

    public static double angleDist(double angle0, double angle1)
    {
        double dist0 = Math.abs(angle0 - angle1);
        double dist1 = (dist0 > 180) ? 360 - dist0 : dist0 + 1;

        if (dist0 <= dist1)
        {
            return angle0 - angle1;
        }
        else
        {
            return dist1;
        }
    }
}
