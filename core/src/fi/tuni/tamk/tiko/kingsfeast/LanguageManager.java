package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Locale;

/**
 * LanguageManager class handles everything related to localization. Different bundles are saved to
 * ObjectMap.
 */
class LanguageManager {
    private ObjectMap<String, I18NBundle> languages;
    private String currentLanguage;

    /**
     * Default Constructor initiates language and currentLanguage variables.
     */
    LanguageManager() {
        languages = new ObjectMap<>();
        currentLanguage = null;
    }

    /**
     * This constructor takes in language name, language location (fileHandle) in assets, and locale
     * and puts it into ObjectMap for ease of access in the future.
     * @param name Name of the language
     * @param fileHandle Location of the .pref file
     * @param locale Locale (en/fi)
     */
    void loadLanguage(String name, FileHandle fileHandle, Locale locale) {
        if(name!=null && !name.isEmpty() && fileHandle != null && locale != null)
            languages.put(name.toLowerCase(), I18NBundle.createBundle(fileHandle, locale));
    }

    /**
     * Sets the current language.
     * @param name Language name.
     */
    void setCurrentLanguage(String name) {
        if(languages.containsKey(name.toLowerCase()))
            currentLanguage = name;
    }

    /**
     * Gets the current bundle in use.
     * @return Current bundle.
     */
    I18NBundle getCurrentBundle() {
        return languages.get(currentLanguage);
    }
}
