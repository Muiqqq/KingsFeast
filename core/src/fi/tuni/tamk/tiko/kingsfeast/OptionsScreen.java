package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class OptionsScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 480;
    private Texture backgroundTexture;
    private Stage stage;
    private final float BUTTON_WIDTH = 128f;
    private final float BUTTON_HEIGHT = 96f;

    private Texture okTexture;
    private Texture creditsTexture;
    private Texture musicOnTexture;
    private Texture musicOffTexture;

    // Sound Fx
    // BG Music
    // Credits
    // Language
    // Cancel

    // DOCUMENTATION

    public OptionsScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        stage.addActor(createBackgroundImage());
        stage.addActor(createCreditsButton());
        stage.addActor(createOkButton());
        stage.addActor((createMusicButton()));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    private Image createBackgroundImage() {
        backgroundTexture = new Texture("options.jpg");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private ImageButton createCreditsButton() {
        creditsTexture = new Texture("credits.png");
        ImageButton credits = new ImageButton(new TextureRegionDrawable(new TextureRegion(creditsTexture)));
        credits.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 4, Align.center);
        credits.setSize(150f, 75f);
        credits.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(new CreditsScreen(kingsFeast));
                dispose();
            }
        });
        return credits;
    }

    private ImageButton createOkButton() {
        okTexture = new Texture("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 3, Align.center);
        ok.setSize(150f, 75f);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(new MainMenuScreen(kingsFeast));
            }
        });
        return ok;
    }

    private ImageButton createMusicButton() {
        // two textures are used to give the user some feedback when pressing a button
        musicOnTexture = new Texture("MusicOnButton.png");
        musicOffTexture = new Texture("MusicOffButton.png");

        // this line is way too goddamn long
        final ImageButton musicButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(musicOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(musicOffTexture)));

        musicButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        musicButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 2, Align.center);

        // button's functionality
        musicButton.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y, int count, int button) {
               super.clicked(e, x, y);
            }
        });

        return musicButton;
    }

    //TextButton gameOptions = new TextButton(...);
    //        gameOptions.getStyle().checked = gameOptions.getStyle().down;
    //        gameOptions.addListener(new ClickListener(){
    //            @Override
    //            public void clicked(InputEvent event, float x, float y) {
    //                (...)
    //            }
    //        });

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        creditsTexture.dispose();
        okTexture.dispose();
    }
}
