package com.example.smartpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PantryAdapter extends ListAdapter<PantryItem, PantryAdapter.PantryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private final OnItemClickListener listener;

    private static final DiffUtil.ItemCallback<PantryItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<PantryItem>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull PantryItem oldItem,
                        @NonNull PantryItem newItem) {
                    return oldItem.id() == newItem.id();
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull PantryItem oldItem,
                        @NonNull PantryItem newItem) {
                    return Objects.equals(oldItem.name(), newItem.name())
                            && Double.compare(oldItem.quantity(), newItem.quantity()) == 0
                            && Objects.equals(oldItem.unit(), newItem.unit())
                            && Objects.equals(oldItem.expiryDate(), newItem.expiryDate());
                }
            };

    public PantryAdapter(List<PantryItem> items, OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        setItems(items);
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pantry_item, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = getItem(position);

        Context context = holder.itemView.getContext();

        holder.name.setText(item.name());

        holder.quantity.setText(context.getString(R.string.pantry_quantity_format, formatQuantity(item.quantity()), item.unit()));

        if (item.expiryDate() == null || item.expiryDate().trim().isEmpty()) {

            holder.expiry.setText(R.string.pantry_no_expiry);

        } else {

            holder.expiry.setText(context.getString(R.string.pantry_expiry_format, item.expiryDate()));
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(getItem(currentPosition));
            }
        });
    }



    public void setItems(List<PantryItem> items) {
        submitList(new ArrayList<>(items));
    }

    private String formatQuantity(double quantity) {

        if (quantity == Math.floor(quantity)) {
            return String.valueOf((int) quantity);
        }

        return String.valueOf(quantity);
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView quantity;
        TextView expiry;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textPantryName);

            quantity = itemView.findViewById(R.id.textPantryQuantity);

            expiry = itemView.findViewById(R.id.textPantryExpiry);
        }
    }
}