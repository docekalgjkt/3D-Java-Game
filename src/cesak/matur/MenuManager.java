package cesak.matur;

public class MenuManager
{
    // region Singleton

    private static final MenuManager MENU_MANAGER = new MenuManager();

    private MenuManager()
    {

    }

    public static MenuManager getInstance()
    {
        return MENU_MANAGER;
    }

    // endregion

    public void onStartGame()
    {
        SceneManager.getInstance().newGame();
    }
}
