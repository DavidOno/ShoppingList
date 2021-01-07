package de.db.shoppinglist.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressor {

    public static final int BY_90_DEGREE = 90;
    public static final int BY_180_DEGREE = 180;
    public static final int BY_270_DEGREE = 270;
    private Context context;

    public ImageCompressor(Context context) {
        this.context = context;
    }

    public byte[] compress(Uri imageUri, int quality) {
        byte[] compressed = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            Bitmap rotatedBitmap;
            int orientation = getOrientation(imageUri);
            rotatedBitmap = rotateBitmap(bitmap, orientation);
            compressed = compress(quality, rotatedBitmap);
            bitmap.recycle();
            rotatedBitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressed;
    }

    private byte[] compress(int quality, Bitmap rotatedBitmap) {
        byte[] compressedImageBytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        compressedImageBytes = baos.toByteArray();
        return compressedImageBytes;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Bitmap rotatedBitmap;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, BY_90_DEGREE);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, BY_180_DEGREE);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, BY_270_DEGREE);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    private int getOrientation(Uri imageUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        ExifInterface ei = new ExifInterface(inputStream);
        return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
