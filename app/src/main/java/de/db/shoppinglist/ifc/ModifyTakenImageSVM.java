package de.db.shoppinglist.ifc;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ModifyTakenImageSVM extends ViewModel {

    private MutableLiveData<Uri> imageLiveData = new MutableLiveData<>();


    public LiveData<Uri> getImageLiveData(){
        return imageLiveData;
    }

    public void setImage(Uri imageUri){
        imageLiveData.postValue(imageUri);
    }

    public void reset(){
        imageLiveData = new MutableLiveData<>();
    }

    public boolean isEmpty(){
        return imageLiveData.getValue() == null;
    }

}
