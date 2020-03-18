package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Guest {
    private String name;
    private String title;
    private Sprite visitorSprite;
    private String[] dialogue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
