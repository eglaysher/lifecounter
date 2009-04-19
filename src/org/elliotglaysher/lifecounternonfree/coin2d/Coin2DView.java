package org.elliotglaysher.lifecounternonfree.coin2d;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import org.elliotglaysher.lifecounternonfree.R;

public class Coin2DView extends View implements View.OnClickListener {
    static private final int ANIMATION_CYCLE = 15;
    static private final int SPIN_SPEED = 600 / ANIMATION_CYCLE;
    static private final int NORMAL_SIZE = 280;
    static private boolean HEADS = false;
//    static private boolean TAILS = true;
    
    static private final int ANIM_STATE_SHRINK = 0;
    static private final int ANIM_STATE_GROW = 1;
    private int animation_state;
    
    private final Handler guiThread = new Handler();
    private final Random generator = new Random();
    
    private final Drawable heads;
    private final Drawable tails;
    
    private boolean coin_value = HEADS;
    private boolean coin_to_display = HEADS;

    private int spins_remaining = 0;
    private int spin = NORMAL_SIZE;
    
    private boolean running_animation = false;
    
    public Coin2DView(Context context, AttributeSet attrs) {
        super(context, attrs);        

        heads = getResources().getDrawable(R.drawable.penny_heads);
        tails = getResources().getDrawable(R.drawable.penny_tails);
        
        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centeredX = (getWidth() / 2) - (spin / 2);
        int centeredY = (getHeight() / 2) - (NORMAL_SIZE / 2);
        
        if (coin_to_display == HEADS) {
            heads.setBounds(centeredX, centeredY, centeredX + spin, centeredY + NORMAL_SIZE);
            heads.draw(canvas);            
        } else {
            tails.setBounds(centeredX, centeredY, centeredX + spin, centeredY + NORMAL_SIZE);
            tails.draw(canvas);
        }
    }

    public void onClick(View v) {
        if (!running_animation) {
            boolean new_coin_value = generator.nextBoolean();
            
            spins_remaining = 1;
            if (new_coin_value == coin_value)
                spins_remaining += 1;
            else
                spins_remaining += 2;
            
            coin_value = new_coin_value;
            animation_state = ANIM_STATE_SHRINK;
            
            invalidate();

            running_animation = true;
            guiThread.postDelayed(spinAction, ANIMATION_CYCLE);
        }
    }
    
    private Runnable spinAction = new Runnable() {
        public void run() {
            boolean done = updateAnimation();
            if (!done) {
                guiThread.postDelayed(spinAction, ANIMATION_CYCLE);
            } else {
                running_animation = false;
            }
        }
    };

    public boolean updateAnimation() {
        boolean done = false;
        if (animation_state == ANIM_STATE_SHRINK) {
            spin -= SPIN_SPEED;
            
            if (spin <= 0) {
                spin = 0;
                animation_state = ANIM_STATE_GROW;
                coin_to_display = !coin_to_display;
            }
        } else if (animation_state == ANIM_STATE_GROW) {
            spin += SPIN_SPEED;
            
            if (spin >= NORMAL_SIZE) {
                spin = NORMAL_SIZE;
                
                spins_remaining--;
                if (spins_remaining <= 0) {
                    spins_remaining = 0;
                    done = true;
                } else {
                    animation_state = ANIM_STATE_SHRINK;
                }
            }
        }
        
        invalidate();
        return done;
    }
    
    public void stopAnimation() {
        spin = NORMAL_SIZE;
        coin_to_display = coin_value;
        running_animation = false;        
        guiThread.removeCallbacks(spinAction);
    }

    public boolean getCoinValue() {
        return coin_value;
    }

    public void setCoinValue(boolean coin_value) {
        this.coin_value = coin_value;
        coin_to_display = coin_value;
    }
}
