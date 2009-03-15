package org.elliotglaysher.lifecounter;

import org.elliotglaysher.lifecounter.coin2d.Coin2DActivity;
import org.elliotglaysher.lifecounter.widgets.LifeLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LifeCounter extends Activity {
    private static final String OPPONENT_LIFE_MODEL = "OPPONENT_LIFE";
    private static final String YOUR_LIFE_MODEL = "YOUR_LIFE";
    private LifeLayout opponentLayout;
    private LifeLayout yourLayout;
    
    private SharedPreferences sharedPreferences;
    
    private PowerManager.WakeLock wakeLock;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        opponentLayout = (LifeLayout)findViewById(R.id.opponent_layout);
        yourLayout = (LifeLayout)findViewById(R.id.your_layout);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Prevent the screen from totally going to sleep...
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag"); 
        
        newGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        opponentLayout.setTheme(sharedPreferences.getString("opponent_theme", "Default"));
        yourLayout.setTheme(sharedPreferences.getString("your_theme", "Default"));

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

        opponentLayout.setModelFromSave(
                (LifeModel)savedInstanceState.getSerializable(OPPONENT_LIFE_MODEL));
        yourLayout.setModelFromSave(
                (LifeModel)savedInstanceState.getSerializable(YOUR_LIFE_MODEL));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(OPPONENT_LIFE_MODEL,
                opponentLayout.getModelForSaving());
        outState.putSerializable(YOUR_LIFE_MODEL,
                yourLayout.getModelForSaving());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.new_game_menu:
            newGame();
            return true;
        case R.id.coin_menu:
            commitPendingChanges();
            startActivity(new Intent(this, Coin2DActivity.class));
            break;
        case R.id.settings_menu:
            commitPendingChanges();
            startActivity(new Intent(this, Settings.class));
            return true;
        case R.id.about_menu:
            commitPendingChanges();
            showAboutBox();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void showAboutBox() {
        new AlertDialog.Builder(this)
        .setTitle(R.string.about_title)
        .setIcon(R.drawable.icon)
        .setMessage(R.string.about_text)
        .setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        })
        .show();
    }

    private void newGame() {
        opponentLayout.newGame();
        yourLayout.newGame();
    }
    
    private void commitPendingChanges() {
        opponentLayout.commitPendingChanges();
        yourLayout.commitPendingChanges();
    }
}