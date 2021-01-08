package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.repository.ShoppingRepository;

/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.LoginFragment}.
 */
public class LoginViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    /**
     * Allows to sign-in.
     *
     * @param idToken          Token for identification.
     * @param postSignInAction Action, running after the signIn.
     *                         Typically used for navigation to different fragment.
     *                         Null is not allowed.
     */
    public void signInWithCredential(String idToken, Runnable postSignInAction) {
        repo.signInWithCredential(idToken, postSignInAction);
    }
}
