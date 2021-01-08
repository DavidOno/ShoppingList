package de.db.shoppinglist.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.db.shoppinglist.R;
import de.db.shoppinglist.utility.ToastUtility;

/**
 * This is the main activity in the Single-Activity Architecture. It contains the containerFragment,
 * which holds all the fragments and the toolbar.
 * Also it decides which fragment is the startDestination, according to Android Jetpack Navigation.
 * If the user is signed-In he will start at the {@link ShoppingListsFragment}, otherwise he starts
 * at the {@link LoginFragment}.
 */
public class ShoppingListsActivity extends AppCompatActivity {

    private ToastUtility toastUtility = ToastUtility.getInstance();
    private NavController navController;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(navHostFragment);
        selectStartFragment();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        NavigationUI.setupActionBarWithNavController(this, navController);
        displayReceivedToasts();
    }

    private void displayReceivedToasts() {
        toastUtility.getNewToast().observe(this, isNew -> {
            if (Boolean.TRUE.equals(isNew)) {
                String message = toastUtility.getMessage();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectStartFragment() {
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.nav_graph);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (navHostFragment.getChildFragmentManager().getBackStackEntryCount() == 0) {
            if (currentUser != null) {
                graph.setStartDestination(R.id.shoppingListsFragment);
            } else {
                graph.setStartDestination(R.id.loginFragment);
            }
            navController.setGraph(graph);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}