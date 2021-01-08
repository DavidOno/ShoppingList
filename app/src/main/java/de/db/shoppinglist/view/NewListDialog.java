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
import de.db.shoppinglist.viewmodel.NewListDialogViewModel;

/**
 * This Dialog allows to create a new list.
 * Currently only a name has to be provided.
 */
public class NewListDialog extends AppCompatDialogFragment {

    private EditText listNameEditText;
    private Button doneButton;
    private Button backButton;

    private NewListDialogViewModel viewModel;

    public NewListDialog(){
        //empty constructor is required
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = inflateDialogLayout();
        Dialog dialog = buildDialog(view);
        findViewsById(view);
        listNameEditText.addTextChangedListener(enableDoneMenuItemOnTextChange());
        doneButton.setEnabled(false);
        listNameEditText.requestFocus();
        doneButton.setOnClickListener(item -> finish());
        backButton.setOnClickListener(item -> closeDialog());
        listNameEditText.addTextChangedListener(enableDoneMenuItemOnTextChange());
        viewModel = new ViewModelProvider(requireActivity()).get(NewListDialogViewModel.class);
        return dialog;
    }

    private Dialog buildDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    private View inflateDialogLayout() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        return inflater.inflate(R.layout.dialog_new_list, null);
    }

    private void findViewsById(View view) {
        listNameEditText = view.findViewById(R.id.editText_list_name);
        doneButton = view.findViewById(R.id.new_list_dialog_doneButton);
        backButton = view.findViewById(R.id.new_list_dialog_backButton);
    }

    private void closeDialog() {
        getDialog().dismiss();
    }

    private TextWatcher enableDoneMenuItemOnTextChange() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //not required
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
                //not required
            }
        };
    }

    private void finish() {
        String listName = listNameEditText.getText().toString();
        ShoppingList shoppingList = new ShoppingList(listName);
        viewModel.addList(shoppingList);
        closeDialog();
    }
}
