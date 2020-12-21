package de.db.shoppinglist.view;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingEntry;

public class ModifyEntryFragment extends Fragment {

    private EditText nameOfProductEditText;
    private EditText quantityEditText;
    private EditText unitOfQuantityEditText;
    private EditText detailsEditText;
    private CheckBox doneCheckbox;
    private String listName;
    private String oldDocumentId;
    private Button deleteButton;

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
        if(getArguments() != null){
            ShoppingEntry entry = ModifyEntryFragmentArgs.fromBundle(getArguments()).getEntry();
            initFields(entry);
            oldDocumentId = getDocumentId(entry);
        }
        deleteButton.setOnClickListener(v -> deleteEntryAndFinish());
    }

    private void deleteEntryAndFinish() {
        deleteEntry();
        closeFragment();
    }

    private void deleteEntry() {
        DocumentReference oldEntryRef = FirebaseFirestore.getInstance().collection("Lists").document(listName).collection("Entries").document(oldDocumentId);
        oldEntryRef.delete();
    }

    private String getDocumentId(ShoppingEntry entry) {
        return String.valueOf(entry.hashCode());
    }


    private void initFields(ShoppingEntry entry) {
        nameOfProductEditText.setText(entry.getName());
        quantityEditText.setText(String.valueOf(entry.getQuantity()));
        unitOfQuantityEditText.setText(entry.getUnitOfQuantity());
        detailsEditText.setText(entry.getDetails());
        doneCheckbox.setChecked(entry.isDone());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ModifyEntryFragmentArgs modifyEntryFragmentArgs = ModifyEntryFragmentArgs.fromBundle(getArguments());
        listName = modifyEntryFragmentArgs.getListName();
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
        float quantity = Float.parseFloat(quantityEditText.getText().toString());
        String unitOfQuantity = unitOfQuantityEditText.getText().toString();
        String nameOfProduct = nameOfProductEditText.getText().toString();
        String details = detailsEditText.getText().toString();
        boolean done = doneCheckbox.isChecked();
        ShoppingEntry entry = new ShoppingEntry(quantity, unitOfQuantity, nameOfProduct, details);
        entry.setDone(done);
        deleteEntry();
        String newDocumentId = getDocumentId(entry);
        DocumentReference modifyEntryRef = FirebaseFirestore.getInstance().collection("Lists").document(listName).collection("Entries").document(newDocumentId);
        modifyEntryRef.set(entry);
        closeFragment();
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
    }
}
