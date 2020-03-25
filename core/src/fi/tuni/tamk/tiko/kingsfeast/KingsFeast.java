package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

public class KingsFeast extends Game {
    // TODO: Add documentation everywhere.
    //  -Check that everything that needs disposing gets disposed when needed!!!
    //  -texture drawing -> done for now. Refactor when needed.
    //  -loading screen is necessary with assetManager so make that -> done for now.
    //  -make sure all assets are loaded with assetManager and not by hand!
    //  -create as many level concepts using almost no game mechanics as possible
    //  -flesh out said levels
    //  -so many things

    // remember to give an instance of 'this' to all new screens, if anything
    // from this class or parent class is to be used in that screen.

    // contains every level's leveldata.
    // also keeping track of current level. used to iterate levels array.
    private final AssetManager assetManager = new AssetManager();
    private LevelBuilder levelBuilder;
    private Array<LevelData> levels;
    private int currentLevel;
    private Preferences kfprefs;

    @Override
    public void create() {
        kfprefs = getPreferences(kfprefs);
        initSaveState();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        levelBuilder = new LevelBuilder(this);
        currentLevel = kfprefs.getInteger("currentLevel");
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

    private Preferences getPreferences(Preferences kfprefs) {
        if (kfprefs == null) {
            kfprefs = Gdx.app.getPreferences("kfprefs");
        }
        return kfprefs;
    }

    private void initSaveState() {
        if (!kfprefs.contains("hasGameBeenSaved")) {
            kfprefs.putBoolean("hasGameBeenSaved", true);
            kfprefs.putInteger("throws", 0);
            kfprefs.putInteger("currentLevel", 0);
            kfprefs.putInteger("pollution", 50);
            kfprefs.flush();
        }
    }

    void clearSavestate() {
        kfprefs.remove("hasGameBeenSaved");
        kfprefs.remove("throws");
        kfprefs.remove("currentLevel");
        kfprefs.remove("pollution");
        initSaveState();
    }

    Preferences getPrefs() {
        return kfprefs;
    }
}
