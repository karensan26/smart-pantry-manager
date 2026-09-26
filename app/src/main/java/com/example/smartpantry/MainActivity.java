package com.example.smartpantry;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String Channel_Id = "expiry channel";
    private static final int Notification_Id = 1;
    private static final int Threshold_Days = 3;
    private static final String PREFS = "pantry_settings";
    private static final String EXPIRY_ALERTS = "expiry_alerts";


    private DatabaseHelper db;
    private PantryAdapter adapter;

    private RecyclerView recyclerPantry;
    private TextView textEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PantryAdapter(
                db.getAllPantryItems(), item -> {

            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);

            intent.putExtra("item_id", item.id());
            startActivity(intent);
        }
        );

        recyclerPantry.setAdapter(adapter);
        findViewById(R.id.buttonAddIngredient).setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);

            startActivity(intent);
        });

        setupBottomNavigation();
        setupNotifications();
    }

    private void setupBottomNavigation() {
        LinearLayout recipes = findViewById(R.id.nav_recipes);
        LinearLayout settings = findViewById(R.id.nav_settings);

        recipes.setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        settings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void setupNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(Channel_Id, "Expiry Alerts", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Alerts for expiring ingredients");
            manager.createNotificationChannel(channel);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        List<PantryItem> items = db.getAllPantryItems();
        loadPantry(items);
        checkExpiredIngredients(items);
    }

    private void loadPantry(List<PantryItem> items) {
        adapter.setItems(items);
        if (items.isEmpty()) {
            recyclerPantry.setVisibility(View.GONE);
            textEmptyPantry.setVisibility(View.VISIBLE);
        } else {
            recyclerPantry.setVisibility(View.VISIBLE);
            textEmptyPantry.setVisibility(View.GONE);
        }
    }

    private void checkExpiredIngredients(List<PantryItem> items) {
        SharedPreferences preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean alertsEnabled = preferences.getBoolean(EXPIRY_ALERTS, true);
        if (!alertsEnabled) {
            return;
        }

        List<String> expiringIngredients = new ArrayList<>();
        for (PantryItem item : items) {
            if (item.expiryDate() != null && item.isExpiringSoon(Threshold_Days)) {
                expiringIngredients.add(item.name());
            }
        }

        if (expiringIngredients.isEmpty()) {
            return;
        }

        String message = "The following ingredients are expiring soon: " + String.join(", ", expiringIngredients);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Channel_Id)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Ingredients Expiring Soon")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(Notification_Id, builder.build());
        }
    }
}