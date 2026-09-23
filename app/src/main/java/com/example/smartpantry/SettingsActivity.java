package com.example.smartpantry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS =
            "pantry_settings";

    private static final String EXPIRY_ALERTS =
            "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat expirySwitch = findViewById(R.id.switchExpiryAlerts);

        SharedPreferences preferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        expirySwitch.setChecked(preferences.getBoolean(EXPIRY_ALERTS, true));

        expirySwitch.setOnCheckedChangeListener(
                (buttonView, checked) -> preferences.edit().putBoolean(EXPIRY_ALERTS, checked).apply());

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {

        findViewById(R.id.nav_pantry).setOnClickListener(v -> {

                    Intent intent = new Intent(this, MainActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);
                    finish();
                });

        findViewById(R.id.nav_recipes).setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));
    }
}