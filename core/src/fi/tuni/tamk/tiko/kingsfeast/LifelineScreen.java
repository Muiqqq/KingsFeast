package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LifelineScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 480;
    private Texture backgroundTexture;
    private Stage stage;
    private final float BUTTON_WIDTH = 500f;
    private final float BUTTON_HEIGHT = 120f;

    private Texture pigsTexture;
    private Texture compostTexture;
    private Texture poorTexture;

    public LifelineScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        stage.addActor(createBackgroundImage());
        stage.addActor(createPigsLifeline());
        stage.addActor(createCompostLifeLine());
        stage.addActor(createPoorLifeLine());
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
        backgroundTexture = kingsFeast.getAssetManager().get("options.jpg");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private ImageButton createPigsLifeline() {
        pigsTexture = kingsFeast.getAssetManager().get("pigsplaceholder.png");
        ImageButton pigsLifeline = new ImageButton(new TextureRegionDrawable(new TextureRegion(pigsTexture)));
        pigsLifeline.setPosition(GAME_WIDTH - 200, 450, Align.center);
        pigsLifeline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        pigsLifeline.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                // DO LIFELINE ACTION
                kingsFeast.setScreen(new GameScreen(kingsFeast));
            }
        });
        return pigsLifeline;
    }

    private ImageButton createCompostLifeLine() {
        compostTexture = kingsFeast.getAssetManager().get("compostplaceholder.png");
        ImageButton compostLifeline = new ImageButton(new TextureRegionDrawable(new TextureRegion(compostTexture)));
        compostLifeline.setPosition(GAME_WIDTH - 200, 300, Align.center);
        compostLifeline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        compostLifeline.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                // DO LIFELINE ACTION
                kingsFeast.setScreen(new GameScreen(kingsFeast));
            }
        });
        return compostLifeline;
    }

    private ImageButton createPoorLifeLine() {
        poorTexture = kingsFeast.getAssetManager().get("poorplaceholder.png");
        ImageButton poorLifeline = new ImageButton(new TextureRegionDrawable(new TextureRegion(poorTexture)));
        poorLifeline.setPosition(GAME_WIDTH - 200, 150, Align.center);
        poorLifeline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        poorLifeline.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                // DO LIFELINE ACTION
                kingsFeast.setScreen(new GameScreen(kingsFeast));
                dispose();
            }
        });
        return poorLifeline;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
