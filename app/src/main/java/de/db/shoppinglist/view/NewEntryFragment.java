package de.db.shoppinglist.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.db.shoppinglist.R;
import de.db.shoppinglist.ifc.TakenImageSVM;
import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.NewEntryViewModel;

public class NewEntryFragment extends Fragment {


    private EditText nameOfProductEditText;
    private EditText quantityEditText;
    private EditText unitOfQuantityEditText;
    private EditText detailsEditText;
    private ImageView image;
    private ShoppingList list;
    private EntryHistoryElement entry;
    private String entryName;
    private NewEntryViewModel viewModel;
    private TakenImageSVM takenImageSVM;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_entry, container, false);
        findViewsById(view);
        if(entryName == null){
            nameOfProductEditText.setText(entry.getName());
            unitOfQuantityEditText.setText(entry.getUnitOfQuantity());
            detailsEditText.setText(entry.getDetails());
        }else{
            nameOfProductEditText.setText(entryName);
        }
        image.setOnClickListener(v -> inflatePopupMenu());
        takenImageSVM.getImageLiveData().observe(getViewLifecycleOwner(), takenImage -> {
            ByteBuffer buffer = takenImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            image.setImageBitmap(bitmapImage);
        });
        return view;
    }

    private void inflatePopupMenu() {
        PopupMenu popup = new PopupMenu(getContext(), image);
        popup.getMenuInflater()
                .inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Camera:
                        NavController navController = NavHostFragment.findNavController(NewEntryFragment.this);
                        NavDirections navDirections = NewEntryFragmentDirections.actionNewEntryFragmentToCameraFragmentAlt2();
                        navController.navigate(navDirections);
                        break;
                    case R.id.Archive: break;

                }

                return true;
            }
        });

        popup.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        handleNavigationArguments();
        viewModel = new ViewModelProvider(requireActivity()).get(NewEntryViewModel.class);
        takenImageSVM = new ViewModelProvider(requireActivity()).get(TakenImageSVM.class);
    }

    private void handleNavigationArguments() {
        getNavigationArguments();
    }

    private void getNavigationArguments() {
        NewEntryFragmentArgs newEntryFragmentArgs = NewEntryFragmentArgs.fromBundle(getArguments());
        list = newEntryFragmentArgs.getList();
        entry = newEntryFragmentArgs.getEntry();
        entryName = newEntryFragmentArgs.getName();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_done, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        MenuItem done = getDoneMenuItem(menu);
        nameOfProductEditText.addTextChangedListener(enableDoneMenuItemOnTextChange(done));
        done.setEnabled(!nameOfProductEditText.getText().toString().isEmpty());
        super.onCreateOptionsMenu(menu, inflater);
    }

    private MenuItem getDoneMenuItem(Menu menu) {
       return menu.findItem(R.id.menu_done_doneButton);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_done_doneButton){
            finishFragment();
        }
        return false;
    }

    private TextWatcher enableDoneMenuItemOnTextChange(MenuItem doneMenuItem) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(nameOfProductEditText.getText().toString().isEmpty()){
                    doneMenuItem.setEnabled(false);
                }else{
                    doneMenuItem.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void finishFragment() {
        float quantity = getQuantity();
        String unitOfQuantity = getString(unitOfQuantityEditText);
        String nameOfProduct = getString(nameOfProductEditText);
        String details = getString(detailsEditText);
        viewModel.addNewEntry(list, quantity, unitOfQuantity, nameOfProduct, details);
        closeFragment();
    }


    private float getQuantity(){
        String numberText = quantityEditText.getText().toString();
        if(numberText.isEmpty()){
            return 0f;
        }else{
            return Float.parseFloat(numberText);
        }
    }

    private String getString(EditText editText){
        return editText.getText().toString();
    }

    @Override
    public void onStop() {
        super.onStop();
        closeKeyBoard();
    }

    private void closeKeyBoard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

    }

    private void openKeyBoard(){
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(getView(), 0);
    }

    private void closeFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigateUp();
        navController.navigateUp();
    }

    private void findViewsById(View view) {
        nameOfProductEditText = view.findViewById(R.id.nameOfProductEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        unitOfQuantityEditText = view.findViewById(R.id.unitOfQuantityEditText);
        detailsEditText = view.findViewById(R.id.detailsEditText);
        image = view.findViewById(R.id.new_entry_imageView);
    }
}
