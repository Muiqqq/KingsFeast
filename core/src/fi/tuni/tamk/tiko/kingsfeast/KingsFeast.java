package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
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

    @Override
    public void create() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        levelBuilder = new LevelBuilder(this);
        currentLevel = 2;
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
        currentLevel++;
    }
}
