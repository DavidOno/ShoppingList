package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.repository.ShoppingRepository;

public class LoginViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public void signInWithCredential(String idToken, Runnable navigationToShoppingList) {
        repo.signInWithCredential(idToken, navigationToShoppingList);
    }
}
