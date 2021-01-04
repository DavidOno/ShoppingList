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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.ShareListViewModel;

public class ShareDialog extends AppCompatDialogFragment {
    private static final int SHARE = R.string.share;
    private EditText emailEditText;
    private Button doneButton;
    private Button backButton;
    private TextView listNameTextView;
    private ShareListViewModel viewModel;
    private ShoppingList list;

    public ShareDialog(){
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = inflateDialogLayout();
        Dialog dialog = buildDialog(view);
        getViewsById(view);
        doneButton.setEnabled(false);
        listNameTextView.setText(getResources().getString(SHARE)+": "+list.getName());
        emailEditText.requestFocus();
        doneButton.setOnClickListener(item -> finish());
        backButton.setOnClickListener(item -> closeDialog());
        emailEditText.addTextChangedListener(enableDoneMenuItemOnTextChange());
        viewModel = new ViewModelProvider(requireActivity()).get(ShareListViewModel.class);
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
        return inflater.inflate(R.layout.dialog_share_list, null);
    }

    private void getViewsById(View view) {
        listNameTextView = view.findViewById(R.id.dialog_share_list_name);
        emailEditText = view.findViewById(R.id.dialog_share_list_email_adress);
        doneButton = view.findViewById(R.id.dialog_share_list_doneButton);
        backButton = view.findViewById(R.id.dialog_share_list_backButton);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareDialogArgs shoppingListFragmentArgs = ShareDialogArgs.fromBundle(getArguments());
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
            public void onTextChanged(CharSequence email, int start, int before, int count) {
                if(isEmpty()){
                    doneButton.setEnabled(false);
                }else if(isGoogleMailAdress(email.toString().toLowerCase())){
                    doneButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private boolean isGoogleMailAdress(String email) {
        return email.endsWith("@gmail.com") || email.endsWith("@googlemail.com");
    }

    private boolean isEmpty() {
        return emailEditText.getText().toString().isEmpty();
    }

    private void finish() {
        String listName = emailEditText.getText().toString();
        list.setName(listName);
        viewModel.share(list);
        closeDialog();
    }
}
