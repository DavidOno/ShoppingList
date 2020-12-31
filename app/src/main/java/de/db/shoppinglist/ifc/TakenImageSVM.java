package de.db.shoppinglist.ifc;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TakenImageSVM extends ViewModel {

    private MutableLiveData<Uri> imageLiveData = new MutableLiveData<>();

    public LiveData<Uri> getImageLiveData(){
        return imageLiveData;
    }


    public void setImage(Uri imageUri){
        imageLiveData.postValue(imageUri);
    }

    /**
     * Resets the properties of the viewmodel.
     * Should be called when the necessary information were extracted.
     * It's recommended to call this method always when leaving a specific fragment.
     */
    public void reset(){
        imageLiveData = new MutableLiveData<>();
    }
}