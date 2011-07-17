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

import org.elliotglaysher.lifecounternonfree.coin2d.Coin2DActivity;
import org.elliotglaysher.lifecounternonfree.coin2d.Coin2DActivityHC;
import org.elliotglaysher.lifecounternonfree.widgets.LifeLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LifeCounter extends Activity {
    private int numPlayers;
    private final LifeLayout lifeLayout[] = new LifeLayout[Constants.MAX_PLAYERS];

    private SharedPreferences sharedPreferences;
    private PowerManager.WakeLock wakeLock;

    // Defined in Honeycomb SDK, but we want to be compatible.
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;
    public static final int SHOW_AS_ACTION = SHOW_AS_ACTION_IF_ROOM
            | SHOW_AS_ACTION_WITH_TEXT;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setupLayouts();

        if (savedInstanceState != null) {
            restoreModelsFromBundle(savedInstanceState);
        }

        // Prevent the screen from totally going to sleep...
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

        newGame();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (numPlayers != getNumPlayersFromPreferences()) {
            // The order of opponents has been switched. Rehook all this stuff
            // up.
            numberPlayersChanged();
        }

        for (int i = 0; i < Constants.MAX_PLAYERS; ++i) {
            if (lifeLayout[i] != null) {
                lifeLayout[i].setTheme(sharedPreferences.getString(
                        Constants.THEME_PREFERENCE_KEY[i], "Black"));

                lifeLayout[i].setName(sharedPreferences.getString(
                        Constants.NAME_PREFERENCE_KEY[i],
                        getString(Constants.DEFAULT_NAME_KEY[i])));
            }
        }

        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();

        wakeLock.release();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreModelsFromBundle(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveModelsToBundle(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuCompat.setShowAsAction(menu.findItem(R.id.coin_menu),
                SHOW_AS_ACTION);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.new_game_menu:
            newGame();
            return true;
        case R.id.coin_menu:
            commitPendingChanges();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                startActivity(new Intent(this, Coin2DActivity.class));
            } else {
                startActivity(new Intent(this, Coin2DActivityHC.class));
            }
            break;
        case R.id.settings_menu:
            commitPendingChanges();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                startActivity(new Intent(this, Settings.class));
            } else {
                startActivity(new Intent(this, SettingsHC.class));
            }
            return true;
        case R.id.about_menu:
            commitPendingChanges();
            showAboutBox();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutBox() {
        new AlertDialog.Builder(this).setTitle(R.string.about_title).setIcon(
                R.drawable.icon).setView(
                getLayoutInflater().inflate(R.layout.about, null, false))
                .setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    private void newGame() {
        for (int i = 0; i < Constants.MAX_PLAYERS; ++i) {
            if (lifeLayout[i] != null) {
                lifeLayout[i].newGame();
            }
        }
    }

    private void commitPendingChanges() {
        for (int i = 0; i < Constants.MAX_PLAYERS; ++i) {
            if (lifeLayout[i] != null) {
                lifeLayout[i].commitPendingChanges();
            }
        }
    }

    private void numberPlayersChanged() {
        Bundle b = new Bundle();
        saveModelsToBundle(b);
        setupLayouts();
        restoreModelsFromBundle(b);
    }

    private void setupLayouts() {
        numPlayers = getNumPlayersFromPreferences();

        switch (numPlayers) {
        case 1:
            setContentView(R.layout.main_1_player);
            break;
        case 2:
            setContentView(R.layout.main_2_player);
            break;
        case 3:
            setContentView(R.layout.main_3_player);
            break;
        case 4:
            setContentView(R.layout.main_4_player);
            break;
        default:
            setContentView(R.layout.main_2_player);
            break;
        }

        lifeLayout[0] = (LifeLayout) findViewById(R.id.player_1_layout);
        lifeLayout[1] = (LifeLayout) findViewById(R.id.player_2_layout);
        lifeLayout[2] = (LifeLayout) findViewById(R.id.player_3_layout);
        lifeLayout[3] = (LifeLayout) findViewById(R.id.player_4_layout);
    }

    private int getNumPlayersFromPreferences() {
        try {
            return Integer.parseInt(sharedPreferences.getString(
                    Constants.NUM_PLAYERS_KEY, "2"));
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    private void saveModelsToBundle(Bundle outState) {
        for (int i = 0; i < Constants.MAX_PLAYERS; ++i) {
            if (lifeLayout[i] != null) {
                outState.putSerializable(Constants.MODEL_SAVE_KEY[i],
                        lifeLayout[i].getModelForSaving());
            }
        }
    }

    private void restoreModelsFromBundle(Bundle savedInstanceState) {
        for (int i = 0; i < Constants.MAX_PLAYERS; ++i) {
            if (lifeLayout[i] != null
                    && savedInstanceState
                            .containsKey(Constants.MODEL_SAVE_KEY[i])) {
                lifeLayout[i].setModelFromSave((LifeModel) savedInstanceState
                        .getSerializable(Constants.MODEL_SAVE_KEY[i]));
            }
        }
    }
}
