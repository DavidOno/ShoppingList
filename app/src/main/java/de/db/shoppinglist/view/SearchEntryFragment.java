package de.db.shoppinglist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.db.shoppinglist.R;

public class SearchEntryFragment extends Fragment {

    private SearchView searchView;
    private ImageButton addEntryButton;
    private RecyclerView historyOfEntries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppinglist, container, false);
        findViewsById(view);

        return view;
    }

    private void findViewsById(View view) {
        this.searchView = view.findViewById(R.id.search_entry_searchView);
        this.addEntryButton = view.findViewById(R.id.search_entry_add);
        this.historyOfEntries = view.findViewById(R.id.search_entry_historyView);
    }

}
