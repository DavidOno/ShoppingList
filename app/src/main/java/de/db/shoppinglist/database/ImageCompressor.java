package de.db.shoppinglist.database;

import android.net.Uri;

public interface ImageCompressor {

    /**
     * Compresses an image. But there's no guarantee of success. If so, null is returned.
     * The orientation of the image is determined before the compression, so that images appear
     * in the right orientation afterwards.
     * @param imageUri Device-intern uri of the image.
     * @param quality The quality of the image, remained after compression.
     * @return Returns bytes of the compressed image. If image was for some reason not compressable, null.
     */
    byte[] compress(Uri imageUri, int quality);
}
