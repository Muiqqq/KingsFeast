package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

public class KingsFeast extends Game {
    // TODO: Current issues:
    //  GENERAL PROBLEMS
    //  -Fix flickering of the screen edges, explore viewports
    //  CLEAN CODE
    //  -Lack of documentation
    //  -Check that everything that needs disposing gets disposed when needed
    //  -All assets need to be changed to be loaded with assetManager
    //  -Move spriteBatch to KingsFeast so it can be used elsewhere
    //  MENUS AND MENU FUNCTIONS
    //  -Graphics for: UI, Game, buttons, HUD, backgrounds
    //  -Main menu needs a how to play button which leads to the written tutorial ----> Button ok, Tutorial not
    //  -Written tutorial needs to be made
    //  -Add functionality to Continue Button in Main Menu Screen
    //  -Buttons might be a bit too small for mobile in general? ----> Now resized except the GameScreen buttons
    //  -Choose a better FreeType Font to resemble the theme more ----> Some fonts cause crashes on mobile
    //  LOCALIZATION
    //  -Change all ImageButtons to TextButtons for localization purposes
    //  -Implement localisation and make the language button save to prefs

    // remember to give an instance of 'this' to all new screens, if anything
    // from this class or parent class is to be used in that screen.

    private final AssetManager assetManager = new AssetManager();
    private LevelBuilder levelBuilder;
    private Array<LevelData> levels;
    private int currentLevel;
    private Preferences kfprefs;
    Music music;

    // Game data
    private String pollutionLevel;
    private String foodWaste;
    private String totalThrows;
    private String levelThrows;
    private String totalScore;
    private String levelScore;


    @Override
    public void create() {
        kfprefs = getPreferencesFromOS(kfprefs);
        initSaveState();
        initVariables();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        levelBuilder = new LevelBuilder(this);
        currentLevel = kfprefs.getInteger("currentLevel");
        setScreen(new LoadingScreen(this, levelBuilder));
        setMusic();
        setSounds();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    AssetManager getAssetManager() {
        return assetManager;
    }

    Array<LevelData> getLevels() {
        return levels;
    }

    void setLevels(Array<LevelData> levels) {
        this.levels = levels;
    }

    int getCurrentLevel() {
        return currentLevel;
    }

    private void setCurrentLevel(int x) {
        currentLevel = x;
    }

    private void incrementCurrentLevel() {
        kfprefs.putInteger("currentLevel", currentLevel + 1);
        kfprefs.flush();
        currentLevel = kfprefs.getInteger("currentLevel");
    }

    private Preferences getPreferencesFromOS(Preferences prefs) {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences("kfsettings");
        }
        return prefs;
    }

    Preferences getPrefs() {
        return kfprefs;
    }

    public void initSaveState() {
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

    public void initVariables() {
        pollutionLevel = Integer.toString(kfprefs.getInteger("pollution", 50));
        totalScore = Integer.toString(kfprefs.getInteger("totalScore", 0));
        totalThrows = Integer.toString(kfprefs.getInteger("totalThrows", 0));
    }

    private void clearSaveState() {
        kfprefs.remove("doPrefsExist");
        kfprefs.remove("totalThrows");
        kfprefs.remove("currentLevel");
        kfprefs.remove("pollution");
        kfprefs.flush();
        setCurrentLevel(kfprefs.getInteger("currentLevel"));
        initSaveState();
    }

    private void setMusic() {
        music = assetManager.get("1.mp3");
        music.setLooping(true);
        if(isMusicEnabled()) {
            music.play();
        }
    }

    private void setSounds() {
        if(isSoundEffectsEnabled()) {
            // Play sounds
        }
    }

    private boolean isMusicEnabled() {
        return getPrefs().getBoolean("music.enabled", true);
    }

    private boolean isSoundEffectsEnabled() {
        return getPrefs().getBoolean("sound.enabled", true);
    }

    void saveGameOnLevelSwap() {
        if (getCurrentLevel() < getLevels().size - 1) {
            incrementCurrentLevel();
        } else {
            clearSaveState();
        }
    }

    // Gameplay data getters and setters
    public void setPollutionLevel(int pollution) {
        int totalPollution = Integer.parseInt(getPollutionLevel()) + pollution;
        this.pollutionLevel = Integer.toString(totalPollution);
    }

    public String getPollutionLevel() {
        return this.pollutionLevel;
    }

    void calculateScore(int throwes, int served) {
        int waste = throwes - served;
        int scores = 0;
        if(waste < 5) {
            scores = 1000;
        } else if(waste >= 5 && waste <= 10) {
            scores = 500;
        } else if(waste > 10 && waste <= 20) {
            scores = 100;
        }
        updateStats(throwes, scores);

    }

    private void updateStats(int throwes, int scores) {
        setLevelThrows(throwes);
        setTotalThrows(throwes);
        setTotalScore(scores);
        calculatePollution(scores);
        setLevelScore(scores);
    }

    void setLevelScore(int score) {
        this.levelScore = Integer.toString(score);
    }

    public String getLevelScore() {
        return this.levelScore;
    }

    void setTotalThrows(int throwes) {
        int total = Integer.parseInt(getTotalThrows()) + throwes;
        this.totalThrows = Integer.toString(total);
    }

    public String getTotalThrows() {
        return this.totalThrows;
    }

    void setTotalScore(int score) {
        int total = Integer.parseInt(getTotalScore());
        total += score;
        this.totalScore = Integer.toString(total);
    }

    public String getTotalScore() {
        return this.totalScore;
    }

    void calculatePollution(int scoring) {
        if (scoring == 1000) {
            setPollutionLevel(10);
        } else if (scoring == 500) {
            setPollutionLevel(5);
        } else if (scoring == 100){
            setPollutionLevel(-10);
        }
    }

    void setLevelThrows(int levelThrows) {
        this.levelThrows = Integer.toString(levelThrows);
    }

    void checkGameEnd() {
        int pollution = Integer.parseInt(pollutionLevel);
        if(pollution >= 100) {
            gameLost();
        } else if (pollution <= 0) {
            gameWon();
        }
    }

    public void gameLost() {

    }

     public void gameWon() {

    }

}
