package de.db.shoppinglist.database;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import de.db.shoppinglist.model.UserInfo;

/**
 * This interface allows to sign-in and -out of the application, based on Google.
 */
public class GoogleLogin implements Login {

    private static final String GOOGLE_LOGIN_TAG = "GoogleLogin";

    /**
     * Complete sign-out, using the {@link GoogleSignInClient}.
     * Afterwards the user, if he tries to resign-in, will be able to choose which account,
     * he wants to use.
     *
     * @param googleSignInClient Client, which allows to sign-out.
     */
    @Override
    public void signOut(GoogleSignInClient googleSignInClient) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(task -> Log.d(GOOGLE_LOGIN_TAG, "Completely logged out"));
    }

    /**
     * Allows to sign-in.
     *
     * @param idToken          Token for identification.
     * @param postSignInAction Action, running after the signIn.
     *                         Typically used for navigation to different fragment.
     */
    @Override
    public void signIn(String idToken, Runnable postSignInAction) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addToUsers();
                        postSignInAction.run();
                    } else {
                        Log.w(GOOGLE_LOGIN_TAG, "Failed to sign in via Google", task.getException());
                    }
                });
    }

    private void addToUsers() {
        UserInfo userInfo = new UserInfo(FirebaseAuth.getInstance());
        FirebaseFirestore.getInstance().collection(FirebaseSource.USERS_KEY).document(getUserId()).set(userInfo);
    }

    private String getUserId() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Log.d(GOOGLE_LOGIN_TAG, "FirebaseAuth returned null for userId");
        }
        return uid;
    }
}
