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
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavHost;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.FireSelectShoppingListModificationViewAdapter;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.viewmodel.SelectListModificationViewModel;

public class SelectShoppingListModificationFragment extends Fragment{

    private RecyclerView listOfListsView;
    private FireSelectShoppingListModificationViewAdapter fireAdapter;
    private SelectListModificationViewModel shoppingListsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_list_for_modify, container, false);
        findViewsById(view);
        listOfListsView.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingListsViewModel = new ViewModelProvider(requireActivity()).get(SelectListModificationViewModel.class);
        setUpRecyclerView();
        handleEditRequest();
        return view;
    }



    private void handleEditRequest() {
        fireAdapter.getEditClicked().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean.booleanValue()) {
                ShoppingList shoppingList = fireAdapter.getClickedElement();
                NavController navController = NavHostFragment.findNavController(this);
                NavDirections openListModifyDialogDirection = SelectShoppingListModificationFragmentDirections.actionSelectShoppingListModificationFragmentToModifyListDialog(shoppingList);
                navController.navigate(openListModifyDialogDirection);
                fireAdapter.resetFlags();
            }
        });
        fireAdapter.getDeleteClicked().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean.booleanValue()) {
                shoppingListsViewModel.deleteList(fireAdapter.getClickedElement());
                fireAdapter.resetFlags();
            }
        });
    }

    private void findViewsById(View view) {
        listOfListsView = view.findViewById(R.id.select_list_modify_listOfLists);
    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<ShoppingList> options = shoppingListsViewModel.getRecyclerViewOptions();
        fireAdapter = new FireSelectShoppingListModificationViewAdapter(options);
        listOfListsView.setAdapter(fireAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_done, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done_doneButton:
                closeFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void closeFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigateUp();
    }
}
