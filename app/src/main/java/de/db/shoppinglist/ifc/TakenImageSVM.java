package de.db.shoppinglist.ifc;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * A shared viewmodel, for communicating between {@link de.db.shoppinglist.view.NewEntryFragment}/
 * {@link de.db.shoppinglist.view.ModifyEntryFragment} and {@link de.db.shoppinglist.view.TakeImageFragment}.
 * This viewmodel shares only the uri of the image.
 * The communication is bidirectional.
 */
public class TakenImageSVM extends ViewModel {

    private MutableLiveData<String> imageLiveData = new MutableLiveData<>();

    public String getImage() {
        return imageLiveData.getValue();
    }

    public void setImage(String imageUri) {
        imageLiveData.setValue(imageUri);
    }

    public void setImage(Uri imageUri) {
        if (imageUri != null) {
            imageLiveData.setValue(imageUri.toString());
        } else {
            imageLiveData.setValue(null);
        }
    }

    public boolean resetedImage() {
        if (getImage() != null) {
            return getImage().isEmpty();
        }
        return false;
    }

    /**
     * Resets the properties of the viewmodel.
     * Should be called when the necessary information were extracted.
     * It's recommended to call this method always when leaving a fragment, where the image was not
     * taken.
     */
    public void reset() {
        imageLiveData = new MutableLiveData<>();
    }
}
