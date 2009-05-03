package org.elliotglaysher.lifecounternonfree;

import org.elliotglaysher.lifecounternonfree.widgets.DefaultEditTextPreference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class Settings extends PreferenceActivity
		implements SharedPreferences.OnSharedPreferenceChangeListener {    
	private SharedPreferences sharedPreferences;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        
        setPreferenceScreen(createPreferenceHierarchy());
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// Regenerate when something changed.
		setPreferenceScreen(createPreferenceHierarchy());
	}
	
    private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        // General preferences
        PreferenceCategory player_preferences = new PreferenceCategory(this);
        player_preferences.setTitle(R.string.player_preferences);
        root.addPreference(player_preferences);

        ListPreference num_players = new ListPreference(this);
        num_players.setEntries(R.array.num_players);
        num_players.setEntryValues(R.array.num_players);
        num_players.setDialogTitle(R.string.number_players);
        num_players.setKey(Constants.NUM_PLAYERS_KEY);
        num_players.setTitle(R.string.number_players);
        num_players.setDefaultValue("2");
        player_preferences.addPreference(num_players);
        
        int number_of_players = getNumPlayersFromPreferences(); 
        for (int i = 0; i < number_of_players; ++i) {
        	PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
        	screen.setKey(Constants.SCREEN_PREFERENCE_KEY[i]);
        	screen.setTitle(Constants.DEFAULT_NAME_KEY[i]);
        	player_preferences.addPreference(screen);
        	
        	PreferenceCategory per_player = new PreferenceCategory(this);
        	per_player.setKey(Constants.CATEGORY_PREFERENCE_KEY[i]);
        	per_player.setTitle(Constants.DEFAULT_NAME_KEY[i]);
        	screen.addPreference(per_player);        	
        	
        	DefaultEditTextPreference name = new DefaultEditTextPreference(this);
        	name.setDialogTitle(R.string.player_name_label);
        	name.setKey(Constants.NAME_PREFERENCE_KEY[i]);
        	name.setTitle(R.string.player_name_label);
        	name.setDefaultValue(getString(Constants.DEFAULT_NAME_KEY[i]));
        	per_player.addPreference(name);
        	
            ListPreference theme = new ListPreference(this);
            theme.setEntries(R.array.themes);
            theme.setEntryValues(R.array.themes);
            theme.setDialogTitle(R.string.player_theme_label);
            theme.setKey(Constants.THEME_PREFERENCE_KEY[i]);
            theme.setTitle(R.string.player_theme_label);
            theme.setDefaultValue("Black");
            per_player.addPreference(theme);        	
        }

        return root;
    }

    private int getNumPlayersFromPreferences() {        
        try {
            return Integer.parseInt(sharedPreferences.getString(Constants.NUM_PLAYERS_KEY, "2"));
        } catch (NumberFormatException e) {
            return 2;
        }
    }
}
