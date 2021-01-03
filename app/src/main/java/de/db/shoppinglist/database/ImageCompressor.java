package de.db.shoppinglist.database;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressor {

    private Context context;

    public ImageCompressor(Context context) {
        this.context = context;
    }

    public byte[] compress(Uri imageUri, int quality) {
        byte[] compressed = null;
        try {
//            InputStream is = context.getContentResolver().openInputStream(imageUri);
//            BitmapFactory.Options dbo = new BitmapFactory.Options();
//            dbo.inJustDecodeBounds = true;
//            Bitmap bitmap = BitmapFactory.decodeStream(is, null, dbo);
//            is.close();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.toString(), options);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
//            ContentResolver cr = context.getContentResolver();
//            InputStream in = cr.openInputStream(imageUri);
//            Bitmap bitmap = BitmapFactory.decodeStream(in,null,null);
            Bitmap rotatedBitmap;
            int orientation = getOrientation(imageUri);
            rotatedBitmap = rotateBitmap(bitmap, orientation);
            compressed = compress(imageUri, quality, bitmap, rotatedBitmap);
            bitmap.recycle();
            rotatedBitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressed;
    }

    private byte[] compress(Uri imageUri, int quality, Bitmap bitmap, Bitmap rotatedBitmap) throws IOException {
        byte[] compressedImageBytes = null;
//        if (rotatedBitmap != bitmap) {//TODO: remove???
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            compressedImageBytes = baos.toByteArray();
//        }
        return compressedImageBytes;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Bitmap rotatedBitmap;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
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
