package com.cesak;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame
{
    //region Singleton

    private static Window window = new Window();

    private Window()
    {
    }

    public static Window getInstance()
    {
        return window;
    }

    //endregion


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

        CardLayout layout = new CardLayout();

        setLayout(layout);

        layout.addLayoutComponent(view, "View");

        setVisible(true);
    }

    private final JPanel menu = new JPanel()
    {
        public void paintComponent(Graphics g)
        {

        }
    };

    private final JPanel view = new JPanel()
    {
        public void paintComponent(Graphics g)
        {

        }
    };
}
