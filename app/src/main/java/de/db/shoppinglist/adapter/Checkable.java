package de.db.shoppinglist.adapter;

import androidx.lifecycle.LiveData;

/**
 * This interface allows to check specific items within a recyclerview and
 * transport the information about the clicked item to its corresponding view.
 * The main goal is the provide an option to communicate between view and recyclerview without
 * coupling them tightly.
 *
 * @param <T> The type, which is represented by an item.
 */
public interface Checkable<T> {

    /**
     * Allows to observe, when an item was checked.
     * @return Returns boolean-livedata indicating that an item was checked.
     */
    LiveData<Boolean> getFlag();

    /**
     * Returns the item, which was checked.
     * Should be called within the observation of {@link #getFlag()}.
     * @return Returns the last checked item.
     */
    T getItem();

    /**
     * Resets the flag and the last-checked item.
     */
    void resetFlags();


}
