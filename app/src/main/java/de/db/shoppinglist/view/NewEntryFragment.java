package de.db.shoppinglist.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import de.db.shoppinglist.R;
import de.db.shoppinglist.database.GlideExtension;
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
    private EntryHistoryElement historyEntry;
    private String entryName;
    private NewEntryViewModel viewModel;
    private TakenImageSVM takenImageSVM;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_entry, container, false);
        findViewsById(view);
        initViews();
        image.setOnClickListener(v -> navigateToTakeImage());
        observeImage();
        return view;
    }

    private void observeImage() {
        viewModel.getImageLiveData().observe(getViewLifecycleOwner(), takenImage -> {
            if(takenImage != null) {
                Glide.with(getContext())
                        .load(takenImage)
                        .skipMemoryCache(false)
                        .into(image);
            }else{
                image.setImageResource(R.drawable.ic_shopping);
            }
        });
    }

    private void initViews() {
        if(entryName == null){
            nameOfProductEditText.setText(historyEntry.getName());
            unitOfQuantityEditText.setText(historyEntry.getUnitOfQuantity());
            detailsEditText.setText(historyEntry.getDetails());
        }else{
            nameOfProductEditText.setText(entryName);
        }
        assignImageInViewModel();
    }

    private void navigateToTakeImage() {
        takenImageSVM.setImage(viewModel.getImage());
        NavController navController = NavHostFragment.findNavController(NewEntryFragment.this);
        NavDirections navDirections = NewEntryFragmentDirections.actionNewEntryFragmentToCameraFragmentAlt2();
        navController.navigate(navDirections);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        viewModel = new ViewModelProvider(requireActivity()).get(NewEntryViewModel.class);
        takenImageSVM = new ViewModelProvider(requireActivity()).get(TakenImageSVM.class);
        getNavigationArguments();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                takenImageSVM.reset();
                viewModel.reset();
                NavController navController = NavHostFragment.findNavController(NewEntryFragment.this);
                navController.navigateUp();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    private void getNavigationArguments() {
        NewEntryFragmentArgs newEntryFragmentArgs = NewEntryFragmentArgs.fromBundle(getArguments());
        list = newEntryFragmentArgs.getList();
        historyEntry = newEntryFragmentArgs.getEntry();
        entryName = newEntryFragmentArgs.getName();
    }

    private void assignImageInViewModel() {
        if(noHistoryEntryProvided() || historyProvidesNoImage()){
            viewModel.setImageLiveData(takenImageSVM.getImage());
        }else{
            if(imageWasReseted()){
                viewModel.setImageLiveData(null);
            }else if(imageWasReplaced()){
                viewModel.setImageLiveData(takenImageSVM.getImage());
            }else{
                viewModel.setImageLiveData(historyEntry.getImageURI());
            }
        }
    }

    private boolean noHistoryEntryProvided() {
        return historyEntry == null;
    }

    private boolean imageWasReplaced() {
        return takenImageSVM.getImage() != null && !takenImageSVM.getImage().isEmpty();
    }

    private boolean imageWasReseted() {
        return takenImageSVM.resetedImage();
    }

    private boolean historyProvidesNoImage() {
        return historyEntry.getImageURI() == null;
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
        Uri imageUri = getImageUri();
        viewModel.addNewEntry(list, quantity, unitOfQuantity, nameOfProduct, details, imageUri, getContext());
        takenImageSVM.reset();
        viewModel.reset();
        closeFragment();
    }

    private Uri getImageUri() {
        Uri imageUri = viewModel.getImageLiveData().getValue();
        if(imageUri != null) {
            return imageUri;
        }
        return null;
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
