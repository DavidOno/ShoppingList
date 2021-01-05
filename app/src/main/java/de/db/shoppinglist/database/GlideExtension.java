package de.db.shoppinglist.database;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.repository.ShoppingRepository;

public class GlideExtension {

    private static ShoppingRepository repo = ShoppingRepository.getInstance();
    
    public static void loadExpirableImage(Context context, Uri imageUri, ImageView view){
        Glide.with(context)
                .load(imageUri)
                .skipMemoryCache(false)
                .into(view);
        repo.updateImageExpirationDate(imageUri);
    }
}
