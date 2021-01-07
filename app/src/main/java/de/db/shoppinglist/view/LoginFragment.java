package de.db.shoppinglist.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import de.db.shoppinglist.R;
import de.db.shoppinglist.viewmodel.LoginViewModel;

/**
 * This fragment displays basic login information.
 * Currently only Google-SignIn is supported.
 */
public class LoginFragment extends Fragment {

    private static final int SIGN_IN_RESULT_KEY = 120;
    private static final String LOGIN_TAG = "Login";
    private GoogleSignInClient googleSignInClient;
    private SignInButton signInButton;
    private LoginViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        signInButton = view.findViewById(R.id.sign_in_button);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GoogleSignInOptions gso = getGoogleSignInOptions();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        signInButton.setOnClickListener(v -> signIn());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    private GoogleSignInOptions getGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_RESULT_KEY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_RESULT_KEY) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()){
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.w(LOGIN_TAG, "Google sign in failed", e);
                }
            }else{
                Log.w(LOGIN_TAG, "Google sign in failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Runnable navigationToShoppingList = this::navigateToShoppingLists;
        viewModel.signInWithCredential(idToken, navigationToShoppingList);
    }

    private void navigateToShoppingLists() {
        NavController navController = NavHostFragment.findNavController(LoginFragment.this);
        NavDirections toShoppingListsFragment = LoginFragmentDirections.actionLoginFragmentToShoppingListsFragment();
        navController.navigate(toShoppingListsFragment);
    }
}
