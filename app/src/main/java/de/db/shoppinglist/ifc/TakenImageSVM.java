package de.db.shoppinglist.ifc;

import android.graphics.drawable.Drawable;
import android.media.Image;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TakenImageSVM extends ViewModel {

    private MutableLiveData<Drawable> imageLiveData = new MutableLiveData<>();

    public LiveData<Drawable> getImageLiveData(){
        return imageLiveData;
    }

    public void setImageLiveData(Drawable image){
        imageLiveData.postValue(image);
    }

    /**
     * Sets the reference of the stored image to null.
     * Reason: Viewmodels are long-living-objects and resources are limited on mobile devices.
     */
    public void freeImage(){
        imageLiveData = new MutableLiveData<>();
    }
}
