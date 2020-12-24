package de.db.shoppinglist.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.ModifyListDialogViewModel;
import de.db.shoppinglist.viewmodel.NewListDialogViewModel;

public class ModifyListDialog extends AppCompatDialogFragment {

    private EditText listNameEditText;
    private Button doneButton;
    private Button backButton;

    private ModifyListDialogViewModel viewModel;
    private ShoppingList list;

    public ModifyListDialog(){
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_modify_list, null);
        builder.setView(view);
        listNameEditText = view.findViewById(R.id.editText_list_name);
        doneButton = view.findViewById(R.id.new_list_dialog_doneButton);
        doneButton.setEnabled(false);
        backButton = view.findViewById(R.id.new_list_dialog_backButton);
        listNameEditText.addTextChangedListener(enableDoneMenuItemOnTextChange());
        listNameEditText.setText(list.getName());
        listNameEditText.requestFocus();
        doneButton.setOnClickListener(item -> finish());
        backButton.setOnClickListener(item -> closeDialog());
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        listNameEditText.addTextChangedListener(enableDoneMenuItemOnTextChange());
        viewModel = new ViewModelProvider(requireActivity()).get(ModifyListDialogViewModel.class);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModifyListDialogArgs shoppingListFragmentArgs = ModifyListDialogArgs.fromBundle(getArguments());
        list = shoppingListFragmentArgs.getList();
    }

    private void closeDialog() {
        getDialog().dismiss();
    }

    private TextWatcher enableDoneMenuItemOnTextChange() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(listNameEditText.getText().toString().isEmpty()){
                    doneButton.setEnabled(false);
                }else{
                    doneButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void finish() {
        String listName = listNameEditText.getText().toString();
        list.setName(listName);
        viewModel.updateListName(list);
        closeDialog();

    }
}