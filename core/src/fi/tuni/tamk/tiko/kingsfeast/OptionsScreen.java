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

/**
 * OptionsScreen class handles the creation of options screen.
 */
public class OptionsScreen extends ScreenAdapter {
    private final KingsFeast kingsFeast;

    // Screen stuff
    private final Screen previousScreen;
    private static final float GAME_WIDTH = 1920;
    private static final float GAME_HEIGHT = 1080;
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
    private Texture scrollBg;

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
        stage.addActor(createScroll());
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

    /**
     * Creates a background image for the screen.
     * @return Background image.
     */
    private Image createBackgroundImage() {
        backgroundTexture = kingsFeast.getAssetManager().get("riverscreen.png");
        Image background = new Image(backgroundTexture);
        background.setSize(GAME_WIDTH, GAME_HEIGHT);
        return background;
    }

    /**
     * Creates a background image of a scroll for the buttons.
     * @return Background scroll image.
     */
    private Image createScroll() {
        scrollBg = kingsFeast.getAssetManager().get("tausta.png");
        Image scroll = new Image(scrollBg);
        scroll.setSize(scrollBg.getWidth() - 250, scrollBg.getHeight() - 250);
        scroll.setPosition(GAME_WIDTH / 2 - scroll.getWidth() / 2, GAME_HEIGHT / 2 - scroll.getHeight() / 2);
        return scroll;
    }

    /**
     * Creates an imagebutton for ok. Tapping it returns the player to the main menu.
     * @return Ok imagebutton.
     */
    private ImageButton createOkButton() {
        okTexture = kingsFeast.getAssetManager().get("OkButton.png");
        ImageButton ok = new ImageButton(new TextureRegionDrawable(new TextureRegion(okTexture)));
        ok.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        ok.setPosition(GAME_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 5 - 150, Align.center);
        ok.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                kingsFeast.setScreen(previousScreen);
            }
        });
        return ok;
    }

    /**
     * Creates an imagebutton for music enabling and disabling.
     * @return Music imagebutton.
     */
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
        musicButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 2 - 100, Align.center);

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

    /**
     * Creates an imagebutton for sound enabling and disabling.
     * @return Sound imagebutton.
     */
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
        soundButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 3 - 100, Align.center);

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

    /**
     * Creates an imagebutton for changing the languages.
     * @return Language imagebutton.
     */
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
        langButton.setPosition(GAME_WIDTH / 2, GAME_HEIGHT - BUTTON_HEIGHT * 4 - 100, Align.center);

        // Button's functionality
        langButton.addListener(new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if(kingsFeast.isEnglishEnabled()) {
                    setEnglishEnabled(false);
                    kingsFeast.langManager.setCurrentLanguage("finnish");
                } else if(!kingsFeast.isEnglishEnabled()) {
                    kingsFeast.langManager.setCurrentLanguage("english");
                    setEnglishEnabled(true);
                }
            }
        });

        return langButton;
    }

    /**
     * Sets the music enabled or disabled and saves it to preferences.
     * @param musicEnabled takes true or false.
     */
    private void setMusicEnabled(boolean musicEnabled) {
        kingsFeast.getPrefs().putBoolean("music.enabled", musicEnabled);
        kingsFeast.getPrefs().flush();
    }

    /**
     * Method to check if music is set to enabled in the settings file.
     * @return true or false.
     */
    private boolean isMusicEnabled() {
        return kingsFeast.getPrefs().getBoolean("music.enabled", true);
    }

    /**
     * Method to check if sounds are set to enabled in the settings file.
     * @return true or false.
     */
    private boolean isSoundEffectsEnabled() {
        return kingsFeast.getPrefs().getBoolean("sound.enabled", true);
    }

    /**
     * Set sounds on or off.
     * @param soundEffectsEnabled takes in true or false.
     */
    private void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        kingsFeast.getPrefs().putBoolean("sound.enabled", soundEffectsEnabled);
        kingsFeast.getPrefs().flush();
    }

    /**
     * Sets the language.
     * @param englishEnabled true = english, false = finnish.
     */
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
