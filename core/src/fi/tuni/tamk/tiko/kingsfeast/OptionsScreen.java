package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

// Using Stage2D
public class OptionsScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;

    // Screen stuff
    private final Screen previousScreen;
    private static final float GAME_WIDTH = 1280;
    private static final float GAME_HEIGHT = 720;
    private final float BUTTON_WIDTH = 500f;
    private final float BUTTON_HEIGHT = 120f;
    private Stage stage;

    //Textures
    private Texture okTexture;
    private Texture musicOnTexture;
    private Texture musicOffTexture;
    private Texture soundOnTexture;
    private Texture soundOffTexture;
    private Texture LanguageEnTexture;
    private Texture LanguageFiTexture;
    private Texture backgroundTexture;

    // Constructor receives game object to access it
    OptionsScreen(KingsFeast kingsFeast, Screen screen) {
        this.kingsFeast = kingsFeast;
        previousScreen = screen;
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Add all buttons
        stage.addActor(createBackgroundImage());
        stage.addActor(createOkButton());
        stage.addActor(createMusicButton());
        stage.addActor(createSoundButton());
        stage.addActor(createLanguageButton());
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

    // Returns background image
    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    // Returns ok imagebutton
    private ImageButton createOkButton() {
        okTexture = kingsFeast.getAssetManager().get("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        ok.setPosition(GAME_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 5, Align.center);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(previousScreen);
            }
        });
        return ok;
    }

    // Returns music imagebutton
    private ImageButton createMusicButton() {
        // Two textures for on and off state as well as two textures in finnish
        // Checks is english or finnish enabled as a language and loads textures accordingly
        if(kingsFeast.isEnglishEnabled()) {
            musicOnTexture = kingsFeast.getAssetManager().get("MusicOnButton.png");
            musicOffTexture = kingsFeast.getAssetManager().get("MusicOffButton.png");
        } else {
            musicOnTexture = kingsFeast.getAssetManager().get("musiikkipaalla.png");
            musicOffTexture = kingsFeast.getAssetManager().get("musiikkipois.png");
        }

        final ImageButton musicButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(musicOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(musicOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(musicOffTexture)));

        // Check which state (on or off) of the button is enabled and shown
        if(isMusicEnabled()) {
            musicButton.setChecked(false);
        } else {
            musicButton.setChecked(true);
        }

        musicButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        musicButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 2, Align.center);

        // Button's functionality
        musicButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if(isMusicEnabled()) {
                    kingsFeast.music.stop();
                    setMusicEnabled(false);
                } else if(!isMusicEnabled()) {
                    kingsFeast.music.play();
                    setMusicEnabled(true);
                }
            }
        });

        return musicButton;
    }

    private ImageButton createSoundButton() {
        // Two textures for on and off state as well as two textures in finnish
        // Checks is english or finnish enabled as a language and loads textures accordingly
        if(kingsFeast.isEnglishEnabled()) {
            soundOnTexture = kingsFeast.getAssetManager().get("SoundOnButton.png");
            soundOffTexture = kingsFeast.getAssetManager().get("SoundOffButton.png");
        } else {
            soundOnTexture = kingsFeast.getAssetManager().get("aanetpaalla.png");
            soundOffTexture = kingsFeast.getAssetManager().get("aanetpois.png");
        }

        final ImageButton soundButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(soundOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(soundOnTexture)),
                        new TextureRegionDrawable(new TextureRegion(soundOffTexture)));

        // Check which state (on or off) of the button is enabled and shown
        if(isSoundEffectsEnabled()) {
            soundButton.setChecked(false);
        } else {
            soundButton.setChecked(true);
        }

        soundButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        soundButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 3, Align.center);

        // Button's functionality
        soundButton.addListener(new ActorGestureListener() {
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    if(isSoundEffectsEnabled()) {
                        //kingsFeast.sound.stop();
                        setSoundEffectsEnabled(false);
                    } else if(!isSoundEffectsEnabled()) {
                        //kingsFeast.sound.play();
                        setSoundEffectsEnabled(true);
                    }
                }
            });

        return soundButton;
    }

    private ImageButton createLanguageButton() {
        // Two textures for on and off state as well as two textures in finnish
        // Checks is english or finnish enabled as a language and loads textures accordingly
        if(kingsFeast.isEnglishEnabled()) {
            LanguageEnTexture = kingsFeast.getAssetManager().get("LanguageEnButton.png");
            LanguageFiTexture = kingsFeast.getAssetManager().get("LanguageFiButton.png");
        } else {
            LanguageEnTexture = kingsFeast.getAssetManager().get("kielienglanti.png");
            LanguageFiTexture = kingsFeast.getAssetManager().get("kielisuomi.png");
        }

        final ImageButton langButton =
                new ImageButton(new TextureRegionDrawable(new TextureRegion(LanguageEnTexture)),
                        new TextureRegionDrawable(new TextureRegion(LanguageEnTexture)),
                        new TextureRegionDrawable(new TextureRegion(LanguageFiTexture)));

        // Check which state (on or off) of the button is enabled and shown
        if(kingsFeast.isEnglishEnabled()) {
            langButton.setChecked(false);
        } else {
            langButton.setChecked(true);
        }

        langButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        langButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 4, Align.center);

        // Button's functionality
        langButton.addListener(new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if(kingsFeast.isEnglishEnabled()) {
                    //kingsFeast.sound.stop();
                    setEnglishEnabled(false);
                } else if(!kingsFeast.isEnglishEnabled()) {
                    //kingsFeast.sound.play();
                    setEnglishEnabled(true);
                }
            }
        });

        return langButton;
    }

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

    // Set and save language settings
    private void setEnglishEnabled(boolean englishEnabled) {
        kingsFeast.getPrefs().putBoolean("english.enabled", englishEnabled);
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
