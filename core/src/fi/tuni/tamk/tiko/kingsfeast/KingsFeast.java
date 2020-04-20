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
    //  STORY
    //  -Make lifeline availability based on story points
    //  -Find better font
    //  -Make text fit the text box

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

    // Story Points
    private boolean storyPoint1Shown;
    private boolean storyPoint2Shown;
    private boolean storyPoint3Shown;
    private boolean storyPoint4Shown;
    private boolean storyPoint5Shown;
    private boolean storyPoint6Shown;
    private boolean storyPoint7Shown;


    @Override
    public void create() {
        batch = new SpriteBatch();
        kfprefs = getPreferencesFromOS(kfprefs);
        initSaveState();
        initVariables();
        initLanguages();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        setScreen(new LoadingScreen(this));
        currentLevel = kfprefs.getInteger("currentLevel");
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
        pollutionLevel = Integer.toString(kfprefs.getInteger("pollution", 90));
        totalScore = Integer.toString(kfprefs.getInteger("totalScore", 0));
        totalThrows = Integer.toString(kfprefs.getInteger("totalThrows", 0));
        storyPoint1Shown = false;
        storyPoint2Shown = false;
        storyPoint3Shown = false;
        storyPoint4Shown = false;
        storyPoint5Shown = false;
        storyPoint6Shown = false;
        storyPoint7Shown = false;
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
    void setMusic() {
        music = assetManager.get("1.mp3");
        music.setLooping(true);
        if(isMusicEnabled()) {
            music.play();
        }
    }

    // Set sound effects to play (This might be redundant)
    void setSounds() {
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
        } else if (waste >= 1 && waste <= 3) {
            scores = 750;
        } else if(waste > 3 && waste <= 5) {
            scores = 500;
        } else if(waste >= 5 && waste <= 6) {
            scores = 200;
        } else if(waste > 6) {
            scores = -100;
        }
        updateStats(throwes, scores, waste);
    }

    // Save new data
    private void updateStats(int throwes, int scores, int waste) {
        setLevelThrows(throwes);
        setTotalThrows(throwes);
        setTotalScore(scores);
        calculatePollution(scores, waste);
        setLevelScore(scores);
    }

    // Calculate how much pollution changed after last level
    private void calculatePollution(int scoring, int waste) {
        oldPollution = Integer.parseInt(getPollutionLevel());
        if (scoring == 1000) {
            setPollutionLevel(-10);
        } else if (scoring == 750) {
            setPollutionLevel(-5);
        } else if (scoring == 500){
            setPollutionLevel(0);
        } else if (scoring == 200) {
            setPollutionLevel(2 + waste);
        } else if (scoring == -100) {
            setPollutionLevel(5 + waste);
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
    boolean isSoundEffectsEnabled() {
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
            kfprefs.putInteger("pollution", 90);
            kfprefs.flush();
            initVariables();
        }
    }

    public boolean isStoryPoint1Shown() {
        return storyPoint1Shown;
    }

    public void setStoryPoint1Shown(boolean storyPoint1Shown) {
        this.storyPoint1Shown = storyPoint1Shown;
    }

    public boolean isStoryPoint2Shown() {
        return storyPoint2Shown;
    }

    public void setStoryPoint2Shown(boolean storyPoint2Shown) {
        this.storyPoint2Shown = storyPoint2Shown;
    }

    public boolean isStoryPoint3Shown() {
        return storyPoint3Shown;
    }

    public void setStoryPoint3Shown(boolean storyPoint3Shown) {
        this.storyPoint3Shown = storyPoint3Shown;
    }

    public boolean isStoryPoint4Shown() {
        return storyPoint4Shown;
    }

    public void setStoryPoint4Shown(boolean storyPoint4Shown) {
        this.storyPoint4Shown = storyPoint4Shown;
    }

    public boolean isStoryPoint5Shown() {
        return storyPoint5Shown;
    }

    public void setStoryPoint5Shown(boolean storyPoint5Shown) {
        this.storyPoint5Shown = storyPoint5Shown;
    }

    public boolean isStoryPoint6Shown() {
        return storyPoint6Shown;
    }

    public void setStoryPoint6Shown(boolean storyPoint6Shown) {
        this.storyPoint6Shown = storyPoint6Shown;
    }

    public boolean isStoryPoint7Shown() {
        return storyPoint7Shown;
    }

    public void setStoryPoint7Shown(boolean storyPoint7Shown) {
        this.storyPoint7Shown = storyPoint7Shown;
    }
}
