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

import java.util.Random;

import org.elliotglaysher.lifecounter.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class Coin2DView extends ViewFlipper implements View.OnClickListener {
    static private boolean HEADS = false;
    static private boolean TAILS = true;

    private final Random generator = new Random();

    private final Drawable heads;
    private final Drawable tails;

    private boolean coin_value = HEADS;
    private final ImageView viewOne;
    private final ImageView viewTwo;

    private ImageView nextView;

    public Coin2DView(Context context, AttributeSet attrs) {
        super(context, attrs);

        heads = getResources().getDrawable(R.drawable.penny_heads);
        heads.setFilterBitmap(true);
        tails = getResources().getDrawable(R.drawable.penny_tails);
        tails.setFilterBitmap(true);

        setInAnimation(inFromRightAnimation());
        setOutAnimation(outToLeftAnimation());

        viewOne = new ImageView(context);
        addView(viewOne);

        viewTwo = new ImageView(context);
        addView(viewTwo);

        setOnClickListener(this);

        nextView = viewOne;
        setCoinValue(generator.nextBoolean());
    }

    public void onClick(View v) {
        Animation in = getInAnimation();
        Animation out = getOutAnimation();
        if ((!in.hasStarted() || in.hasEnded())
                && (!out.hasStarted() || out.hasEnded())) {
            setCoinValue(generator.nextBoolean());
            showNext();
        }
    }

    public boolean getCoinValue() {
        return coin_value;
    }

    public void setCoinValue(boolean coin_value) {
        this.coin_value = coin_value;

        // Set the value of the next view
        nextView.setImageDrawable(coin_value ? tails : heads);
        nextView = nextView == viewTwo ? viewOne : viewTwo;
    }

    private Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
}
