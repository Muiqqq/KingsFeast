package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;

public class KingsFeast extends Game {
    // TODO: Add documentation everywhere.

    @Override
    public void create () {
        setScreen(new MainMenuScreen(this));
    }
}
