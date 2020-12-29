package de.db.shoppinglist.ifc;

import android.media.Image;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TakenImageSVM extends ViewModel {

    private MutableLiveData<Image> imageLiveData = new MutableLiveData<>();

    public LiveData<Image> getImageLiveData(){
        return imageLiveData;
    }

    public void setImageLiveData(Image image){
        imageLiveData.postValue(image);
    }

    /**
     * Sets the reference of the stored image to null.
     * Reason: Viewmodels are long-living-objects and resources are limited on mobile devices.
     */
    public void freeImage(){
        imageLiveData.setValue(null);
    }
}
