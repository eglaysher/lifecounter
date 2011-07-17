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
package org.elliotglaysher.lifecounter.coin2d;

import org.elliotglaysher.lifecounter.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class Coin2DActivity extends Activity {
    static private final String CURRENT_STATE = "CURRENT_STATE";

    private Coin2DView coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin2d);

        // Have the system blur any windows behind this one.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        // Send input to the only view on screen
        coin = (Coin2DView) findViewById(R.id.coin2dview);
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
