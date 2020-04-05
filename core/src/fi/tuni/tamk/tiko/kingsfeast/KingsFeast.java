package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

public class KingsFeast extends Game {
    // TODO: Current issues:
    //  GENERAL PROBLEMS
    //  -Fix flickering of the screen edges, explore viewports ----> Changed all UI viewports to StretchViewport,
    //                                                                  needs further testing but seems to work.
    //                                                                  Some graphics might look stretched on some screen sizes ~Muikku
    //  CLEAN CODE
    //  -Lack of documentation
    //  -Check that everything that needs disposing gets disposed when needed
    //  -All assets need to be changed to be loaded with assetManager
    //  -Move spriteBatch to KingsFeast so it can be used elsewhere ----> Should be ok, now only one instance of batch exists,
    //                                                                      get it with kingsFeast.getSpriteBatch();
    //  MENUS AND MENU FUNCTIONS
    //  -Graphics for: UI, Game, buttons, HUD, backgrounds
    //  -Menu buttons have incorrect size ---> Should be ok
    //  -Main menu needs a how to play button which leads to the written tutorial ----> Button ok, Tutorial not
    //  -Written tutorial needs to be made
    //  -Saves need to be made resettable, add a reset save button to main menu
    //  -OR Change start game to continue game and add a new game button ----> Button ok, functionality not
    //  -Settings toggle buttons' textures need to stick ----> Muikku did it
    //  -Feedback screen doesn't play well with different screen sizes ----> Should be a bit better now
    //  -Buttons might be a bit too small for mobile in general? ----> Now resized except the GameScreen buttons
    //  -Add a container for King's dialogue and use FreetypeFonts to display text ----> Done -Melentjeff
    //  -Choose a better FreeType Font to resemble the theme more
    //  LOCALIZATION
    //  -Change all ImageButtons to TextButtons for localization purposes
    //  -Implement localisation and make the language button save to prefs

    // remember to give an instance of 'this' to all new screens, if anything
    // from this class or parent class is to be used in that screen.

    private final AssetManager assetManager = new AssetManager();
    private SpriteBatch batch;
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
    private boolean gameEnd;


    @Override
    public void create() {
        batch = new SpriteBatch();
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
        batch.dispose();
        assetManager.dispose();
    }

    SpriteBatch getSpriteBatch() {
        return batch;
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

    void setCurrentLevel(int x) {
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

    public void clearSaveState() {
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
            //clearSaveState();
            this.currentLevel = 0;
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
            setPollutionLevel(-15);
        } else if (scoring == 750) {
            setPollutionLevel(-8);
        } else if (scoring == 500){
            setPollutionLevel(-2);
        } else if (scoring == 200) {
            setPollutionLevel(5);
        } else if (scoring == -100) {
            setPollutionLevel(10);
        }
    }

    void setLevelThrows(int levelThrows) {
        this.levelThrows = Integer.toString(levelThrows);
    }

}
