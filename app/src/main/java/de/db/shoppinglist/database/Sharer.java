package de.db.shoppinglist.database;

import de.db.shoppinglist.model.ShoppingList;

/**
 * This interface procides functions, necessary to share/communicate between different users.
 */
public interface Sharer {

    /**
     * Enables sharing of a single shopping-list.
     *
     * @param list  List to share.
     * @param email Email to identify the receiving party.
     */
    void share(ShoppingList list, String email);
}
