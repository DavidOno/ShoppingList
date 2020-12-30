package de.db.shoppinglist.ifc;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TakenImageSVM extends ViewModel {

    private MutableLiveData<Drawable> imageLiveData = new MutableLiveData<>();
    private Uri imageUri;

    public LiveData<Drawable> getImageLiveData(){
        return imageLiveData;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImage(Drawable image, Uri imageUri){
        imageLiveData.postValue(image);
        this.imageUri = imageUri;
    }

    /**
     * Resets the properties of the viewmodel.
     * Should be called when the necessary information were extracted.
     */
    public void reset(){
        imageLiveData = new MutableLiveData<>();
        imageUri = null;
    }
}
