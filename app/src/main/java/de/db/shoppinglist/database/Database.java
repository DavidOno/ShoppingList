package de.db.shoppinglist.database;

import de.db.shoppinglist.model.ShoppingList;

public interface Database {
    ShoppingList getShoppingList(String name);
}

