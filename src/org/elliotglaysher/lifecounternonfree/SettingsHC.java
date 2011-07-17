package org.elliotglaysher.lifecounternonfree;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingsHC extends PreferenceActivity {
    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // app icon in Action Bar clicked; go home
            Intent intent = new Intent(this, LifeCounter.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.honeycomb_preference_headers, target);
    }
}
