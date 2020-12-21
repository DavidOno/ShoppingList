package de.db.shoppinglist.database;

import de.db.shoppinglist.model.ShoppingElement;
import de.db.shoppinglist.model.ShoppingList;

public interface Source {
    ShoppingList getShoppingList(String name);

    public void addEntry();

    public void modifyEntry();

    public void deleteEntry(ShoppingElement item);

    public void addList();

    public void modifyList();

    public void deleteList();
}

