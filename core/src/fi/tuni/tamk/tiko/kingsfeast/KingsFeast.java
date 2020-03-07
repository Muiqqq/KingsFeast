package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;

public class KingsFeast extends Game {
    // TODO: Add documentation everywhere.
    //  -add dispose() when necessary
    //  -make levels last longer than one throw -> implement placeholder textures and stuff?

    // contains every level's leveldata.
    // also keeping track of current level. used to iterate levels array.
    private Array<LevelData> levels;
    private int currentLevel;

    @Override
    public void create () {
        currentLevel = 0;
        levels = LevelBuilder.buildLevels();
        setScreen(new MainMenuScreen(this));
    }

    Array<LevelData> getLevels() {
        return levels;
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
