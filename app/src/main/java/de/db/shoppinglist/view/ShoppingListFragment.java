package de.db.shoppinglist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.FireShoppingListRecViewAdapter;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.ShoppingListViewModel;

public class ShoppingListFragment extends Fragment implements FireShoppingListRecViewAdapter.OnEntryListener{

    private RecyclerView entriesView;
    private FloatingActionButton newEntryButton;
    private ShoppingListViewModel shoppingListViewModel;
    private ShoppingList list;
    private FirestoreRecyclerAdapter fireAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglist, container, false);
        findViewsById(view);
        newEntryButton.setOnClickListener(v -> openNewEntryFragment());
        entriesView.setLayoutManager(new LinearLayoutManager(getContext()));
        setUpViewModel();
        setUpRecyclerView();
        setTitleOfFragment();
        handleItemInteraction();
        return view;
    }

    private void setUpViewModel() {
        shoppingListViewModel = new ViewModelProvider(requireActivity()).get(ShoppingListViewModel.class);
    }

    private void setTitleOfFragment() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(list.getName());
    }

    private void handleItemInteraction() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int adapterPosition = viewHolder.getAdapterPosition();
                boolean wasSuccess = shoppingListViewModel.deleteEntry(list, fireAdapter.getItem(adapterPosition));
                if(wasSuccess)
                Toast.makeText(getContext(), "Could be deleted", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(entriesView);
    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<ShoppingEntry> options = shoppingListViewModel.getRecylerViewOptions(list);
        fireAdapter = new FireShoppingListRecViewAdapter(options, this);
        entriesView.setAdapter(fireAdapter);
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

    private void openNewEntryFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections newEntry = ShoppingListFragmentDirections.actionShoppingListFragmentToNewEntryFragment(list);
        navController.navigate(newEntry);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ShoppingListFragmentArgs shoppingListFragmentArgs = ShoppingListFragmentArgs.fromBundle(getArguments());
        list = shoppingListFragmentArgs.getList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shoppinglist, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void findViewsById(View view) {
        entriesView = view.findViewById(R.id.listOfEntries);
        newEntryButton = view.findViewById(R.id.AddNewEntryButton);
    }

    @Override
    public void onEntryClick(int position) {
        ShoppingEntry entry = (ShoppingEntry) fireAdapter.getItem(position);
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections openSelectedEntry = ShoppingListFragmentDirections.actionShoppingListFragmentToModifyEntryFragment(entry, list);
        navController.navigate(openSelectedEntry);
    }
}
