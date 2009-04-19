package org.elliotglaysher.lifecounternonfree.coin2d;

import java.util.Random;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import org.elliotglaysher.lifecounternonfree.R;

public class Coin2DView extends ImageView implements View.OnClickListener, AnimationListener {
    static private boolean HEADS = false;
    static private boolean TAILS = true;
    
    private final Handler guiThread = new Handler();
    private final Random generator = new Random();
    
    private final Drawable heads;
    private final Drawable tails;
    
    private final Animation shrink_animation;
    private final Animation grow_animation;    
    
    private boolean coin_value = HEADS;
    private boolean coin_to_display = HEADS;

    private int spins_remaining = 0;
    
    public Coin2DView(Context context, AttributeSet attrs) {
        super(context, attrs);        

        heads = getResources().getDrawable(R.drawable.penny_heads);
        heads.setFilterBitmap(true);
        tails = getResources().getDrawable(R.drawable.penny_tails);
        tails.setFilterBitmap(true);
        setImageDrawable(heads);
    
        shrink_animation = AnimationUtils.loadAnimation(context, R.anim.coin_shrink);
        shrink_animation.setAnimationListener(this);
        grow_animation = AnimationUtils.loadAnimation(context, R.anim.coin_grow);
        grow_animation.setAnimationListener(this);
        
        setOnClickListener(this);
    }

    public void onClick(View v) {
        Animation a = getAnimation();
        if (a == null || a.hasEnded()) {
            boolean new_coin_value = generator.nextBoolean();
            startAnimation(shrink_animation);
            
            spins_remaining = 1;
            if (new_coin_value == coin_value)
                spins_remaining += 1;
            else
                spins_remaining += 2;
            
            coin_value = new_coin_value;
            startAnimation(shrink_animation);
        }
    }

    public void stopAnimation() {
        coin_to_display = coin_value;
    }

    public boolean getCoinValue() {
        return coin_value;
    }

    public void setCoinValue(boolean coin_value) {
        this.coin_value = coin_value;
        coin_to_display = coin_value;
    }

    @Override
    public void onAnimationEnd(final Animation animation) {
        guiThread.post(new Runnable() {
            public void run() {
                if (spins_remaining > 0) {                    
                    if (animation == shrink_animation) {
                        if (coin_to_display == HEADS) {
                            setImageDrawable(tails);
                            coin_to_display = TAILS;
                        } else {
                            setImageDrawable(heads);                                                        
                            coin_to_display = HEADS;
                        }
                        
                        startAnimation(grow_animation);
                        spins_remaining--;
                    } else {
                        startAnimation(shrink_animation);
                    }                    
                }
            }
        });
    }
    
    // Ignored AnimationListener methods.
    @Override
    public void onAnimationRepeat(Animation animation) {  }
    
    @Override
    public void onAnimationStart(Animation animation) {  }
}
