package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        recyclerPantry.setLayoutManager(
                new LinearLayoutManager(this));

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {

        List<PantryItem> items = db.getAllPantryItems();

        adapter.setItems(items);

        if (items.isEmpty()) {

            recyclerPantry.setVisibility(View.GONE);
            textEmptyPantry.setVisibility(View.VISIBLE);

        } else {

            recyclerPantry.setVisibility(View.VISIBLE);
            textEmptyPantry.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {

        LinearLayout recipes = findViewById(R.id.nav_recipes);

        LinearLayout settings = findViewById(R.id.nav_settings);

        recipes.setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));

        settings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }
}