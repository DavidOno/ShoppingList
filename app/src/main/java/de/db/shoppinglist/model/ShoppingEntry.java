package de.db.shoppinglist.model;

import androidx.annotation.NonNull;

public class ShoppingEntry {

    private String amount;
    private boolean done;
    private ShoppingElement shoppingElement;

    public ShoppingEntry(String amount, ShoppingElement shoppingElement) {
        this.amount = amount;
        this.shoppingElement = shoppingElement;
        done = false;
    }

    public ShoppingEntry(ShoppingEntry other) {
        this.amount = other.amount;
        this.shoppingElement = other.shoppingElement;
        done = false;
    }

    public String getName() {
        return shoppingElement.getName();
    }
}
