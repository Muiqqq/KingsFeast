package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
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
    private final Screen previousScreen;
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
    OptionsScreen(KingsFeast kingsFeast, Screen screen) {
        this.kingsFeast = kingsFeast;
        previousScreen = screen;
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
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    private ImageButton createCreditsButton() {
        creditsTexture = kingsFeast.getAssetManager().get("credits.png");
        ImageButton credits = new ImageButton(new TextureRegionDrawable(new TextureRegion(creditsTexture)));
        credits.setPosition(GAME_WIDTH / 5, (GAME_HEIGHT / 5) - 75);
        credits.setSize(150f, 75f);
        credits.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(new CreditsScreen(kingsFeast, getThisScreen()));
                dispose();
            }
        });
        return credits;
    }

    private ImageButton createOkButton() {
        okTexture = kingsFeast.getAssetManager().get("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setPosition(GAME_WIDTH / 5, (GAME_HEIGHT / 5) - 30);
        ok.setSize(150f, 75f);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(previousScreen);
            }
        });
        return ok;
    }

    private ImageButton createMusicButton() {
        // two textures are used to give the user some feedback when pressing a button
        musicOnTexture = kingsFeast.getAssetManager().get("MusicOnButton.png");
        musicOffTexture = kingsFeast.getAssetManager().get("MusicOffButton.png");

        // this line is way too goddamn long
        final ImageButton musicButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(musicOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(musicOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(musicOffTexture)));

        if(isMusicEnabled()) {
            musicButton.setChecked(false);
        } else {
            musicButton.setChecked(true);
        }

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
        soundOnTexture = kingsFeast.getAssetManager().get("SoundOnButton.png");
        soundOffTexture = kingsFeast.getAssetManager().get("SoundOffButton.png");

        // this line is way too goddamn long
        final ImageButton soundButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(soundOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(soundOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(soundOffTexture)));

        if(isSoundEffectsEnabled()) {
            soundButton.setChecked(false);
        } else {
            soundButton.setChecked(true);
        }

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

    /* Redundant
    // Get preferences file
    protected Preferences getPrefs() {
        return Gdx.app.getPreferences("kfsettings");
    }
    */

    // Set and save music settings
    private void setMusicEnabled(boolean musicEnabled) {
        kingsFeast.getPrefs().putBoolean("music.enabled", musicEnabled);
        kingsFeast.getPrefs().flush();
    }

    // Method to check if music is set to enabled in the settings file
    private boolean isMusicEnabled() {
        return kingsFeast.getPrefs().getBoolean("music.enabled", true);
    }

    // Method to check if sound effects are set to enabled in the settings file
    private boolean isSoundEffectsEnabled() {
        return kingsFeast.getPrefs().getBoolean("sound.enabled", true);
    }

    // Set and save sound effect settings
    private void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        kingsFeast.getPrefs().putBoolean("sound.enabled", soundEffectsEnabled);
        kingsFeast.getPrefs().flush();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private OptionsScreen getThisScreen() {
        return this;
    }
}
