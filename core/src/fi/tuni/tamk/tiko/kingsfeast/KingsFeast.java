package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

public class KingsFeast extends Game {
    // TODO: Current issues:
    //  GENERAL PROBLEMS
    //  -Fix flickering of the screen edges, explore viewports ----> Changed all UI viewports to StretchViewport,
    //                                                                  needs further testing but seems to work.
    //                                                                  Some graphics might look stretched on some screen sizes ~Muikku
    //  CLEAN CODE
    //  -Lack of documentation ----> Documentation started on various screens
    //  -Check that everything that needs disposing gets disposed when needed
    //  -Move spriteBatch to KingsFeast so it can be used elsewhere ----> Should be ok, now only one instance of batch exists,
    //                                                                      get it with kingsFeast.getSpriteBatch();
    //  MENUS AND MENU FUNCTIONS
    //  -Graphics for: UI, Game, buttons, HUD, backgrounds
    //  -Main menu needs a how to play button which leads to the written tutorial ----> Button ok, Tutorial not
    //  -Written tutorial needs to be made
    //  -Verify that continue button works as intended
    //  -Buttons might be a bit too small for mobile in general? ----> Now resized except the GameScreen buttons
    //  -When accessing settings through pause and changing language, language not changed in GameScreen when continued
    //  LOCALIZATION
    //  -Localize all text once written

    // remember to give an instance of 'this' to all new screens, if anything
    // from this class or parent class is to be used in that screen.

    private final AssetManager assetManager = new AssetManager();
    private SpriteBatch batch;
    private Array<LevelData> levels;
    private int currentLevel;
    private Preferences kfprefs;
    Music music;

    // Game data
    private String pollutionLevel;
    private String totalThrows;
    private String levelThrows;
    private String totalScore;
    private String levelScore;
    private int oldPollution;

    // Internationalization
    LanguageManager langManager;


    @Override
    public void create() {
        batch = new SpriteBatch();
        kfprefs = getPreferencesFromOS(kfprefs);
        initSaveState();
        initVariables();
        initLanguages();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        currentLevel = kfprefs.getInteger("currentLevel");
        setScreen(new LoadingScreen(this));
        setMusic();
        setSounds();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }

    // Increment current level and save level number to preferences
    private void incrementCurrentLevel() {
        kfprefs.putInteger("currentLevel", currentLevel + 1);
        kfprefs.flush();
        currentLevel = kfprefs.getInteger("currentLevel");
    }

    // Initialize variables to default for a new game
    private void initVariables() {
        pollutionLevel = Integer.toString(kfprefs.getInteger("pollution", 50));
        totalScore = Integer.toString(kfprefs.getInteger("totalScore", 0));
        totalThrows = Integer.toString(kfprefs.getInteger("totalThrows", 0));
    }

    // Clear save data
    void clearSaveState() {
        kfprefs.remove("doPrefsExist");
        kfprefs.remove("totalThrows");
        kfprefs.remove("currentLevel");
        kfprefs.remove("totalScore");
        kfprefs.remove("pollution");
        kfprefs.flush();
        setCurrentLevel(kfprefs.getInteger("currentLevel"));
        initSaveState();
    }

    private void initLanguages() {
        langManager = new LanguageManager();

        FileHandle englishFileHandle = Gdx.files.internal("i18n/MyBundle_en");
        FileHandle finnishFileHandle = Gdx.files.internal("i18n/MyBundle_fi");

        langManager.loadLanguage("english", englishFileHandle, Locale.US);
        langManager.loadLanguage("finnish", finnishFileHandle, new Locale("fi", "FI"));

        if(isEnglishEnabled()) {
            langManager.setCurrentLanguage("english");
        } else {
            langManager.setCurrentLanguage("finnish");
        }

    }

    // Set music to play
    private void setMusic() {
        music = assetManager.get("1.mp3");
        music.setLooping(true);
        if(isMusicEnabled()) {
            music.play();
        }
    }

    // Set sound effects to play (This might be redundant)
    private void setSounds() {
        if(isSoundEffectsEnabled()) {
            // Play sounds
        }
    }

    // Save game when level changes
    void saveGameOnLevelSwap() {
        if (getCurrentLevel() < getLevels().size - 1) {
            incrementCurrentLevel();
            getPrefs().putInteger("totalScore",
                    Integer.parseInt(getTotalScore()));

            getPrefs().putInteger("pollution",
                    Integer.parseInt(getPollutionLevel()));
            getPrefs().flush();
        } else {
            //clearSaveState(); <--- This commented out to test infinite loop probably will either way be deleted
            this.currentLevel = 0;
            getPrefs().putInteger("totalScore",
                    Integer.parseInt(getTotalScore()));

            getPrefs().putInteger("pollution",
                    Integer.parseInt(getPollutionLevel()));
            getPrefs().flush();
        }
    }

    // Game logic methods

    // Calculate score and invoke updateStats method to save new info
    void calculateScore(int throwes, int served) {
        int waste = throwes - served;
        int scores = 0;

        if(waste == 0) {
            scores = 1000;
        } else if (waste == 1) {
            scores = 750;
        } else if(waste > 1 && waste <= 3) {
            scores = 500;
        } else if(waste >= 4 && waste <= 6) {
            scores = 200;
        } else if(waste > 6) {
            scores = -100;
        }
        updateStats(throwes, scores);
    }

    // Save new data
    private void updateStats(int throwes, int scores) {
        setLevelThrows(throwes);
        setTotalThrows(throwes);
        setTotalScore(scores);
        calculatePollution(scores);
        setLevelScore(scores);
    }

    // Calculate how much pollution changed after last level
    private void calculatePollution(int scoring) {
        oldPollution = Integer.parseInt(getPollutionLevel());
        if (scoring == 1000) {
            setPollutionLevel(-15);
        } else if (scoring == 750) {
            setPollutionLevel(-30); // oli 8
        } else if (scoring == 500){
            setPollutionLevel(-2);
        } else if (scoring == 200) {
            setPollutionLevel(5);
        } else if (scoring == -100) {
            setPollutionLevel(10);
        }
    }

    // SETTERS
    void setLevelThrows(int levelThrows) {
        this.levelThrows = Integer.toString(levelThrows);
    }
    void setLevels(Array<LevelData> levels) {
        this.levels = levels;
    }
    void setCurrentLevel(int x) {
        currentLevel = x;
    }
    void setLevelScore(int score) {
        this.levelScore = Integer.toString(score);
    }

    void setTotalScore(int score) {
        int total = Integer.parseInt(getTotalScore());
        total += score;
        this.totalScore = Integer.toString(total);
    }

    void setPollutionLevel(int pollution) {
        int totalPollution = Integer.parseInt(getPollutionLevel()) + pollution;
        if(totalPollution < 0) {
            totalPollution = 0;
        } else if(totalPollution > 100) {
            totalPollution = 100;
        }
        this.pollutionLevel = Integer.toString(totalPollution);
    }

    void setTotalThrows(int throwes) {
        int total = Integer.parseInt(getTotalThrows()) + throwes;
        this.totalThrows = Integer.toString(total);
    }

    // GETTERS
    int getOldPollution() {
        return this.oldPollution;
    }
    String getTotalScore() { return this.totalScore; }
    String getTotalThrows() {
        return this.totalThrows;
    }
    String getLevelScore() { return this.levelScore; }
    String getPollutionLevel() { return this.pollutionLevel; }
    Preferences getPrefs() {
        return kfprefs;
    }
    SpriteBatch getSpriteBatch() {
        return batch;
    }
    AssetManager getAssetManager() {
        return assetManager;
    }
    Array<LevelData> getLevels() { return levels; }
    int getCurrentLevel() {
        return currentLevel;
    }

    // Preferences and save game methods
    private boolean isMusicEnabled() {
        return getPrefs().getBoolean("music.enabled", true);
    }
    private boolean isSoundEffectsEnabled() {
        return getPrefs().getBoolean("sound.enabled", true);
    }
    public boolean isEnglishEnabled() {
        return getPrefs().getBoolean("english.enabled", true);
    }
    private Preferences getPreferencesFromOS(Preferences prefs) {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences("kfsettings");
        }
        return prefs;
    }

    private void initSaveState() {
        if (!kfprefs.contains("doPrefsExist")) {
            kfprefs.putBoolean("doPrefsExist", true);
            kfprefs.putInteger("totalThrows", 0);
            kfprefs.putInteger("currentLevel", 0);
            kfprefs.putInteger("totalScore", 0);
            kfprefs.putInteger("pollution", 50);
            kfprefs.flush();
            initVariables();
        }
    }

}
