package de.db.shoppinglist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
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
import de.db.shoppinglist.viewmodel.ShoppingListViewModel;

public class ShoppingListFragment extends Fragment implements FireShoppingListRecViewAdapter.OnEntryListener{

    private int positionToByModified;
    private RecyclerView entriesView;
    private FloatingActionButton newEntryButton;
//    private ShoppingListRecViewAdapter adapter;
    private ShoppingListViewModel shoppingListViewModel;
    private String listName;

    //TODO: Put in MVVM
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private FirestoreRecyclerAdapter fireAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglist, container, false);
        findViewsById(view);
        newEntryButton.setOnClickListener(v -> openNewEntryFragment());
        entriesView.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingListViewModel = new ViewModelProvider(requireActivity()).get(ShoppingListViewModel.class);
        setUpRecyclerView();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(listName);
        return view;
    }

    private void setUpRecyclerView() {
        Query query =  db.collection("Lists/"+listName+"/Entries");
        FirestoreRecyclerOptions<ShoppingEntry> options = new FirestoreRecyclerOptions.Builder<ShoppingEntry>()
                .setQuery(query, ShoppingEntry.class)
                .build();

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
        NavDirections newEntry = ShoppingListFragmentDirections.actionShoppingListFragmentToNewEntryFragment(listName);
        navController.navigate(newEntry);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ShoppingListFragmentArgs shoppingListFragmentArgs = ShoppingListFragmentArgs.fromBundle(getArguments());
        listName = shoppingListFragmentArgs.getListName();
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
        positionToByModified = position;
        ShoppingEntry entry = (ShoppingEntry) fireAdapter.getItem(position);
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections openSelectedEntry = ShoppingListFragmentDirections.actionShoppingListFragmentToModifyEntryFragment(entry, listName);
        navController.navigate(openSelectedEntry);
    }
}
