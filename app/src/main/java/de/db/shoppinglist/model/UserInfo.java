package de.db.shoppinglist.model;

import com.google.firebase.auth.FirebaseAuth;

/**
 * This model stores some meta-information about the users, using the app.
 */
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

    /**
     * Required by firebase.
     * @return Returns the displayname.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Required by firebase.
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }
}
