package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;
/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.ShoppingListsFragment}.
 */
public class ShoppingListsViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    /**
     * Deletes a list from database.
     *
     * @param list List, which is supposed to be deleted.
     */
    public void deleteList(ShoppingList list) {
        repo.deleteList(list.getUid());
    }

    /**
     * Build the FirestoreRecyclerOptions, used in {@link com.firebase.ui.firestore.FirestoreRecyclerAdapter}.
     * Due to this options the FirestoreRecyclerAdapter knows which shopping-lists to display.
     *
     * @return Returns the options, containing a query, which data should be displayed.
     */
    public FirestoreRecyclerOptions<ShoppingList> getRecyclerViewOptions() {
        return repo.getShoppingListsRecyclerViewOptions();
    }

    /**
     * Deletes complete history.
     */
    public void deleteHistory(){
        repo.deleteHistory();
    }

    /**
     * Deletes all lists and the corresponding entries.
     */
    public void deleteAllLists() {
        repo.deleteAllLists();
    }

    /**
     * Allows a complete sign-out, using the {@link GoogleSignInClient}
     * Afterwards the user, if he tries to resign-in, will be able to choose which account,
     * he wants to use.
     *
     * @param googleSignInClient Client, which allows to sign-out.
     */
    public void signOut(GoogleSignInClient googleSignInClient) {
        repo.signOut(googleSignInClient);
    }
}
