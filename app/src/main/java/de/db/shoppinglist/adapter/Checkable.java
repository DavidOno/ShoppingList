package de.db.shoppinglist.adapter;

import androidx.lifecycle.LiveData;

public interface Checkable<T> {

    LiveData<Boolean> getFlag();
    T getItem();
    void resetFlags();


}
