package de.db.shoppinglist.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ModifyEntryViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    private MutableLiveData<Uri> imageLiveData = new MutableLiveData<>();

    public MutableLiveData<Uri> getImageLiveData() {
        return imageLiveData;
    }

    public void setImageLiveData(String uri) {
        if(uri != null && !uri.isEmpty()) {
            imageLiveData.setValue(Uri.parse(uri));
        }else{
            imageLiveData.setValue(null);
        }
    }

    public Uri getImage(){
        return imageLiveData.getValue();
    }

    public void deleteEntry(String listUid, String documentUid){
        repo.deleteEntry(listUid, documentUid);
    }

    public void toggleDoneStatus(ShoppingList list, ShoppingEntry entry) {
        entry.setDone(!entry.isDone());
        repo.updateDoneStatus(list.getUid(), entry);
    }

    public void modifyEntry(ShoppingList list, ShoppingEntry entry, Context context){
        repo.modifyWholeEntry(list, entry, context);
    }


}
