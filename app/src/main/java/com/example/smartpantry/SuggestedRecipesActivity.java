package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SuggestedRecipesActivity
        extends AppCompatActivity {

    private DatabaseHelper db;
    private LinearLayout recipeContainer;
    private TextView textNoRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        db = new DatabaseHelper(this);

        recipeContainer = findViewById(R.id.recipeContainer);

        textNoRecipes = findViewById(R.id.textNoRecipes);

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {

        recipeContainer.removeAllViews();

        List<Recipe> recipes = MatchRecipes.strictMatch(db);

        if (recipes.isEmpty()) {

            textNoRecipes.setVisibility(View.VISIBLE);
            return;
        }

        textNoRecipes.setVisibility(View.GONE);

        for (Recipe recipe : recipes) {

            TextView recipeView = new TextView(this);

            recipeView.setText(recipe.name());
            recipeView.setTextSize(18);
            recipeView.setPadding(20, 30, 20, 30);

            recipeView.setBackgroundColor(
                    0xFFFFFFFF
            );

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(0, 0, 0, 16);
            recipeView.setLayoutParams(params);

            recipeView.setOnClickListener(v -> {

                Intent intent = new Intent(this, RecipeDetailActivity.class);

                intent.putExtra("recipe_id", recipe.id());

                startActivity(intent);
            });

            recipeContainer.addView(recipeView);
        }
    }

    private void setupBottomNavigation() {

        findViewById(R.id.nav_pantry).setOnClickListener(v -> {

            Intent intent = new Intent(this, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
                    finish();
                });

        findViewById(R.id.nav_settings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }
}