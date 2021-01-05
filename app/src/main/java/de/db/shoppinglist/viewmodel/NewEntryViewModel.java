package de.db.shoppinglist.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class NewEntryViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();
    private MutableLiveData<Uri> imageLiveData = new MutableLiveData<>();

    public Uri getImage(){
        return imageLiveData.getValue();
    }

    public boolean hasImage(){
        return imageLiveData.getValue() != null;
    }

    public MutableLiveData<Uri> getImageLiveData() {
        return imageLiveData;
    }

    public void setImageLiveData(Uri imageUri) {
        imageLiveData.setValue(imageUri);
    }

    public void addNewEntry(ShoppingList list, float quantity, String unitOfQuantity, String nameOfProduct, String details, Uri imageUri, Context context) {
        int position = list.getNextFreePosition();
        String downloadUri = null;
        boolean isImageUpdateRequired = true;
        if(imageUri != null) {
            if (isDownLoadUri(imageUri)) {
                downloadUri = imageUri.toString();
                imageUri = null;
                isImageUpdateRequired = false;
            }
        }
        ShoppingEntry shoppingEntry = new ShoppingEntry(quantity, unitOfQuantity, nameOfProduct, details, position, downloadUri);
        repo.addEntry(list.getUid(), shoppingEntry, imageUri, context, isImageUpdateRequired);
    }

    private boolean isDownLoadUri(Uri uploadImageUri) {
        return uploadImageUri.toString().startsWith("http");
    }

    public void reset() {
        imageLiveData = new MutableLiveData<>();
    }
}
