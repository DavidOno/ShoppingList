package de.db.shoppinglist.ifc;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TakenImageSVM extends ViewModel {

    private MutableLiveData<String> imageLiveData = new MutableLiveData<>();

    public LiveData<String> getImageLiveData(){
        return imageLiveData;
    }

    public String getImage(){
        return imageLiveData.getValue();
    }

    public boolean hasImage(){
        return getImage() != null;
    }

    public void setImage(String imageUri){
        imageLiveData.setValue(imageUri);
    }

    public void setImage(Uri imageUri){
        if(imageUri != null) {
            imageLiveData.setValue(imageUri.toString());
        }else{
            imageLiveData.setValue(null);
        }
    }

    public boolean resetedImage(){
        if(getImage() != null) {
            return getImage().isEmpty();
        }
        return false;
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
