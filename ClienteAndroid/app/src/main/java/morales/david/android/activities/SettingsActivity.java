package morales.david.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import morales.david.android.MainActivity;
import morales.david.android.R;
import morales.david.android.fragments.SettingsFragment;
import morales.david.android.utils.ActionBarUtil;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle(getString(R.string.act_settings_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarUtil.changeStyle(this, getSupportActionBar());

        getSupportFragmentManager().beginTransaction().replace(R.id.act_settings_frame, new SettingsFragment()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

}