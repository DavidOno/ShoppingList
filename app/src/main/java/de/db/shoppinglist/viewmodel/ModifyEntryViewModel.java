package de.db.shoppinglist.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.ModifyEntryFragment}.
 * Since most of the data are EditText, only the image is stored in this viewmodel.
 */
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

    /**
     * Deletes an entry from a specific list.
     *
     * @param listUid     Id of the list, containing this entry.
     * @param documentUid Id of the entry, which should be deleted.
     */
    public void deleteEntry(String listUid, String documentUid){
        repo.deleteEntry(listUid, documentUid);
    }

    /**
     * Updates if the entry is done or not.
     *
     * @param list List containing the entry.
     * @param entry  The entry, with the new done-status.
     */
    public void toggleDoneStatus(ShoppingList list, ShoppingEntry entry) {
        entry.setDone(!entry.isDone());
        repo.updateDoneStatus(list.getUid(), entry);
    }

    /**
     * Updates the complete entry.
     *
     * @param list    List containing the entry, which is supposed to be updated.
     * @param entry   The modified entry.
     * @param context The application context.
     */
    public void modifyEntry(ShoppingList list, ShoppingEntry entry, Context context){
        repo.modifyWholeEntry(list, entry, context);
    }


}
