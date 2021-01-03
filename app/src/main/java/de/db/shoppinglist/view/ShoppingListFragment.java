package de.db.shoppinglist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.Checkable;
import de.db.shoppinglist.adapter.FireShoppingListRecViewAdapter;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.ShoppingListViewModel;

public class ShoppingListFragment extends Fragment implements FireShoppingListRecViewAdapter.OnEntryListener{

    private RecyclerView entriesView;
    private FloatingActionButton newEntryButton;
    private ShoppingListViewModel shoppingListViewModel;
    private ShoppingList list;
    private FireShoppingListRecViewAdapter fireAdapter;
    private static final String EXPANDED_POSITION_KEY = "Exp_key";
    private static final String PREV_EXPANDED_POSITION_KEY = "Prev_exp_key";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglist, container, false);
        findViewsById(view);
        newEntryButton.setOnClickListener(v -> openNewEntryFragment());
        setUpViewModel();
        setUpRecyclerView();
        setTitleOfFragment();
        handleItemInteraction();
        handleItemIsDone();
        return view;
    }

    private void setUpViewModel() {
        shoppingListViewModel = new ViewModelProvider(requireActivity()).get(ShoppingListViewModel.class);
    }

    private void setTitleOfFragment() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(list.getName());
    }

    private void handleItemInteraction() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                ShoppingEntry firstEntry = (ShoppingEntry) fireAdapter.getItem(fromPosition);
                ShoppingEntry secondEntry = (ShoppingEntry) fireAdapter.getItem(toPosition);

                shoppingListViewModel.updatePosition(list, firstEntry, toPosition);
                shoppingListViewModel.updatePosition(list, secondEntry, fromPosition);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int adapterPosition = viewHolder.getAdapterPosition();
                shoppingListViewModel.deleteEntry(list, fireAdapter.getItem(adapterPosition));
            }
        }).attachToRecyclerView(entriesView);
    }

    public void handleItemIsDone(){
        if(fireAdapter instanceof Checkable){
            Checkable<ShoppingEntry> checkable = (Checkable)fireAdapter;
            checkable.getFlag().observe(getViewLifecycleOwner(),
                    aBoolean -> {
                        if(aBoolean.booleanValue()) {
                            ShoppingEntry entry = checkable.getItem();
                            shoppingListViewModel.toggleDoneStatus(list, entry);
                            ((Checkable) fireAdapter).resetFlags();
                        }
                    });
        }
    }

    private void setUpRecyclerView() {
        entriesView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        NavDirections newEntry = ShoppingListFragmentDirections.actionShoppingListFragmentToSearchEntryFragment(list);
        navController.navigate(newEntry);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        setRetainInstance(true);
        ShoppingListFragmentArgs shoppingListFragmentArgs = ShoppingListFragmentArgs.fromBundle(getArguments());
        list = shoppingListFragmentArgs.getList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shoppinglist, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_shoppingList_check_all:
                fireAdapter.getSnapshots().forEach(entry -> shoppingListViewModel.setStatusToDone(list, (ShoppingEntry) entry));
                break;
            case R.id.menu_shoppingList_delete_all_checked:
                fireAdapter.getSnapshots().stream()
                        .filter(ShoppingEntry.class::isInstance)
                        .filter(shoppingEntry -> ((ShoppingEntry) shoppingEntry).isDone())
                        .forEach(entry -> shoppingListViewModel.deleteEntry(list, (ShoppingEntry) entry));
                break;
            case R.id.menuItemDeleteAllEntries:
                fireAdapter.getSnapshots().stream()
                        .filter(ShoppingEntry.class::isInstance)
                        .forEach(entry -> shoppingListViewModel.deleteEntry(list, (ShoppingEntry) entry));
                break;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            int expandedPosition = savedInstanceState.getInt(EXPANDED_POSITION_KEY);
            fireAdapter.setExpandedPosition(expandedPosition);
            int previousPosition = savedInstanceState.getInt(PREV_EXPANDED_POSITION_KEY);
            fireAdapter.setExpandedPosition(previousPosition);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXPANDED_POSITION_KEY, fireAdapter.getExpandedPosition());
        outState.putInt(PREV_EXPANDED_POSITION_KEY, fireAdapter.getPreviousExpandedPosition());
    }
}

