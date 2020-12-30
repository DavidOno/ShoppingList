package de.db.shoppinglist.ifc;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;

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

}
