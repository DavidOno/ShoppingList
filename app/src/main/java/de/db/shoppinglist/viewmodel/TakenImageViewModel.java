package de.db.shoppinglist.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.TakeImageFragment}.
 * In this case only the image has to be stored in the viewmodel.
 */
public class TakenImageViewModel extends ViewModel {

    public static final String IS_EMPTY_IMAGE = "";
    private MutableLiveData<String> imageLiveData = new MutableLiveData<>();

    public LiveData<String> getImageLiveData() {
        return imageLiveData;
    }

    public String getImage() {
        return imageLiveData.getValue();
    }

    public boolean hasImage() {
        return getImage() != null;
    }

    public void setImage(Uri imageUri) {
        imageLiveData.setValue(imageUri.toString());
    }

    public void setImage(String imageUri) {
        if (imageUri != null && imageUri.isEmpty()) {
            imageLiveData.setValue(IS_EMPTY_IMAGE);
        } else if (imageUri != null) {
            imageLiveData.setValue(imageUri);
        } else {
            imageLiveData.setValue(null);
        }
    }
}
