package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (" +
                "id integer primary key autoincrement," +
                "name text not null," +
                "quantity real not null check (quantity > 0), " +
                "unit text not null," +
                "expiry_date text)");

        db.execSQL("CREATE TABLE recipes (" +
                "id integer primary key autoincrement," +
                "name text not null unique," +
                "method text not null)");
        db.execSQL("CREATE TABLE recipe_ingredients(" +
                "id integer primary key autoincrement," +
                "recipe_id integer not null," +
                "name text not null," +
                "quantity real not null check(quantity > 0)," +
                "unit text not null," +
                "foreign key (recipe_id) references recipes(id) on delete cascade)");

        seedRecipes(db);

    }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry_items");

        onCreate(db);

    }

    //Pantry Items CRUD
    public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
        ContentValues values = pantryValues(name,quantity,unit, expiryDate);
        return getWritableDatabase().insert("pantry_items", null, values);

    }

    public int updatePantryItem(int id, String name, double quantity, String unit, String expiryDate) {
        ContentValues values = pantryValues(name, quantity, unit, expiryDate);
        return getWritableDatabase().update("pantry_items", values, "id = ?", new String[]{String.valueOf(id)});

    }

    public int deletePantryItem(int id) {
        return getWritableDatabase().delete("pantry_items", "id = ?", new String[]{String.valueOf(id)});
    }

    public PantryItem getPantryItem(int id) {
        try (Cursor cursor = getReadableDatabase().query("pantry_items", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return pantryFromCursor(cursor);
            }
        }
        return null;
    }

    private PantryItem pantryFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        double quantity = cursor.getColumnIndexOrThrow("quantity");
        String unit = String.valueOf(cursor.getColumnIndexOrThrow("unit"));
        int expiryIdx = cursor.getColumnIndexOrThrow("expiry_date");
        String expiryDate = cursor.isNull(expiryIdx)? null:cursor.getString(expiryIdx);

        return new PantryItem(id, name, quantity, unit, expiryDate);

    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> item = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("pantry_items", null, null, null, null, null, "name")) {
            while (cursor.moveToNext()) item.add(pantryFromCursor(cursor));
        }
        return item;

    }
//Recipe CRUD
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("recipes", null, null, null, null, null, "name")) {
            while ((cursor.moveToNext())) recipes.add(recipeFromCursor(cursor));
        }
        return recipes;


    }

    private Recipe recipeFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String method = cursor.getString(cursor.getColumnIndexOrThrow("method"));


        return new Recipe(id, name, method);
    }

    public Recipe getRecipe(int id) {
        try (Cursor cursor = getReadableDatabase().query("recipes", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor.moveToFirst()) return recipeFromCursor(cursor);
        }
        return null;
    }
//Recipe Ingredients CRUD
    public List<RecipeIngredient> getRecipeIngredients(int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("recipe_ingredients", null, "recipe_id = ?", new String[]{String.valueOf(recipeId)}, null, null, "name ASC")) {
            while (cursor.moveToNext()) {
                ingredients.add(new RecipeIngredient(
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        }
        return ingredients;
    }

    private ContentValues pantryValues(String name, double quantity, String unit, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim().toLowerCase());
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            values.putNull("expiry_date");
        } else {
            values.put("expiry_date", expiryDate.trim());

        }

    return values;
}

    private void seedRecipes(SQLiteDatabase db) {
    addRecipe(db, "Chickpea Curry", "1. Sauté onion and garlic.\n2. Add curry powder.\n3. Stir in chickpeas and simmer.",
            req("chickpeas", 1, "can"), req("onion", 1, "piece"), req("garlic", 2, "clove"), req("curry powder", 1, "tsp"));

    addRecipe(db, "Vegetable Stir Fry", "1. Heat oil.\n2. Add mixed vegetables and garlic.\n3. Stir fry with soy sauce.",
            req("mixed vegetables", 200, "g"), req("garlic", 2, "clove"), req("soy sauce", 15, "ml"));

    addRecipe(db, "Bean Chili", "1. Cook onion and garlic.\n2. Add beans, tomatoes, and chili powder.\n3. Simmer until thick.",
            req("beans", 1, "can"), req("tomatoes", 1, "can"), req("onion", 1, "piece"), req("chili powder", 1, "tsp"));

    addRecipe(db, "Lentil Soup", "1. Boil lentils with onion and carrot.\n2. Add stock cube.\n3. Simmer until tender.",
            req("lentils", 1, "cup"), req("onion", 1, "piece"), req("carrot", 1, "piece"), req("stock cube", 1, "piece"));

    addRecipe(db, "Garlic Flatbread", "1. Mix flour, salt, and water.\n2. Roll flat.\n3. Fry with garlic oil.",
            req("flour", 2, "cup"), req("salt", 1, "tsp"), req("water", 150, "ml"), req("garlic", 2, "clove"), req("oil", 15, "ml"));

    addRecipe(db, "Egg Fried Noodles", "1. Cook noodles.\n2. Scramble eggs.\n3. Stir fry with garlic and soy sauce.",
            req("instant noodles", 1, "pack"), req("egg", 2, "piece"), req("garlic", 2, "clove"), req("soy sauce", 15, "ml"));

    addRecipe(db, "Tomato Soup", "1. Cook onion and garlic.\n2. Add tomatoes and stock.\n3. Blend until smooth.",
            req("tomatoes", 1, "can"), req("onion", 1, "piece"), req("garlic", 2, "clove"), req("stock cube", 1, "piece"));

    addRecipe(db, "Potato Hash", "1. Dice potatoes and onion.\n2. Fry in oil until golden.",
            req("potato", 2, "piece"), req("onion", 1, "piece"), req("oil", 20, "ml"));

    addRecipe(db, "Simple Pancakes", "1. Mix flour, egg, milk, and sugar.\n2. Cook batter on pan until golden.",
            req("flour", 1, "cup"), req("egg", 1, "piece"), req("milk", 200, "ml"), req("sugar", 1, "tbsp"));

    addRecipe(db, "Garlic Rice", "1. Cook rice.\n2. Fry garlic in oil.\n3. Mix together.",
            req("rice", 200, "g"), req("garlic", 3, "clove"), req("oil", 15, "ml"));

    addRecipe(db, "Vegetable Pasta", "1. Boil pasta.\n2. Add sautéed vegetables.\n3. Toss with oil.",
            req("pasta", 100, "g"), req("mixed vegetables", 150, "g"), req("oil", 15, "ml"));

    addRecipe(db, "Spicy Beans", "1. Fry onion and garlic.\n2. Add beans and chili powder.\n3. Simmer until thick.",
            req("beans", 1, "can"), req("onion", 1, "piece"), req("garlic", 2, "clove"), req("chili powder", 1, "tsp"));

    addRecipe(db, "Carrot Soup", "1. Boil carrots and onion.\n2. Blend with stock.\n3. Serve hot.",
            req("carrot", 3, "piece"), req("onion", 1, "piece"), req("stock cube", 1, "piece"));

    addRecipe(db, "Rice Porridge", "1. Boil rice with milk.\n2. Stir until creamy.\n3. Sweeten with sugar.",
            req("rice", 100, "g"), req("milk", 250, "ml"), req("sugar", 1, "tbsp"));

    addRecipe(db, "Vegetable Omelette", "1. Beat eggs.\n2. Add chopped vegetables.\n3. Cook in oil.",
            req("egg", 2, "piece"), req("mixed vegetables", 100, "g"), req("oil", 15, "ml"));

    addRecipe(db, "Garlic Lentils", "1. Boil lentils.\n2. Fry garlic in oil.\n3. Mix together.",
            req("lentils", 1, "cup"), req("garlic", 3, "clove"), req("oil", 15, "ml"));

    addRecipe(db, "Vegetable Rice", "1. Cook rice.\n2. Add sautéed vegetables.\n3. Mix and serve.",
            req("rice", 200, "g"), req("mixed vegetables", 150, "g"), req("oil", 15, "ml"));

    addRecipe(db, "Fruit Oats", "1. Cook oats with milk.\n2. Add chopped fruit.\n3. Serve warm.",
            req("oats", 50, "g"), req("milk", 200, "ml"), req("apple", 1, "piece"));
}

    private RecipeIngredient req (String name, double quantity, String unit){
        return new RecipeIngredient(name, quantity, unit);
    }

    private void addRecipe(SQLiteDatabase db,String name, String method, RecipeIngredient...ingredients){
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("method", method);
        long recipeId = db.insertOrThrow("recipes", null, recipeValues);
        for (RecipeIngredient ingredient : ingredients){
            ContentValues values = new ContentValues();
            values.put("recipe_id", recipeId);
            values.put("name", ingredient.name());
            values.put("quantity", ingredient.quantity());
            values.put("unit", ingredient.unit());
            db.insertOrThrow("recipe_ingredients", null, values);

        }

    }
}















