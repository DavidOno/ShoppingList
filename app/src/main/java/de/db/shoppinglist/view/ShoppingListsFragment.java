package de.db.shoppinglist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.FireShoppingListsRecViewAdapter;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.ShoppingListsViewModel;

public class ShoppingListsFragment extends Fragment implements FireShoppingListsRecViewAdapter.OnListListener{

    private RecyclerView listOfListsView;
    private FloatingActionButton newListButton;
    private FireShoppingListsRecViewAdapter fireAdapter;
    private ShoppingListsViewModel shoppingListsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglists, container, false);
        findViewsById(view);
        newListButton.setOnClickListener(v -> openNewListDialog());
        listOfListsView.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingListsViewModel = new ViewModelProvider(requireActivity()).get(ShoppingListsViewModel.class);
        setUpRecyclerView();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int adapterPosition = viewHolder.getAdapterPosition();
                shoppingListsViewModel.deleteList(fireAdapter.getItem(adapterPosition));
            }
        }).attachToRecyclerView(listOfListsView);
        return view;
    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<ShoppingList> options = shoppingListsViewModel.getRecyclerViewOptions();
        fireAdapter = new FireShoppingListsRecViewAdapter(options, this);
        listOfListsView.setAdapter(fireAdapter);
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
        NewListDialog dialog = new NewListDialog();
        dialog.show(getParentFragmentManager(), null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Main_menu_edit:
                NavController navController = NavHostFragment.findNavController(this);
                NavDirections modifyListsDirection = ShoppingListsFragmentDirections.actionShoppingListsFragmentToSelectShoppingListModificationFragment();
                navController.navigate(modifyListsDirection);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListClick(int position) {
        ShoppingList list = fireAdapter.getItem(position);
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections openSelectedListDirection = ShoppingListsFragmentDirections.actionShoppingListsFragmentToShoppingListFragment(list);
        navController.navigate(openSelectedListDirection);
    }

    @Override
    public void onStart() {
        super.onStart();
        fireAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        fireAdapter.stopListening();
    }
}
