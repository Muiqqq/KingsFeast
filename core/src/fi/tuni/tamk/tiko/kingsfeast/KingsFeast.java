package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;

public class KingsFeast extends Game {
    // TODO: Add documentation everywhere.
    // TODO: Make camera follow the foodPlate, within a levels bounds.
    AppPreferences appPreferences;

    @Override
    public void create () {
        setScreen(new MainMenuScreen(this));
        appPreferences = new AppPreferences();
    }
}
