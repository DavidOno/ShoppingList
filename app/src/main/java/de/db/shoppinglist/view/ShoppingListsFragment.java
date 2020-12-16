package de.db.shoppinglist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.ShoppingListsRecViewAdapter;

public class ShoppingListsFragment extends Fragment implements ShoppingListsRecViewAdapter.OnListListener{

    private static final int NEW_LIST_REQUEST = 1000;
    private RecyclerView listOfListsView;
    private FloatingActionButton newListButton;
    private ShoppingListsRecViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglists, container, false);
        findViewsById(view);
        newListButton.setOnClickListener(v -> openNewListDialog());
        adapter = new ShoppingListsRecViewAdapter(this);
        listOfListsView.setAdapter(adapter);
        listOfListsView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void findViewsById(View view) {
        listOfListsView = view.findViewById(R.id.listOfLists);
        newListButton = view.findViewById(R.id.AddNewListButton);
    }

    private void openNewListDialog() {
        NewListDialog dialog = new NewListDialog(adapter);
        dialog.show(getParentFragmentManager(), null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_manu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onListClick(int position) {
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections openSelectedList = ShoppingListsFragmentDirections.actionShoppingListsFragmentToShoppingListFragment();
        navController.navigate(openSelectedList);
    }
}
