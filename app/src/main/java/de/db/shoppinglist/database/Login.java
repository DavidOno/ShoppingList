package de.db.shoppinglist.database;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

/**
 * This interface allows to sign-in and -out of the application.
 */
public interface Login {

    /**
     * Allows a complete sign-out, using the {@link GoogleSignInClient}
     * Afterwards the user, if he tries to resign-in, will be able to choose which account,
     * he wants to use.
     *
     * @param googleSignInClient Client, which allows to sign-out.
     */
    void signOut(GoogleSignInClient googleSignInClient);

    /**
     * Allows to sign-in.
     *
     * @param idToken          Token for identification.
     * @param postSignInAction Action, running after the signIn.
     *                         Typically used for navigation to different fragment.
     *                         Null is not allowed.
     */
    void signIn(String idToken, Runnable postSignInAction);
}
