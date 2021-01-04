package de.db.shoppinglist.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInfo {

    private String displayName;
    private String email;

    /**
     * Firebase requires an empty constructor.
     */
    public UserInfo() {
        //empty constructor
    }

    public UserInfo(FirebaseAuth instance) {
        displayName = instance.getCurrentUser().getDisplayName();
        email = instance.getCurrentUser().getEmail();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }
}
