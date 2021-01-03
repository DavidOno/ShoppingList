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
import de.db.shoppinglist.ifc.ModifyTakenImageSVM;
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
    private ModifyTakenImageSVM takenImageSVM;

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
        initFields();
        entryId = getDocumentId();
        setUpViewModel();
        setTitle();
        imageView.setOnClickListener(v -> modifyImage());
        deleteButton.setOnClickListener(v -> deleteEntryAndFinish());
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
        NavDirections modifyImageDirection = ModifyEntryFragmentDirections.actionModifyEntryFragmentToModifyTakeImageFragment();
        navController.navigate(modifyImageDirection);
    }

    private void setImageAsArgument() {
        if(entry.getImageURI() != null && !entry.getImageURI().isEmpty())
            takenImageSVM.setImage(Uri.parse(entry.getImageURI()));
    }

    private String getTitlePrefix(){
        return getResources().getString(MODIFY_RESOURCE)+" ";
    }

    private void setTitle() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getTitlePrefix() + entry.getName());
    }

    private void setUpViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ModifyEntryViewModel.class);
    }

    private void deleteEntryAndFinish() {
        deleteEntry();
        closeFragment();
    }

    private void deleteEntry() {
        viewModel.deleteEntry(list.getUid(), entryId);
    }

    private String getDocumentId() {
        return entry.getUid();
    }


    private void initFields() {
        nameOfProductEditText.setText(entry.getName());
        quantityEditText.setText(String.valueOf(entry.getQuantity()));
        unitOfQuantityEditText.setText(entry.getUnitOfQuantity());
        detailsEditText.setText(entry.getDetails());
        doneCheckbox.setChecked(entry.isDone());
        setImage();
        handleDoneChecked();
    }

    private void setImage() {
        if(isUriValid()){
            imageView.setImageURI(takenImageSVM.getImageLiveData().getValue());
            return;
        }
        if(isDownloadLinkValid()) {
            Glide.with(getContext())
                    .load(entry.getImageURI())
                    .skipMemoryCache(false)
                    .into(imageView);
            return;
        }
    }

    private boolean isUriValid() {
        boolean hasContent =  takenImageSVM.getImageLiveData().getValue() != null && !takenImageSVM.getImageLiveData().getValue().toString().isEmpty();
        if(hasContent)
            return !takenImageSVM.getImageLiveData().getValue().toString().startsWith("https");
        return false;
    }

    private boolean isDownloadLinkValid() {
        return entry.getImageURI() != null && !entry.getImageURI().isEmpty() && entry.getImageURI().startsWith("https");
    }

    private void handleDoneChecked() {
        doneCheckbox.setOnClickListener(v -> {
            viewModel.toggleDoneStatus(list, entry);
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ModifyEntryFragmentArgs modifyEntryFragmentArgs = ModifyEntryFragmentArgs.fromBundle(getArguments());
        list = modifyEntryFragmentArgs.getList();
        entry = modifyEntryFragmentArgs.getEntry();
        takenImageSVM = new ViewModelProvider(requireActivity()).get(ModifyTakenImageSVM.class);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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

            }
        };
    }

    private void finishFragment() {
        float quantity = Float.parseFloat(quantityEditText.getText().toString());
        String unitOfQuantity = unitOfQuantityEditText.getText().toString();
        String nameOfProduct = nameOfProductEditText.getText().toString();
        String details = detailsEditText.getText().toString();
        boolean done = doneCheckbox.isChecked();
        setValues(quantity, unitOfQuantity, nameOfProduct, details, done);
        String imageUri = null;
        if(!takenImageSVM.isEmpty()) {
            imageUri = takenImageSVM.getImageLiveData().getValue().toString();
//            viewModel.modifyImageOfEntry(list, entry, imageUri, getContext());
        }
        viewModel.modifyEntry(list, entry, imageUri, getContext());

        takenImageSVM.reset();
        closeFragment();
    }


    private void setValues(float quantity, String unitOfQuantity, String nameOfProduct, String details, boolean done) {
        entry.setQuantity(quantity);
        entry.setUnitOfQuantity(unitOfQuantity);
        entry.setName(nameOfProduct);
        entry.setDetails(details);
        entry.setDone(done);
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
