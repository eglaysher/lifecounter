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
package org.elliotglaysher.lifecounter;

public class Constants {
    static public final int MAX_PLAYERS = 4;
    static public final String THEME_PREFERENCE_KEY[] = new String[]{
        "player_1_theme", "player_2_theme", "player_3_theme", "player_4_theme"
    };
    static public final String NAME_PREFERENCE_KEY[] = new String[]{
        "player_1_name", "player_2_name", "player_3_name", "player_4_name"
    };
    static public final String MODEL_SAVE_KEY[] = new String[]{
        "PLAYER_1_MODEL", "PLAYER_2_MODEL", "PLAYER_3_MODEL", "PLAYER_4_MODEL"
    };

    // Psuedo-preference used as a key to save/restore state.
    static public final String SCREEN_PREFERENCE_KEY[] = new String[]{
        "player_1_screen", "player_2_screen", "player_3_screen", "player_4_screen"
    };

    // Psuedo-preference used as a key to save/restore state.
    static public final String CATEGORY_PREFERENCE_KEY[] = new String[]{
        "player_1_cat", "player_2_cat", "player_3_cat", "player_4_cat"
    };
    
    static public final String NUM_PLAYERS_KEY = "num_players";
    
    static public final int DEFAULT_NAME_KEY[] = new int[]{
        R.string.player_1_label, R.string.player_2_label,
        R.string.player_3_label, R.string.player_4_label
    };
}
