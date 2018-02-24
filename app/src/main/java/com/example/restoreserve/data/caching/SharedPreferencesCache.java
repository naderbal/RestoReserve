package com.example.restoreserve.data.caching;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 *
 * <p>
 *     Class providing basic implementation of Shared
 *     Preferences storage.
 * </p>
 */
public abstract class SharedPreferencesCache {
    private SharedPreferences prefData;
    private SharedPreferences.Editor prefDataEditor;

    public SharedPreferencesCache(Context context) {
        try { // initialize
            prefData = context.getSharedPreferences(getName(), Context.MODE_PRIVATE);
            prefDataEditor = prefData.edit();
        }
        catch (Exception e) {
            // nothing to be done
        }
    }

    /**
     * Returns the current preferences file name.
     */
    protected abstract String getName();

    /**
     * Clears all data stored in Shared Preferences under the current name.
     */
    public void clearCache() {
        try {
            prefDataEditor.clear();
            prefDataEditor.commit();
        }
        catch (Exception e) {
            // nothing to be done
        }
    }

    /**
     * Removes a value from shared preferences with the given key, if found.
     */
    protected void remove(@NonNull String key) {
        prefDataEditor.remove(key).commit();
    }

    /**
     * Stores a String in Shared Preferences under the given key.
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     * @return true if the value was stored, false otheriwse.
     */
    protected boolean storeString(String key, String value) {
        try {
            prefDataEditor.putString(key, value);
            prefDataEditor.commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Returns the preference value if it exists and it's
     * a String, or defValue.
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     */
    protected String getString(String key, String defValue) {
        try {
            return prefData.getString(key, null);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * Stores an Int value in Shared Preferences under the given key.
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     * @return true if the value was stored, false otheriwse.
     */
    protected boolean storeInt(String key, int value) {
        try {
            prefDataEditor.putInt(key, value);
            prefDataEditor.commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Returns the preference value if it exists and it's
     * an Int value, or defValue.
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this key does not exist.
     */
    protected int getInt(String key, int defValue) {
        try {
            return prefData.getInt(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * Stores a Long value in Shared Preferences under the given key.
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     * @return true if the value was stored, false otheriwse.
     */
    protected boolean storeLong(String key, long value) {
        try {
            prefDataEditor.putLong(key, value);
            prefDataEditor.commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Returns the preference value if it exists and it's
     * Long value, or defValue.
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this key does not exist.
     */
    protected long getLong(String key, long defValue) {
        try {
            return prefData.getLong(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * Stores a Boolean value in Shared Preferences under the given key.
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     * @return true if the value was stored, false otheriwse.
     */
    protected boolean storeBoolean(String key, boolean value) {
        try {
            prefDataEditor.putBoolean(key, value);
            prefDataEditor.commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Returns the preference value if it exists and it's
     * a Boolean value, or defValue.
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this key does not exist.
     */
    protected boolean getBoolean(String key, boolean defValue) {
        try {
            return prefData.getBoolean(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }
}
