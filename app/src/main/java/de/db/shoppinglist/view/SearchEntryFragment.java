package de.db.shoppinglist.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
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
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.SearchEntryViewModel;

public class SearchEntryFragment extends Fragment implements SearchEntryRecyclerViewAdapter.OnEntryListener {

    private static final String SEARCH_QUERY_KEY = "Search_query_key";
    private SearchView searchView;
    private ImageButton addEntryButton;
    private RecyclerView historyOfEntries;
    private SearchEntryRecyclerViewAdapter adapter;
    private SearchEntryViewModel viewModel;
    private ShoppingList list;
    private View view;
    private String query;

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
                query = newText;
                adapter.getFilter().filter(query);
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
        LiveData<List<EntryHistoryElement>> history = viewModel.getHistory();
        adapter = new SearchEntryRecyclerViewAdapter(history.getValue(),this);
        history.observe(getViewLifecycleOwner(),entryHistoryElements -> {
            adapter.setHistory(history.getValue());
            adapter.notifyDataSetChanged();
            adapter.getFilter().filter(query);
            Log.d("SEARCH_ENTRY", "Observed change");
        });
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(query != null)
            outState.putString(SEARCH_QUERY_KEY, query);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            String query = savedInstanceState.getString(SEARCH_QUERY_KEY);
            adapter.getFilter().filter(query);
        }
    }
}
