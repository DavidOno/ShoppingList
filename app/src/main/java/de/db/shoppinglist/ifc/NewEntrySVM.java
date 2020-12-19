package de.db.shoppinglist.ifc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;

public class NewEntrySVM extends ViewModel {

    private final MutableLiveData<ShoppingEntry> selected = new MutableLiveData<>();

    public void provide(ShoppingEntry item) {
        selected.setValue(item);
    }

    public LiveData<ShoppingEntry> getProvided() {
        return selected;
    }



}
