package com.cesak;

import javax.swing.*;
import java.awt.*;

/**
 * Class where is GUI built
 */
public class Window extends JFrame
{
    //region Singleton

    private static final Window window = new Window();

    private Window()
    {
    }

    public static Window getInstance()
    {
        return window;
    }

    //endregion

    private Container pane;

    public void start()
    {
        buildGUI();
    }

    private void buildGUI()
    {
        setTitle("3D Java Game");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width / 2 - getWidth() / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getHeight() / 2
        );

        pane = getContentPane();

        CardLayout cd = new CardLayout();
        pane.setLayout(cd);

        pane.add(menu);
        pane.add(game);

        cd.addLayoutComponent(menu, "Menu");
        cd.addLayoutComponent(game, "Game");

        setVisible(true);
    }

    /**
     * Panel which represents Main Menu Scene
     */
    private JPanel menu = new JPanel()
    {
        public void paintComponent(Graphics g)
        {
            g.setColor(Color.red);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    };

    /**
     * Panel which represents In-Game Scene
     */
    private JPanel game = new JPanel()
    {
        public void paintComponent(Graphics g)
        {
            g.setColor(Color.green);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    };
}
