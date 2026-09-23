package com.example.smartpantry;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId == -1) {
            finish();
            return;
        }
        Recipe recipe;
        List<RecipeIngredient> ingredients;

        try (DatabaseHelper db = new DatabaseHelper(this)) {
            recipe = db.getRecipe(recipeId);

            if (recipe == null) {
                finish();
                return;
            }

            ingredients = db.getRecipeIngredients(recipeId);
        }

        TextView name = findViewById(R.id.textRecipeName);

        TextView method = findViewById(R.id.textRecipeMethod);

        LinearLayout ingredientsContainer = findViewById(R.id.ingredientContainer);

        name.setText(recipe.name());
        method.setText(recipe.method());


        for (RecipeIngredient ingredient : ingredients) {

            TextView textView = new TextView(this);

            String text = "• " + formatQuantity(ingredient.quantity()) + " " + ingredient.unit() + " " + ingredient.name();

            textView.setText(text);
            textView.setTextSize(16);
            textView.setPadding(0, 8, 0, 8);

            ingredientsContainer.addView(textView);
        }
    }

    private String formatQuantity(double value) {

        if (value == Math.floor(value)) {
            return String.valueOf((int) value);
        }

        return String.valueOf(value);
    }
}