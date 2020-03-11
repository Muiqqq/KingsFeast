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
    private Array<Texture> allTextures;
    private Array<Texture> foodTextures;
    private Array<Texture> visitorTextures;

    LevelBuilder(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }

    // TODO: FIX THIS ALEKSI!!!
    void createTextureArrays() {
        allTextures = new Array<>();
        foodTextures = new Array<>();
        visitorTextures = new Array<>();

        // this badboy slaps them in alphabetical order automatically! keep in mind.
        allTextures = kingsFeast.getAssetManager().getAll(Texture.class, allTextures);
        System.out.println("amount of loaded textures: " + allTextures.size);

        // filenames are important -> a naming convention should be set up
        for (int i = 0; i < allTextures.size; i++) {
            if (allTextures.get(i).toString().contains("food")) {
                foodTextures.add(allTextures.get(i));
            }
            if (allTextures.get(i).toString().contains("goal")) {
                visitorTextures.add(allTextures.get(i));
            }
        }

        System.out.println(foodTextures.get(0));
        System.out.println(visitorTextures.get(0));
    }

    Array<LevelData> buildLevels() {
        Array<LevelData> levels = new Array<>();
        levels.add(level1());
        levels.add(level2());
        levels.add(level3());
        return levels;
    }

    private LevelData level1() {
        LevelData level = new LevelData();
        level.setTiledMap("level1.tmx");
        level.setSlingAnchorPos();
        return level;
    }

    private LevelData level2() {
        LevelData level = new LevelData();
        level.setTiledMap("level2.tmx");
        level.setSlingAnchorPos();
        return level;
    }

    private LevelData level3() {
        LevelData level = new LevelData();
        level.setTiledMap("level3.tmx");
        level.setSlingAnchorPos();
        return level;
    }

    /*
    private void createLevelTextureArrays(Array<Texture> visitorTextures, Array<Texture> foodTextures) {

    }
     */
}
