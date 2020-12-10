package de.db.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class ShoppingListsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_manu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }
}