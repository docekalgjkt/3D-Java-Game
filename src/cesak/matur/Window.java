package cesak.matur;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class where is GUI built
 */
public class Window extends JFrame
{
    // region Singleton

    private static final Window WINDOW = new Window();

    private Window()
    {

    }

    public static Window getInstance()
    {
        return WINDOW;
    }

    // endregion

    private Container pane;

    private CardLayout layout;

    /**
     * Width and Height of the window in pixels
     */
    private final int screenWidth = 800, screenHeight = 800;

    private final int scale = 6;

    // --- Rendering Game View

    /**
     * Width and Height of the scaled view in pixels
     * <br></br>
     * e.g. When scale is equal to 2, this scaledWidth and scaledHeight will be half the window size
     */
    private int scaledWidth, scaledHeight;

    double[] walls;
    double[] texs;
    String[] what;

    // ---

    public void start()
    {
        addKeyListener(MenuController.getInstance());
        addKeyListener(GameController.getInstance());
        buildGUI();
    }

    public void switchScene(String sceneName)
    {
        layout.show(pane, sceneName);
    }

    private void buildGUI()
    {
        setTitle("3D Java Game");
        setSize(screenWidth, screenHeight);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Centering the Game Window
        setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width / 2 - getWidth() / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getHeight() / 2
                   );

        pane = getContentPane();

        layout = new CardLayout();
        pane.setLayout(layout);
        layout.setHgap(0);
        layout.setVgap(0);

        pane.add(menu);
        pane.add(game);

        layout.addLayoutComponent(menu, "Menu");
        layout.addLayoutComponent(game, "Game");

        layout.first(pane);

        setUndecorated(true);
        setVisible(true);
    }

    /**
     * Panel which represents Main Menu Scene
     */
    private final JPanel menu = new JPanel()
    {
        public void paintComponent(Graphics g)
        {
            g.setColor(Color.gray);
            g.fillRect(0, 0, screenWidth, screenHeight);

            g.setColor(Color.white);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 120));
            g.drawString("3D GAME", screenWidth / 2 - 275, 200);

            g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 30));
            g.drawString("Press Enter to Play", screenWidth / 2 - 150, screenHeight / 2);

            g.setColor(Color.darkGray);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
            g.drawString("Controls:", screenWidth / 2 - 90, screenHeight / 2 + 90);

            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
            g.drawString("Movement: W S A D", screenWidth / 2 - 170, screenHeight / 2 + 130);
            g.drawString("Rotation: Left and Right Arrows", screenWidth / 2 - 170, screenHeight / 2 + 160);
            g.drawString("Shooting: Space", screenWidth / 2 - 170, screenHeight / 2 + 190);
            g.drawString("Interact: E", screenWidth / 2 - 170, screenHeight / 2 + 220);
            g.drawString("Quit Game: Escape", screenWidth / 2 - 170, screenHeight / 2 + 250);
        }
    };

    /**
     * Panel which represents In-Game Scene
     */
    private final JPanel game = new JPanel()
    {
        public void paintComponent(Graphics g)
        {
            if (walls == null)
            {
                redraw();
                return;
            }

            // Ceiling
            g.setColor(new Color(60, 60, 60)); // 0, 0, 0.1f
            g.fillRect(0, 0, screenWidth, screenHeight / 2);

            // Floors
            g.setColor(new Color(125, 125, 125));
            g.fillRect(0, screenHeight / 2, screenWidth, screenHeight / 2);

            //region Walls
            for (int i = 0; i < walls.length; i++)
            {
                if (walls[i] == 0) continue;

                // Calculating how high should currently rendered column be
                int lineHeight = (int) (scaledHeight / walls[i] / 1.9);

                // Making distant walls darker
                float shade = 1 - (Math.round(walls[i]) / (float) Player.getInstance().getCamDistance());
                shade = (shade < 0) ? 0 : shade;

                // y-position of the top-first pixel of the currently rendered column
                int colTopPixelY = (scaledHeight / 2) - (lineHeight / 2);

                // Index of the column in the texture of the wall which was hit
                int textureColumnIndex/* = (int) Math.floor((double) LevelManager.getInstance().getTex(what[i]).getWidth() * texs[i])*/;

                int wallType = Integer.parseInt(what[i]);
                textureColumnIndex = (int) Math.floor((double) LevelManager.getInstance().getTexture(wallType).getWidth() * texs[i]);

                // Height of the texture which is about to render
                int textureHeight/* = LevelManager.getInstance().getTex(what[i]).getHeight()*/;

                textureHeight = LevelManager.getInstance().getTexture(wallType).getHeight();

                // Ratio of lineHeight and textureHeight
                double ratio0 = (double) lineHeight / textureHeight;

                // If the texture has more rows than the column pixels this value will be the ratio1 of it
                // This is used to not cycle texture rows that will be not rendered due to the currently rendered column lesser pixel count
                double ratio1 = (textureHeight > lineHeight) ? (double) textureHeight / lineHeight : 1;

                // Y-position of the last pixel we filled/drew
                // This value is here to prevent overdrawing pixels that already has a color
                int prevY = -1;

                // Cycling through a texture column to draw particular color into selected pixel
                for (int currentTextureRow = 0; currentTextureRow < textureHeight / ratio1; currentTextureRow++)
                {
                    // Offset of the player view point to make it look like the player character actually has a height
                    int playerEyes = 2;

                    // Y-position of the pixel we are about to assign a color to
                    int pixelY = (int) (colTopPixelY + (ratio0 * (int) (currentTextureRow * ratio1 + playerEyes)));

                    // Moves to the next pixel, because this we know this one has already been colored (thanks to the prevY)
                    if (currentTextureRow != 0 && pixelY == prevY)
                    {
                        pixelY++;
                    }

                    // Assigning prevY value
                    prevY = pixelY;

                    // Getting the number of pixels that are going to be colored with particular color
                    // Used if rendered column has more pixels than the texture's rows
                    // Just to speed up the process, otherwise there could be more pixels gaining color from the same texture pixel
                    int ySize = (int) Math.floor(ratio0 + 1) * scale;


                    if (pixelY < 0)
                    {
                        if (-pixelY >= (int) Math.floor(ratio0 + 1))
                        {
                            continue;
                        }
                        else if (-pixelY < (int) Math.floor(ratio0 + 1))
                        {
                            ySize += pixelY;
                            pixelY = 0;
                        }
                    }
                    else if ((pixelY * scale) >= scaledHeight * scale)
                    {
                        break;
                    }
                    else if ((pixelY * scale) + ySize >= scaledHeight * scale)
                    {
                        ySize = (scaledHeight * scale) - (pixelY * scale) - 1;
                    }

                    if (textureColumnIndex == LevelManager.getInstance().getTexture(wallType).getWidth())
                    {
                        textureColumnIndex--;
                    }

                    // Getting the color of particular texture pixel
                    int pixel = LevelManager.getInstance().getTexture(wallType).getRGB(textureColumnIndex, (int) Math.floor(currentTextureRow * ratio1));

                    Color color = new Color(pixel, false);

                    // Getting darker shades of the color
                    Color shadedColor = new Color(((float) color.getRed() / 255) * shade, ((float) color.getGreen() / 255) * shade, ((float) color.getBlue() / 255) * shade);

                    // Filling selected pixels with the gotten color
                    g.setColor(shadedColor);
                    g.fillRect(i * scale, pixelY * scale, scale, ySize);
                }
            }
            //endregion

            //region Objects
            List<LevelObject> levelObjects = new ArrayList<>();

            levelObjects.addAll(LevelManager.getInstance().getObjects());
            levelObjects.addAll(LevelManager.getInstance().getEnemies());
            levelObjects.addAll(LevelManager.getInstance().getPickables());

            // Sorting levelObjects (by distance)
            for (int i = 1; i < levelObjects.size(); i++)
            {
                for (int i1 = 0; i1 < i; i1++)
                {
                    if (levelObjects.get(i).distToPlayer() > levelObjects.get(i1).distToPlayer())
                    {
                        levelObjects.add(i1, levelObjects.get(i));
                        levelObjects.remove(i + 1);
                    }
                }
            }

            // Rendering levelObjects
            for (LevelObject levelObject : levelObjects)
            {
                if (levelObject.distToPlayer() <= Player.getInstance().getNearClip())
                    continue;

                int size = (int) ((scaledHeight / (levelObject.distToPlayerTan())) * levelObject.getSize());
                int sizeX = (int) (((scaledWidth / (levelObject.distToPlayerTan()))) * levelObject.getSize());

                for (int x = 0; x < sizeX; x++)
                {
                    int posX = -(sizeX / 2) + x + (int) (levelObject.getScreenX() * (scaledWidth));
                    double yPos = (0.0 + levelObject.getScreenY())/* - ((1.0 / levelObject.getSize()) * 0.5)*/;
                    int y0 = (scaledHeight / 2) - (int) (size * (yPos));

                    if (posX < 0 || posX >= walls.length || (walls[posX] < levelObject.distToPlayerTan() && walls[posX] != 0))
                        continue;

                    int p = levelObject.getMyImage().getHeight();
                    double h = (double) size / p;

                    double ratio = (p > size) ? (double) p / size : 1;

                    int prevY = -1;

                    for (int y = 0; y < p / ratio; y++)
                    {
                        int y1 = (int) Math.floor(y0 + (h * (int) Math.floor(y * ratio + 2)));

                        if (y != 0 && y1 == prevY)
                        {
                            y1++;
                        }

                        prevY = y1;

                        int ySize = (int) Math.floor(h + 1) * scale;

                        if (y1 < 0)
                        {
                            if (-y1 >= (int) Math.floor(h + 1))
                            {
                                continue;
                            }
                            else if (-y1 < (int) Math.floor(h + 1))
                            {
                                ySize += y1;
                                y1 = 0;
                            }
                        }
                        else if ((y1 * scale) >= scaledHeight * scale)
                        {
                            break;
                        }
                        else if ((y1 * scale) + ySize >= scaledHeight * scale)
                        {
                            ySize = (scaledHeight * scale) - (y1 * scale) - 1;
                        }

                        double imgW = levelObject.getMyImage().getWidth() * ((double) x / sizeX);

                        int pixel = levelObject.getMyImage().getRGB((int) imgW, (int) Math.floor(y * ratio));
                        if (pixel == 0) continue;

                        float shade = 1 - (Math.round(levelObject.distToPlayerTan()) / (float) Player.getInstance().getCamDistance());
                        shade = (shade < 0) ? 0 : shade;

                        Color color = new Color(pixel, false);
                        Color shadedColor = new Color(((float) color.getRed() / 255) * shade, ((float) color.getGreen() / 255) * shade, ((float) color.getBlue() / 255) * shade);

                        g.setColor(shadedColor);
                        g.fillRect(posX * scale, y1 * scale, scale, ySize);
                    }
                }
            }

            //endregion

            // --- UI ---

            // Cursor

            g.setColor(Color.white);
            g.fillRect(scaledWidth * scale / 2, ((scaledHeight * scale) - 1) / 2, 2, 4);
            g.fillRect(scaledWidth * scale / 2 - 1, (scaledHeight * scale) / 2, 1, 2);
            g.fillRect(scaledWidth * scale / 2 + 2, (scaledHeight * scale) / 2, 1, 2);

            // Weapon

            int size = 500;
            g.drawImage(Player.getInstance().getMyWeapon().getWeaponAnimation(), (screenWidth - size) / 2, (screenHeight - size), size, size, null);

            // Health

            g.setColor(Color.red);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
            g.drawString("HEALTH", screenWidth - 190, screenHeight - 25);

            g.setColor(Color.red);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
            g.drawString(String.valueOf(Player.getInstance().getHealth()), screenWidth - 100, screenHeight - 25);

            // Ammo

            g.setColor(Color.blue);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
            g.drawString("AMMO", 190, screenHeight - 25);

            g.setColor(Color.blue);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
            g.drawString(String.valueOf(Player.getInstance().getMyWeapon().getCurrentAmmunition()), 100, screenHeight - 25);
        }
    };

    public void redraw()
    {
        int whichScene = SceneManager.getInstance().getCurrentScene();

        switch (whichScene)
        {
            case 1 -> redrawGame();
        }
    }

    private void redrawGame()
    {
        scaledHeight = screenHeight / scale;
        scaledWidth = screenWidth / scale;

        Renderer.getInstance().render(scaledWidth);

        walls = Renderer.getInstance().getWalls();
        texs = Renderer.getInstance().getTexs();
        what = Renderer.getInstance().getWhat();

        game.repaint();
    }
}
