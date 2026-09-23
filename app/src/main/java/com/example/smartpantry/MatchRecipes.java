package com.example.smartpantry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MatchRecipes {

    public static List<Recipe> strictMatch(DatabaseHelper db) {
        List<Recipe> matchedRecipes = new ArrayList<>();
        List<PantryItem> userPantryItems = db.getAllPantryItems();

        for (Recipe recipe : db.getAllRecipes()) {
            boolean isMatchingRecipe = true;

            for (RecipeIngredient ingredient : db.getRecipeIngredients(recipe.id())) {
                Amount amountNeeded = getBaseAmount(ingredient.quantity(), ingredient.unit());
                double available = 0;

                for (PantryItem pantryItem : userPantryItems) {
                    if (sameName(pantryItem.name(), ingredient.name())) {
                        Amount amountInPantry = getBaseAmount(pantryItem.quantity(), pantryItem.unit());

                        if (amountInPantry.unit.equals(amountNeeded.unit)) {
                            available += amountInPantry.amount;
                        }
                    }
                }

                if (available < amountNeeded.amount) {
                    isMatchingRecipe = false;
                    break;
                }
            }

            if (isMatchingRecipe) {
                matchedRecipes.add(recipe);
            }
        }

        return matchedRecipes;
    }

    private static boolean sameName(String a, String b) {
        return normalizeName(a).equals(normalizeName(b));
    }

    public static String normalizeName(String value) {
        String name = value.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
        if (name.equals("tomatoes")) return "tomato";
        if (name.equals("potatoes")) return "potato";
        if (name.equals("eggs")) return "egg";
        if (name.equals("apples")) return "apple";
        if (name.equals("bananas")) return "banana";
        if (name.equals("oranges")) return "orange";

        if (name.endsWith("s") && !name.endsWith("ss")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    private static Amount getBaseAmount(double amount, String unit) {
        unit = unit.toLowerCase(Locale.ROOT).trim();
        switch (unit) {
            case "kg":
                return new Amount(amount * 1000, "grams");
            case "g":
            case "gram":
            case "grams":
                return new Amount(amount, "grams");
            case "l":
            case "litre":
            case "liter":
                return new Amount(amount * 1000, "ml");
            case "ml":
                return new Amount(amount, "ml");
            case "tbsp":
                return new Amount(amount * 15, "ml");
            case "tsp":
                return new Amount(amount * 5, "ml");
            case "piece":
            case "pieces":
            case "item":
            case "items":
                return new Amount(amount, "count");
            case "slice":
            case "slices":
                return new Amount(amount, "slice");
            default:
                return new Amount(amount, unit);
            case "clove":
            case "cloves":
                return new Amount(amount, "clove");
            case"cup":
                case "cups":
                    return new Amount(amount, "cup");
            case "can":
                case "cans":
                    return new Amount(amount, "can");
            case "pack":
                case "packs":
                    return new Amount(amount, "pack");
            case "bunch":
                case "bunches":
                    return new Amount(amount, "bunch");



        }
    }

    private static class Amount {
        final double amount;
        final String unit;

        private Amount(double amount, String unit) {
            this.amount = amount;
            this.unit = unit;
        }
    }
}
