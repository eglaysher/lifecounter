package org.elliotglaysher.lifecounternonfree.widgets;

import org.elliotglaysher.lifecounternonfree.LifeModel;
import org.elliotglaysher.lifecounternonfree.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LifeLayout extends LinearLayout {
    static private final int MIN_LIFE = -999;
    static private final int MAX_LIFE = 999;
    static private final int START_LIFE = 20;
    static private final int SPINNER_SPEED = 300;
    static private final int COMMIT_DELAY_MS = 2000;
    
    private LifeView lifeHistory;
    private NumberPicker spinner;
    private Handler guiThread;
    private LifeModel life;
    
    public LifeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.life_layout, this, true);

        guiThread = new Handler();
        
        // Read the name that's supposed to go on this column
        TypedArray array = context.obtainStyledAttributes(
                attrs, R.styleable.lifeLayout, 0, 0);
        String name = array.getString(R.styleable.lifeLayout_playerName);
        if (name == null) {
            name = "???";
        }
        TextView nameView = (TextView)findViewById(R.id.player_name); 
        nameView.setText(name);
        
        int visibility = array.getBoolean(R.styleable.lifeLayout_nameShown, true) ? VISIBLE : GONE;
        nameView.setVisibility(visibility);
        
        lifeHistory = (LifeView)findViewById(R.id.life_widget);
        spinner = (NumberPicker)findViewById(R.id.spinner_widget);
        spinner.setRange(MIN_LIFE, MAX_LIFE);
        spinner.setSpeed(SPINNER_SPEED);
        spinner.setOnChangeListener(new LifeChangeListener());

        newGame();
    }
    
    public void newGame() {
        life = new LifeModel();
        lifeHistory.setModel(life);
        spinner.setCurrent(START_LIFE);
        
        lifeHistory.onHistoryChanged();
    }
    
    public void commitPendingChanges() {
        guiThread.removeCallbacks(commitLifeScore);
        life.commitValue();            
    }

    public void setTheme(String theme) {
        lifeHistory.setTheme(theme);
    }
    
    public LifeModel getModelForSaving() {
        commitPendingChanges();
        return life;
    }

    public void setModelFromSave(LifeModel in) {
        commitPendingChanges();
        life = in;
        lifeHistory.setModel(life);
        spinner.setCurrent(life.getLife());
        
        lifeHistory.onHistoryChanged();
    }
    
    private class LifeChangeListener implements NumberPicker.OnChangedListener {
        public void onChanged(NumberPicker spinner, int oldVal, int newVal) {
            // The default behaviour of NumberPicker is to overflow; prevent 
            // us from going over MAX_LIFE or going under MIN_LIFE.
            if (oldVal == MIN_LIFE && newVal == MAX_LIFE) {
                spinner.setCurrent(MIN_LIFE);
            } else if (oldVal == MAX_LIFE && newVal == MIN_LIFE) {
                spinner.setCurrent(MAX_LIFE);
            } else {
                handleNewValue(spinner.getId(), newVal);
            }
        }
    }

    private void handleNewValue(int id, int newVal) {
        life.setLife(newVal);
        lifeHistory.onHistoryChanged();
        
        guiThread.removeCallbacks(commitLifeScore);
        guiThread.postDelayed(commitLifeScore, COMMIT_DELAY_MS);
    }
    
    private Runnable commitLifeScore = new Runnable() {
        public void run() {
            life.commitValue();            
        }
    };
}
