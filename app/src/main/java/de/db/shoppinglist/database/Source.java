package de.db.shoppinglist.database;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public interface Source {

    public boolean addEntry(String listUid, ShoppingEntry entry);

//    public boolean modifyEntry(String uid, ShoppingEntry entry);

    public boolean deleteEntry(String listUid, String documentUid);

    public boolean addList(ShoppingList shoppingList);

    public boolean modifyList();

    public boolean deleteList(String listId);
}

