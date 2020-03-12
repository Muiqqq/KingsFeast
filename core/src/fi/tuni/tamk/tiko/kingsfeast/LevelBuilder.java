package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

/**
 * Create new levels here.
 * Levels contain a different tiled map and other level specific information.
 * This class builds an array containing all the data for all the levels in the game.
 * New levels have to be added by hand.
 *
 *
 * This exists so a different Screen isn't needed for every single level.
 *
 * TODO: more documentation
 *  - Also there has to be a better way to do this. AssetManager?
 *  - Randomizing might be simplest code wise? least amount of manual labour.
 *  - How tho?
 */
class LevelBuilder {
    private final KingsFeast kingsFeast;

    LevelBuilder(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }

    Array<LevelData> buildLevels() {
        Array<LevelData> levels = new Array<>();
        levels.add(level1());
        levels.add(level2());
        levels.add(level3());
        return levels;
    }

    // thiese could be simplified, but in case there's actually going to be more info to
    // be saved in LevelData, keep these as is.
    private LevelData level1() {
        LevelData level = new LevelData("level1.tmx");
        return level;
    }

    private LevelData level2() {
        LevelData level = new LevelData("level2.tmx");
        return level;
    }

    private LevelData level3() {
        LevelData level = new LevelData("level3.tmx");
        return level;
    }

}
