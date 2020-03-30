package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

public class KingsFeast extends Game {
    // TODO: Current issues:
    //  -Lack of documentation
    //  -Check that everything that needs disposing gets disposed when needed
    //  -Graphics for: UI, Game, buttons, HUD, backgrounds
    //  -All assets need to be changed to be loaded with assetManager
    //  -Move spriteBatch to KingsFeast so it can be used elsewhere
    //  -Menu buttons have incorrect size
    //  -Main menu needs a how to play button which leads to the written tutorial
    //  -Written tutorial needs to be made
    //  -Saves need to be made resettable, add a reset save button to main menu
    //  -OR Change start game to continue game and add a new game button
    //  -Settings toggle buttons' textures need to stick
    //  -Settings needs buttons for localization, also that needs to be implemented
    //  -Pause menu resets the level when unpausing
    //  -Settings returns only back to main menu, should be able to return to pause menu if
    //   settings was accessed from there
    //  -Feedback screen doesn't play well with different screen sizes
    //  -Buttons might be a bit too small for mobile in general?

    // remember to give an instance of 'this' to all new screens, if anything
    // from this class or parent class is to be used in that screen.

    private final AssetManager assetManager = new AssetManager();
    private LevelBuilder levelBuilder;
    private Array<LevelData> levels;
    private int currentLevel;
    private Preferences kfprefs;
    Music music;

    @Override
    public void create() {
        kfprefs = getPreferencesFromOS(kfprefs);
        initSaveState();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        levelBuilder = new LevelBuilder(this);
        currentLevel = kfprefs.getInteger("currentLevel");
        setMusic();
        setSounds();
        setScreen(new LoadingScreen(this, levelBuilder));
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    AssetManager getAssetManager() {
        return assetManager;
    }

    Array<LevelData> getLevels() {
        return levels;
    }

    void setLevels(Array<LevelData> levels) {
        this.levels = levels;
    }

    int getCurrentLevel() {
        return currentLevel;
    }

    void setCurrentLevel(int x) {
        currentLevel = x;
    }

    void incrementCurrentLevel() {
        kfprefs.putInteger("currentLevel", currentLevel + 1);
        kfprefs.flush();
        currentLevel = kfprefs.getInteger("currentLevel");
    }

    private Preferences getPreferencesFromOS(Preferences prefs) {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences("kfsettings");
        }
        return prefs;
    }

    Preferences getPrefs() {
        return kfprefs;
    }

    private void initSaveState() {
        if (!kfprefs.contains("doPrefsExist")) {
            kfprefs.putBoolean("doPrefsExist", true);
            kfprefs.putInteger("totalThrows", 0);
            kfprefs.putInteger("currentLevel", 0);
            kfprefs.putInteger("pollution", 50);
            kfprefs.flush();
        }
    }

    void clearSaveState() {
        kfprefs.remove("doPrefsExist");
        kfprefs.remove("totalThrows");
        kfprefs.remove("currentLevel");
        kfprefs.remove("pollution");
        kfprefs.flush();
        setCurrentLevel(kfprefs.getInteger("currentLevel"));
        initSaveState();
    }

    void setMusic() {
        music = Gdx.audio.newMusic(Gdx.files.internal("1.mp3"));
        music.setLooping(true);
        if(isMusicEnabled()) {
            music.play();
        }
    }

    void setSounds() {
        if(isSoundEffectsEnabled()) {
            // Play sounds
        }
    }

    public boolean isMusicEnabled() {
        return getPrefs().getBoolean("music.enabled", true);
    }

    public boolean isSoundEffectsEnabled() {
        return getPrefs().getBoolean("sound.enabled", true);
    }

    void saveGameOnLevelSwap() {
        if (getCurrentLevel() < getLevels().size - 1) {
            incrementCurrentLevel();
        } else {
            clearSaveState();
        }
    }
}
