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
    //  CLEAN CODE
    //  -Lack of documentation ----> Documentation started on various screens
    //  -Check that everything that needs disposing gets disposed when needed
    //  -Move spriteBatch to KingsFeast so it can be used elsewhere ----> Should be ok, now only one instance of batch exists,
    //                                                                      get it with kingsFeast.getSpriteBatch();
    //  MENUS AND MENU FUNCTIONS
    //  -Graphics for: Sling, River Pollution, King's dining hall
    //  -Verify that continue button works as intended
    //  -When accessing settings through pause and changing language, language not changed in GameScreen when continued
    //  LOCALIZATION
    //  -Localize all text once written
    //  STORY

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
    private int cleanLevelCounter;
    private String levelFoodWaste;

    // Internationalization
    LanguageManager langManager;

    // Story Points
    private final int storyPointAmount = 7;
    private boolean[] storyPoints;

    private boolean tutorialLevels;

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
        cleanLevelCounter = 0;
        levelFoodWaste = "0";
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

        storyPoints = new boolean[storyPointAmount];
        for (int i = 0; i < storyPointAmount; i++) {
            String storyPointKey = "storypoint" + (i+1);
            storyPoints[i] = kfprefs.getBoolean(storyPointKey);
        }

        tutorialLevels = true;
    }

    // Clear save data
    void clearSaveState() {
        kfprefs.remove("doPrefsExist");
        kfprefs.remove("totalThrows");
        kfprefs.remove("currentLevel");
        kfprefs.remove("totalScore");
        kfprefs.remove("pollution");
        kfprefs.remove("playthroughComplete");
        for (int i = 0; i < storyPointAmount; i++) {
            kfprefs.remove("storypoint" + (i+1));
        }

        kfprefs.flush();
        setCurrentLevel(kfprefs.getInteger("currentLevel"));
        levels = Util.buildLevels(this);
        initSaveState();
    }

    /**
     * Initializes localization. Creates new LanguageManager to which language .pref location and
     * info is fed. Fetches current language from prefs and sets it at the start of the game.
     */
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

    /**
     * Starts music.
     */
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
            levels.shuffle();
            getPrefs().putBoolean("playthroughComplete", true);
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
        setLevelFoodWaste(waste);
        int scores = 0;

        if(waste == 0) {
            scores = 3000;
        } else if (waste == 1) {
            scores = 2000;
        } else if(waste > 1 && waste <= 3) {
            scores = 1500;
        } else if(waste >= 4 && waste <= 7) {
            scores = 750;
        } else if(waste > 7) {
            scores = -100;
        }
        updateStats(throwes, scores, waste, served);
    }

    // Save new data
    private void updateStats(int throwes, int scores, int waste, int served) {
        setLevelThrows(throwes);
        setTotalThrows(throwes);
        setTotalScore(scores);
        if(isStoryPointShown(1)) {
            tutorialLevels = false;
        }
        calculatePollution(waste, served);
        setLevelScore(scores);
    }

    /**
     * Calculates new pollution level and sets it depending on score and waste.
     * @param waste Level score.
     * @param served Amount of served guests.
     */
    private void calculatePollution(int waste, int served) {
        oldPollution = Integer.parseInt(getPollutionLevel());

        if(tutorialLevels){
            setPollutionLevel(waste - (served * 3) - cleanLevelCounter);
        } else if(!tutorialLevels) {
            setPollutionLevel(waste - served - cleanLevelCounter);
        }
    }

    // SETTERS
    void setLevelThrows(int levelThrows) {
        this.levelThrows = Integer.toString(levelThrows);
    }
    void setCleanLevelCounter(int counter) {this.cleanLevelCounter += counter; }
    void resetCleanLevelCounter() {this.cleanLevelCounter = 0;}

    void setLevels(Array<LevelData> levels) {
        if (kfprefs.getBoolean("playthroughComplete")) {
            levels.shuffle();
            this.levels = levels;
        } else {
            this.levels = levels;
        }
    }

    /**
     * Set current level. Keeps track of the levels played.
     * @param x number of the level.
     */
    void setCurrentLevel(int x) {
        currentLevel = x;
    }

    /**
     * Set current level score.
     * @param score of the current level.
     */
    void setLevelScore(int score) {
        this.levelScore = Integer.toString(score);
    }

    void setLevelFoodWaste(int waste) {
        int foodWaste = Integer.parseInt(getLevelFoodWaste());
        foodWaste += waste;
        this.levelFoodWaste = Integer.toString(foodWaste);
    }

    void resetLevelFoodWaste() {
        this.levelFoodWaste = Integer.toString(0);
    }

    void setTotalScore(int score) {
        int total = Integer.parseInt(getTotalScore());
        total += score;
        this.totalScore = Integer.toString(total);
    }

    /**
     * Sets the pollution level.
     * @param pollution Change to old level (plus or minus)
     */
    void setPollutionLevel(int pollution) {
        int totalPollution = Integer.parseInt(getPollutionLevel()) + pollution;
        if(totalPollution < 0) {
            totalPollution = 0;
        } else if(totalPollution > 100) {
            totalPollution = 100;
        }
        this.pollutionLevel = Integer.toString(totalPollution);
    }

    /**
     * Sets the total throws.
     * @param throwes Throws to be added to total amount.
     */
    void setTotalThrows(int throwes) {
        int total = Integer.parseInt(getTotalThrows()) + throwes;
        this.totalThrows = Integer.toString(total);
    }

    // GETTERS

    /**
     * Get pollution level before the change.
     * @return Pollution level before change.
     */
    int getOldPollution() {
        return this.oldPollution;
    }

    /**
     * Get total score.
     * @return Total score.
     */
    String getTotalScore() { return this.totalScore; }
    String getTotalThrows() { return this.totalThrows; }
    String getLevelFoodWaste() { return this.levelFoodWaste; }
    String getLevelScore() { return this.levelScore; }
    String getPollutionLevel() { return this.pollutionLevel; }
    Preferences getPrefs() {
        return kfprefs;
    }
    SpriteBatch getSpriteBatch() {
        return batch;
    }

    /**
     * Get AssetManager.
     * @return AssetManager.
     */
    AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Get created levels.
     * @return Levels.
     */
    Array<LevelData> getLevels() { return levels; }
    int getCurrentLevel() {
        return currentLevel;
    }

    // PREFERENCE STUFF

    /**
     * Check if music is enabled.
     * @return true or false.
     */
    private boolean isMusicEnabled() {
        return getPrefs().getBoolean("music.enabled", true);
    }

    /**
     * Check if sound effects are enabled.
     * @return true or false.
     */
    boolean isSoundEffectsEnabled() {
        return getPrefs().getBoolean("sound.enabled", true);
    }

    /**
     * Check if english is enabled.
     * @return true or false.
     */
    boolean isEnglishEnabled() {
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
            kfprefs.putBoolean("playthroughComplete", false);

            for (int i = 0; i < storyPointAmount; i++) {
                kfprefs.putBoolean("storypoint" + (i+1), false);
            }

            kfprefs.flush();
            initVariables();
        }
    }

    void setStoryPointShown(int storyPointNum, boolean storyPointShown) {
        storyPoints[storyPointNum - 1] = storyPointShown;
        kfprefs.putBoolean("storypoint" + storyPointNum, storyPointShown);
        kfprefs.flush();
    }

    boolean isStoryPointShown(int storyPointNum) {
        return storyPoints[storyPointNum - 1];
    }
}
