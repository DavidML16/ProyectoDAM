package morales.david.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import morales.david.android.R;

public class DisconnectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnected);

        getSupportActionBar().hide();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sgh_preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove(getString(R.string.sgh_preference_user));
        editor.remove(getString(R.string.sgh_preference_pass));
        editor.commit();

    }

}