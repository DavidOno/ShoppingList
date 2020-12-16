package de.db.shoppinglist.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import de.db.shoppinglist.R;

public class NewListActivity extends Activity {

    private EditText nameOfListEditText;
    private MenuItem doneMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_list_activity);
        nameOfListEditText = findViewById(R.id.editText_list_name);

    }

    private TextWatcher enableDoneMenuItemOnTextChange() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(nameOfListEditText.getText().toString().isEmpty()){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        doneMenuItem = menu.add(Menu.NONE, 1000, Menu.NONE, R.string.done);
        doneMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        enableMenuItem();
        nameOfListEditText.addTextChangedListener(enableDoneMenuItemOnTextChange());
        doneMenuItem.setOnMenuItemClickListener(item -> returnResult());
        return true;
    }

    private void enableMenuItem() {
        if(nameOfListEditText == null || nameOfListEditText.getText().toString().isEmpty()){
            doneMenuItem.setEnabled(false);
        }else{
            doneMenuItem.setEnabled(true);
        }
    }

    private boolean returnResult() {
        Intent intent = new Intent();
        intent.putExtra("ListName", nameOfListEditText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }


}
