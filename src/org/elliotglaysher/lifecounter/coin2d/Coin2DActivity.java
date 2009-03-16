package org.elliotglaysher.lifecounter.coin2d;

import org.elliotglaysher.lifecounter.R;

import android.app.Activity;
import android.os.Bundle;

public class Coin2DActivity extends Activity {
    static private final String CURRENT_STATE = "CURRENT_STATE";
    
    private Coin2DView coin;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin2d);
        
        // Send input to the only view on screen
        coin = (Coin2DView)findViewById(R.id.coin2dview);
        coin.requestFocus();
    }
    
    @Override
    protected void onPause() {
        coin.stopAnimation();
        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        coin.setCoinValue(savedInstanceState.getBoolean(CURRENT_STATE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CURRENT_STATE, coin.getCoinValue());
    }
}
