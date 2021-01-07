package de.db.shoppinglist.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class NewEntryViewModel extends ViewModel {

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

    public void addNewEntry(ShoppingList list, float quantity, String unitOfQuantity, String nameOfProduct, String details, Uri imageUri, Context context) {
        int position = list.getNextFreePosition();
        String image = null;
        if(imageUri != null){
            image = imageUri.toString();
        }
        ShoppingEntry shoppingEntry = new ShoppingEntry(quantity, unitOfQuantity, nameOfProduct, details, position, image);
        repo.addEntry(list.getUid(), shoppingEntry, context);
    }

    public void reset(){
        imageLiveData = new MutableLiveData<>();
    }
}
