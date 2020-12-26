package de.db.shoppinglist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.SearchEntryRecyclerViewAdapter;
import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.SearchEntryViewModel;

public class SearchEntryFragment extends Fragment implements SearchEntryRecyclerViewAdapter.OnEntryListener {

    private SearchView searchView;
    private ImageButton addEntryButton;
    private RecyclerView historyOfEntries;
    private SearchEntryRecyclerViewAdapter adapter;
    private SearchEntryViewModel viewModel;
    private ShoppingList list;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_entry, container, false);
        findViewsById();
        addEntryButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            NavDirections newEntry = SearchEntryFragmentDirections.actionSearchEntryFragmentToNewEntryFragment(list, null, searchView.getQuery().toString());
            navController.navigate(newEntry);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        setUpViewModel();
        setUpRecyclerView();
        return view;
    }

    private void setUpViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(SearchEntryViewModel.class);
    }

    private void setUpRecyclerView() {
        List<EntryHistoryElement> history = viewModel.getHistory();
        adapter = new SearchEntryRecyclerViewAdapter(history,this);
        historyOfEntries.setAdapter(adapter);
        historyOfEntries.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SearchEntryFragmentArgs searchEntryFragmentArgs = SearchEntryFragmentArgs.fromBundle(getArguments());
        list = searchEntryFragmentArgs.getList();
    }

    private void findViewsById() {
        this.searchView = view.findViewById(R.id.search_entry_searchView);
        this.addEntryButton = view.findViewById(R.id.search_entry_add);
        this.historyOfEntries = view.findViewById(R.id.search_entry_historyView);
    }

    @Override
    public void onEntryClick(int position) {
        EntryHistoryElement entry = adapter.getHistoryEntry(position);
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections newEntry = SearchEntryFragmentDirections.actionSearchEntryFragmentToNewEntryFragment(list, entry, null);
        navController.navigate(newEntry);
    }

}
