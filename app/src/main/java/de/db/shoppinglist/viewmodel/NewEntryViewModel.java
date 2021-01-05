package de.db.shoppinglist.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class NewEntryViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

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
}
