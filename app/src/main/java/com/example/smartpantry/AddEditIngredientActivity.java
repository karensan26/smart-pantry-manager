package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editQuantity;
    private EditText editUnit;
    private EditText editExpiry;

    private DatabaseHelper db;

    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        db = new DatabaseHelper(this);

        editName = findViewById(R.id.editIngredientName);

        editQuantity = findViewById(R.id.editQuantity);

        editUnit = findViewById(R.id.inputUnit);

        editExpiry = findViewById(R.id.inputExpiryDate);

        TextView title = findViewById(R.id.screen_title);

        Button delete = findViewById(R.id.delete);

        itemId = getIntent().getIntExtra("item_id", -1);

        if (itemId != -1) {

            title.setText(R.string.edit_ingredient);
            delete.setVisibility(View.VISIBLE);

            loadItem();

        } else {

            title.setText(R.string.add_ingredient);
            delete.setVisibility(View.GONE);
        }

        findViewById(R.id.pick_date).setOnClickListener(v -> showDatePicker());

        findViewById(R.id.clear_date).setOnClickListener(v -> editExpiry.setText(""));

        findViewById(R.id.save_item).setOnClickListener(v -> saveItem());

        findViewById(R.id.cancel).setOnClickListener(v -> finish());

        delete.setOnClickListener(v -> deleteItem());

        setupBottomNavigation();
    }

    private void loadItem() {

        PantryItem item = db.getPantryItem(itemId);

        if (item == null) {
            finish();
            return;
        }

        editName.setText(item.name());

        editQuantity.setText(String.valueOf(item.quantity()));

        editUnit.setText(item.unit());

        if (item.expiryDate() != null) {
            editExpiry.setText(item.expiryDate());
        }
    }

    private void saveItem() {

        String name = editName.getText().toString().trim();

        String quantityText = editQuantity.getText().toString().trim();

        String unit = editUnit.getText().toString().trim();

        String expiry = editExpiry.getText().toString().trim();

        if (name.isEmpty()) {

            editName.setError("Ingredient name is required");

            return;
        }

        if (quantityText.isEmpty()) {

            editQuantity.setError("Quantity is required");

            return;
        }

        double quantity;

        try {

            quantity = Double.parseDouble(quantityText);

        } catch (NumberFormatException e) {

            editQuantity.setError("Enter a valid quantity");

            return;
        }

        if (quantity <= 0) {

            editQuantity.setError("Quantity must be greater than 0");

            return;
        }

        if (unit.isEmpty()) {

            editUnit.setError("Unit is required");

            return;
        }

        String expiryValue = expiry.isEmpty() ? null : expiry;

        if (itemId == -1) {

            db.addPantryItem(name, quantity, unit, expiryValue);

            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();

        } else {

            db.updatePantryItem(itemId, name, quantity, unit, expiryValue);

            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void deleteItem() {

        if (itemId == -1) {
            return;
        }

        db.deletePantryItem(itemId);

        Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {

            String date = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, day);editExpiry.setText(date);},
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    private void setupBottomNavigation() {

        LinearLayout pantry = findViewById(R.id.nav_pantry);

        LinearLayout recipes = findViewById(R.id.nav_recipes);

        LinearLayout settings = findViewById(R.id.nav_settings);

        pantry.setOnClickListener(v -> {finish();});

        recipes.setOnClickListener(v -> {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));

            finish();
        });

        settings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));

            finish();
        });
    }
}