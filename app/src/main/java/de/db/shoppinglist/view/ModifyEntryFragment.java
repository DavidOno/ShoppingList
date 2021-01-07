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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import de.db.shoppinglist.R;
import de.db.shoppinglist.ifc.TakenImageSVM;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.ModifyEntryViewModel;

public class ModifyEntryFragment extends Fragment {

    public static final int MODIFY_RESOURCE = R.string.modify_;
    private EditText nameOfProductEditText;
    private EditText quantityEditText;
    private EditText unitOfQuantityEditText;
    private EditText detailsEditText;
    private CheckBox doneCheckbox;
    private ImageView imageView;
    private ShoppingList list;
    private Button deleteButton;
    private String entryId;
    private ShoppingEntry entry;
    private ModifyEntryViewModel viewModel;
    private TakenImageSVM takenImageSVM;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_entry, container, false);
        findViewsById(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigationArguments();
        initFields();
        setTitle();
        imageView.setOnClickListener(v -> modifyImage());
        deleteButton.setOnClickListener(v -> deleteEntryAndFinish());
        customizeBackPress();
    }

    private void customizeBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                takenImageSVM.reset();
                NavController navController = NavHostFragment.findNavController(ModifyEntryFragment.this);
                navController.navigateUp();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }


    private void modifyImage() {
        setImageAsArgument();
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections modifyImageDirection = ModifyEntryFragmentDirections.actionModifyEntryFragmentToCameraFragmentAlt2();
        navController.navigate(modifyImageDirection);
    }

    private void setImageAsArgument() {
        takenImageSVM.setImage(viewModel.getImage());
    }

    private String getTitlePrefix(){
        return getResources().getString(MODIFY_RESOURCE)+" ";
    }

    private void setTitle() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getTitlePrefix() + entry.getName());
    }

    private void deleteEntryAndFinish() {
        deleteEntry();
        closeFragment();
    }

    private void deleteEntry() {
        viewModel.deleteEntry(list.getUid(), entryId);
    }

    private void initFields() {
        nameOfProductEditText.setText(entry.getName());
        quantityEditText.setText(String.valueOf(entry.getQuantity()));
        unitOfQuantityEditText.setText(entry.getUnitOfQuantity());
        detailsEditText.setText(entry.getDetails());
        doneCheckbox.setChecked(entry.isDone());
        entryId = entry.getUid();
        observeImage();
        handleDoneChecked();
    }

    private void observeImage() {
        viewModel.getImageLiveData().observe(getViewLifecycleOwner(), takenImage -> {
            if(takenImage != null) {
                Glide.with(getContext())
                        .load(takenImage)
                        .skipMemoryCache(false)
                        .into(imageView);
            }else{
                imageView.setImageResource(R.drawable.ic_shopping);
            }
        });
    }

    private void handleDoneChecked() {
        doneCheckbox.setOnClickListener(v -> viewModel.toggleDoneStatus(list, entry));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        takenImageSVM = new ViewModelProvider(requireActivity()).get(TakenImageSVM.class);
        viewModel = new ViewModelProvider(requireActivity()).get(ModifyEntryViewModel.class);
    }

    private void getNavigationArguments() {
        ModifyEntryFragmentArgs modifyEntryFragmentArgs = ModifyEntryFragmentArgs.fromBundle(getArguments());
        list = modifyEntryFragmentArgs.getList();
        entry = modifyEntryFragmentArgs.getEntry();
        assignImageInViewModel();
    }

    private void assignImageInViewModel() {
        if(toModifingEntryProvidesNoImage()){
            viewModel.setImageLiveData(takenImageSVM.getImage());
        }else{
            if(imageWasReseted()){
                viewModel.setImageLiveData(null);
            }else if(imageWasReplaced()){
                viewModel.setImageLiveData(takenImageSVM.getImage());
            }else{
                viewModel.setImageLiveData(entry.getImageURI());
            }
        }
    }

    private boolean toModifingEntryProvidesNoImage() {
        return entry.getImageURI() == null;
    }

    private boolean imageWasReseted() {
        return takenImageSVM.resetedImage();
    }

    private boolean imageWasReplaced() {
        return takenImageSVM.getImage() != null && !takenImageSVM.getImage().isEmpty();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_done, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        MenuItem done = getDoneMenuItem(menu);
        nameOfProductEditText.addTextChangedListener(enableDoneMenuItemOnTextChange(done));
        done.setEnabled(true);
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
                //not required
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = nameOfProductEditText.getText().toString();
                if(name.isEmpty()){
                    doneMenuItem.setEnabled(false);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getTitlePrefix() + name);
                }else{
                    doneMenuItem.setEnabled(true);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getTitlePrefix() + name);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //not required
            }
        };
    }

    private void finishFragment() {
        float quantity = Float.parseFloat(quantityEditText.getText().toString());
        String unitOfQuantity = unitOfQuantityEditText.getText().toString();
        String nameOfProduct = nameOfProductEditText.getText().toString();
        String details = detailsEditText.getText().toString();
        boolean done = doneCheckbox.isChecked();
        Uri imageUri = viewModel.getImage();
        setValues(quantity, unitOfQuantity, nameOfProduct, details, done, imageUri);
        viewModel.modifyEntry(list, entry, getContext());
        takenImageSVM.reset();
        closeFragment();
    }


    private void setValues(float quantity, String unitOfQuantity, String nameOfProduct, String details, boolean done, Uri imageUri) {
        entry.setQuantity(quantity);
        entry.setUnitOfQuantity(unitOfQuantity);
        entry.setName(nameOfProduct);
        entry.setDetails(details);
        entry.setDone(done);
        String image = null;
        if(imageUri != null){
            image = imageUri.toString();
        }
        entry.setImageURI(image);
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
    }

    private void findViewsById(View view) {
        nameOfProductEditText = view.findViewById(R.id.modify_entry_nameOfProductEditText);
        quantityEditText = view.findViewById(R.id.modify_entry_quantityEditText);
        unitOfQuantityEditText = view.findViewById(R.id.modify_entry_unitOfQuantityEditText);
        detailsEditText = view.findViewById(R.id.modify_entry_detailsEditText);
        doneCheckbox = view.findViewById(R.id.modify_entry_done_checkbox);
        deleteButton = view.findViewById(R.id.modify_entry_deleteButton);
        imageView = view.findViewById(R.id.modify_entry_imageView);
    }
}
