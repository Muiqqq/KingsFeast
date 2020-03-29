package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    private Texture soundOnTexture;
    private Texture soundOffTexture;

    // Sound Fx saving initially working
    // Language to be done

    // DOCUMENTATION

    // Constructor receives game object to access it
    public OptionsScreen(KingsFeast kingsFeast) {
        this.kingsFeast = kingsFeast;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Add all buttons
        stage.addActor(createBackgroundImage());
        stage.addActor(createCreditsButton());
        stage.addActor(createOkButton());
        stage.addActor(createMusicButton());
        stage.addActor(createSoundButton());
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
        backgroundTexture = new Texture("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private ImageButton createCreditsButton() {
        creditsTexture = new Texture("credits.png");
        ImageButton credits = new ImageButton(new TextureRegionDrawable(new TextureRegion(creditsTexture)));
        credits.setPosition(GAME_WIDTH / 5, (GAME_HEIGHT / 5) - 75);
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
        ok.setPosition(GAME_WIDTH / 5, (GAME_HEIGHT / 5) - 30);
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

        musicButton.setSize(150f, 75f);
        musicButton.setPosition(GAME_WIDTH - 300, GAME_HEIGHT - 150);

        // button's functionality
        musicButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if(isMusicEnabled()) {
                    System.out.println(isMusicEnabled());
                    kingsFeast.music.stop();
                    setMusicEnabled(false);
                } else if(!isMusicEnabled()) {
                    System.out.println(isMusicEnabled());
                    kingsFeast.music.play();
                    setMusicEnabled(true);
                }
            }
        });

        return musicButton;
    }

    private ImageButton createSoundButton() {
        // two textures are used to give the user some feedback when pressing a button
        soundOnTexture = new Texture("SoundOnButton.png");
        soundOffTexture = new Texture("SoundOffButton.png");

        // this line is way too goddamn long
        final ImageButton soundButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(soundOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(soundOffTexture)));

        soundButton.setSize(150f, 75f);
        soundButton.setPosition(GAME_WIDTH - 300, GAME_HEIGHT - 200);

        // button's functionality
        soundButton.addListener(new ActorGestureListener() {
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    if(isSoundEffectsEnabled()) {
                        System.out.println(isSoundEffectsEnabled());
                        //kingsFeast.sound.stop();
                        setSoundEffectsEnabled(false);
                    } else if(!isSoundEffectsEnabled()) {
                        System.out.println(isSoundEffectsEnabled());
                        //kingsFeast.sound.play();
                        setSoundEffectsEnabled(true);
                    }
                }
            });

        return soundButton;
    }

    // Get preferences file
    protected Preferences getPrefs() {
        return Gdx.app.getPreferences("kfsettings");
    }

    // Set and save music settings
    public void setMusicEnabled(boolean musicEnabled) {
        getPrefs().putBoolean("music.enabled", musicEnabled);
        getPrefs().flush();
    }

    // Method to check if music is set to enabled in the settings file
    public boolean isMusicEnabled() {
        return getPrefs().getBoolean("music.enabled", true);
    }

    // Method to check if sound effects are set to enabled in the settings file
    public boolean isSoundEffectsEnabled() {
        return getPrefs().getBoolean("sound.enabled", true);
    }

    // Set and save sound effect settings
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        getPrefs().putBoolean("sound.enabled", soundEffectsEnabled);
        getPrefs().flush();
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        creditsTexture.dispose();
        okTexture.dispose();
        musicOffTexture.dispose();
        musicOnTexture.dispose();
        soundOffTexture.dispose();
        soundOnTexture.dispose();
    }
}
