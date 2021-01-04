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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        handleItemInteraction();
        return view;
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
                shoppingListsViewModel.deleteList(fireAdapter.getItem(adapterPosition));
            }
        }).attachToRecyclerView(listOfListsView);
    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<ShoppingList> options = shoppingListsViewModel.getRecyclerViewOptions();
        NavController navController = NavHostFragment.findNavController(this);
        fireAdapter = new FireShoppingListsRecViewAdapter(options, this, navController);
        listOfListsView.setAdapter(fireAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
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
                navigateToModifyLists();
                break;
            case R.id.Main_menu_delete_history:
                shoppingListsViewModel.deleteHistory();
                break;
            case R.id.Main_menuItemDeleteAllLists:
                shoppingListsViewModel.deleteAllLists();
                break;
            case R.id.Main_menu_sign_out:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToModifyLists() {
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections modifyListsDirection = ShoppingListsFragmentDirections.actionShoppingListsFragmentToSelectShoppingListModificationFragment();
        navController.navigate(modifyListsDirection);
    }

    private void signOut() {
        GoogleSignInOptions gso = getGoogleSignOptions();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        shoppingListsViewModel.signOut(googleSignInClient);
        navigateToLoginPage();
    }

    private GoogleSignInOptions getGoogleSignOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
    }

    private void navigateToLoginPage() {
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections toLoginFragment = ShoppingListsFragmentDirections.actionShoppingListsFragmentToLoginFragment();
        navController.navigate(toLoginFragment);
    }

    @Override
    public void onListClick(int position) {
        ShoppingList list = fireAdapter.getItem(position);
        navigateToSelectedList(list);
    }

    private void navigateToSelectedList(ShoppingList list) {
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
