package com.company;

public class Main
{

    public static void main(String[] args)
    {
        Game.getInstance();
    }

    public static double angleDist(double angle0, double angle1)
    { // -5, 5
        /*if (angle0 > 360) angle0 -= 360;
        else if (angle0 < 0) angle0 += 360;
        if (angle1 > 360) angle1 -= 360;
        else if (angle1 < 0) angle1 += 360;*/

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
