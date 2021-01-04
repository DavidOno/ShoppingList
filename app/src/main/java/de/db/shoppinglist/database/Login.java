package de.db.shoppinglist.database;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public interface Login {

    void signOut(GoogleSignInClient googleSignInClient);

    void signIn(String idToken, Runnable navigationToShoppingList);
}
