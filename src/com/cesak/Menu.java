package com.cesak;

import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame
{

    // region Singleton

    private static Menu menu = null;

    public static Menu getInstance()
    {
        if (menu == null)
        {
            menu = new Menu();
        }
        return menu;
    }

    // endregion

    public Menu()
    {
        setTitle("Program");
        setSize(Toolkit.getDefaultToolkit().getScreenSize().width / 5 * 4, Toolkit.getDefaultToolkit().getScreenSize().height / 5 * 4);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setVisible(true);
    }

}
