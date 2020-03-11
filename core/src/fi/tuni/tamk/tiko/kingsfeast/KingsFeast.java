package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

public class KingsFeast extends Game {
    // TODO: Add documentation everywhere.
    //  -add dispose() when necessary
    //  -make levels last longer than one throw -> implement placeholder textures and stuff?
    //  -texture drawing
    //  -loading screen is necessary with assetManager so make that
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
    public void create () {
        levelBuilder = new LevelBuilder(this);
        levels = levelBuilder.buildLevels();
        currentLevel = 0;
        setScreen(new LoadingScreen(this));
    }

    AssetManager getAssetManager() {
        return assetManager;
    }

    Array<LevelData> getLevels() {
        return levels;
    }

    LevelBuilder getLevelBuilder() {
        return levelBuilder;
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
