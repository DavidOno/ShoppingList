package de.db.shoppinglist.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import de.db.shoppinglist.R;

public class ShoppingListsActivity extends AppCompatActivity /*implements ShoppingListsRecViewAdapter.OnListListener*/{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}