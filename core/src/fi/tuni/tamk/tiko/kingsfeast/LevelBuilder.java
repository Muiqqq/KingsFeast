package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;


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
        levels.add(level4());
        return levels;
    }

    // These can be simplified?
    private LevelData level1() {
        return new LevelData(kingsFeast.getAssetManager().get("map1.tmx",
                TiledMap.class));
    }

    private LevelData level2() {
        return new LevelData(kingsFeast.getAssetManager().get("level2.tmx",
                TiledMap.class));
    }

    private LevelData level3() {
        return new LevelData(kingsFeast.getAssetManager().get("level3.tmx",
                TiledMap.class));
    }

    private LevelData level4() {
        return new LevelData(kingsFeast.getAssetManager().get("level4.tmx",
                TiledMap.class));
    }

}
