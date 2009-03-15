package org.elliotglaysher.lifecounter.coin2d;

import org.elliotglaysher.lifecounter.R;

import android.app.Activity;
import android.os.Bundle;

public class Coin2DActivity extends Activity {
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
}
