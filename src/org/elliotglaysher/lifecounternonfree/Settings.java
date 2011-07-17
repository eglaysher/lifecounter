/*
 * Copyright (C) 2009 Elliot Glaysher.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.elliotglaysher.lifecounternonfree;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class Settings extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        createPreferenceHierarchy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        // Regenerate when something changed.
        createPreferenceHierarchy();
    }

    private void createPreferenceHierarchy() {
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().removeAll();
        }
        addPreferencesFromResource(R.xml.preferences);

        // Remove each PreferenceScreen that
        int count = getNumPlayersFromPreferences();
        if (count < 4) {
            removePlayerPreferenceKey("player_4_screen");
        }
        if (count < 3) {
            removePlayerPreferenceKey("player_3_screen");
        }
        if (count < 2) {
            removePlayerPreferenceKey("player_2_screen");
        }
    }

    private int getNumPlayersFromPreferences() {
        try {
            return Integer.parseInt(sharedPreferences.getString(
                    Constants.NUM_PLAYERS_KEY, "2"));
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    private void removePlayerPreferenceKey(String key) {
        PreferenceScreen top_level = getPreferenceScreen();
        PreferenceCategory category = (PreferenceCategory) top_level
                .findPreference("player_category");

        Preference p = category.findPreference(key);
        category.removePreference(p);
    }
}
