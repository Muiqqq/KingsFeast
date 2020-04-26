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
    private final int storyPointAmount = 7;
    private boolean[] storyPoints;

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

        storyPoints = new boolean[storyPointAmount];
        for (int i = 0; i < storyPointAmount; i++) {
            String storyPointKey = "storypoint" + (i+1);
            storyPoints[i] = kfprefs.getBoolean(storyPointKey);
        }
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
        music = assetManager.get("bgmusik.mp3");
        music.setLooping(true);
        if(isMusicEnabled()) {
            music.play();
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

    /**
     * Calculates score based on accumulated waste. Throws - served = waste.
     * @param throwes How many foods player threw.
     * @param served How many guests served.
     */
    void calculateScore(int throwes, int served) {
        int waste = throwes - served;
        int scores = 0;

        if(waste == 0) {
            scores = 1000;
        } else if (waste >= 1 && waste <= 3) {
            scores = 750;
        } else if(waste > 3 && waste <= 5) {
            scores = 500;
        } else if(waste >= 5 && waste <= 8) {
            scores = 200;
        } else if(waste > 8) {
            scores = -100;
        }
        updateStats(throwes, scores, waste);
    }

    /**
     * Updates all stats based on last level. Passes three parameters to calculate new data.
     * @param throwes Food thrown in last level.
     * @param scores Score gained in last level.
     * @param waste Waste accumulated in last level.
     */
    private void updateStats(int throwes, int scores, int waste) {
        setLevelThrows(throwes);
        setTotalThrows(throwes);
        setTotalScore(scores);
        calculatePollution(scores, waste);
        setLevelScore(scores);
    }

    /**
     * Calculates new pollution level and sets it depending on score and waste.
     * @param scoring Level score.
     * @param waste Amount of waste accumulated in the last level.
     */
    private void calculatePollution(int scoring, int waste) {
        oldPollution = Integer.parseInt(getPollutionLevel());
        if (scoring == 1000) {
            setPollutionLevel(-10);
        } else if (scoring == 750) {
            setPollutionLevel(-5);
        } else if (scoring == 500){
            setPollutionLevel(waste);
        } else if (scoring == 200) {
            setPollutionLevel(waste);
        } else if (scoring == -100) {
            setPollutionLevel(5 + waste);
        }
    }

    // SETTERS

    /**
     * Sets the amount of throws in last level to a variable.
     * @param levelThrows Amount of food thrown.
     */
    private void setLevelThrows(int levelThrows) {
        this.levelThrows = Integer.toString(levelThrows);
    }


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
    private void setCurrentLevel(int x) {
        currentLevel = x;
    }

    /**
     * Set current level score.
     * @param score of the current level.
     */
    private void setLevelScore(int score) {
        this.levelScore = Integer.toString(score);
    }

    /**
     * Set total score.
     * @param score to be added to total amount.
     */
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
    private void setTotalThrows(int throwes) {
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

    /**
     * Get total throws.
     * @return Total throws.
     */
    String getTotalThrows() {
        return this.totalThrows;
    }

    /**
     * Get level score.
     * @return Level score.
     */
    String getLevelScore() { return this.levelScore; }

    /**
     * Get current pollution level.
     * @return Current pollution level.
     */
    String getPollutionLevel() { return this.pollutionLevel; }
    Preferences getPrefs() {
        return kfprefs;
    }

    /**
     * Get Spritebatch.
     * @return Spritebatch.
     */
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
