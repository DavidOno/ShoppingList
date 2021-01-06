package de.db.shoppinglist.view;

import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.db.shoppinglist.R;
import de.db.shoppinglist.ifc.TakenImageSVM;
import de.db.shoppinglist.viewmodel.TakenImageViewModel;

/**
 * Important parts taken from
 * https://github.com/bikashthapa01/basic-camera-app-android/blob/master/app/src/main/java/net/smallacademy/cameraandgallery/MainActivity.java
 * and adapted.
 */
public class TakeImageFragment extends Fragment {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private static final String CONTENT_URI_KEY = "Content_uri_key";
    private static final String WAS_REPLACED = "";
    private ImageView selectedImage;
    private Button cameraButton;
    private Button galleryButton;
    private Button removeButton;
    private String currentPhotoPath;
    private MenuItem done;
    private TakenImageSVM sharedViewModel;
    private TakenImageViewModel viewModel;
    private boolean wasEnabled = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_image, container, false);
        findViewsById(view);
        removeButton.setOnClickListener(v -> removeImage());
        removeButton.setVisibility(isDisplayed());
        cameraButton.setOnClickListener(v -> askCameraPermissions());
        galleryButton.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);
        });
        observeImage();
        return view;
    }

    public int isDisplayed(){
        return viewModel.hasImage() ? View.VISIBLE : View.INVISIBLE;
    }

    private void removeImage() {
        viewModel.setImage(WAS_REPLACED);
    }

    private void observeImage() {
        viewModel.getImageLiveData().observe(getViewLifecycleOwner(), uri -> {
            if(uri != null && !uri.isEmpty()) {
                Glide.with(getContext())
                        .load(Uri.parse(uri))
                        .into(selectedImage);
            }else{
                selectedImage.setImageResource(R.drawable.ic_launcher_background);
            }
            if(done != null) {
                done.setEnabled(true);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        viewModel = new ViewModelProvider(requireActivity()).get(TakenImageViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(TakenImageSVM.class);
        viewModel.setImage(sharedViewModel.getImage());
    }

    private void findViewsById(View view) {
        selectedImage = view.findViewById(R.id.displayImageView);
        cameraButton = view.findViewById(R.id.cameraBtn);
        galleryButton = view.findViewById(R.id.galleryBtn);
        removeButton = view.findViewById(R.id.take_image_delete_button);
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(getContext(), "Camera-permission is required to use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_done, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        done = menu.findItem(R.id.menu_done_doneButton);
        done.setEnabled(wasEnabled);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_done_doneButton:
                sharedViewModel.setImage(viewModel.getImage());
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigateUp();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK && data !=  null){
                File f = new File(currentPhotoPath);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                viewModel.setImage(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);
                done.setEnabled(true);
                removeButton.setVisibility(View.VISIBLE);
            }else{
                done.setEnabled(false);
            }
        }
        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK && data != null){
                Uri contentUri = data.getData();
                viewModel.setImage(contentUri);
                done.setEnabled(true);
                removeButton.setVisibility(View.VISIBLE);
            }else{
                done.setEnabled(false);
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "de.db.shoppinglist.file_provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
            outState.putBoolean("done-status", done.isEnabled());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            wasEnabled = savedInstanceState.getBoolean("done-status");
        }
    }
}
