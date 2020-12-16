package de.db.shoppinglist.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.ShoppingListsRecViewAdapter;

public class ShoppingListsActivity extends AppCompatActivity implements ShoppingListsRecViewAdapter.OnListListener{

    private static final int NEW_LIST_REQUEST = 1000;
    private RecyclerView listOfListsView;
    private FloatingActionButton newListButton;
    private ShoppingListsRecViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsById();
        newListButton.setOnClickListener(v -> openNewListActivity());
        adapter = new ShoppingListsRecViewAdapter(this);
        listOfListsView.setAdapter(adapter);
        listOfListsView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void findViewsById() {
        listOfListsView = findViewById(R.id.listOfLists);
        newListButton = findViewById(R.id.AddNewListButton);
    }

    private void openNewListActivity() {
//        Intent newListIntent = new Intent(this, NewListActivity.class);
//        startActivityForResult(newListIntent, NEW_LIST_REQUEST);
        NewListDialog dialog = new NewListDialog(adapter);
        dialog.show(getSupportFragmentManager(), "test dialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_LIST_REQUEST && resultCode == RESULT_OK && data != null) {
            Toast.makeText(this, data.getStringExtra("ListName"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_manu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public void onListClick(int position) {
        //TODO:
    }
}