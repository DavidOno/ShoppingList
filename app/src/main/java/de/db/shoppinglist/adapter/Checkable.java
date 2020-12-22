package de.db.shoppinglist.adapter;

import androidx.lifecycle.LiveData;

import java.util.Optional;

public interface Checkable<T> {

    public LiveData<Boolean> getFlag();
    public T getItem();
    public void resetFlags();


}
