package de.db.shoppinglist.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Model for storing all the reusable information of a created {@link ShoppingEntry}.
 * The quantity of an entry is not considered as reusable.
 */
public class EntryHistoryElement implements Parcelable {
    private String name;
    private String unitOfQuantity;
    private String details;
    private String imageURI;
    /**
     * The id which identifies this entry in the database.
     */
    private String uid;

    /**
     * Firebase requires an empty constructor.
     */
    public EntryHistoryElement() {

    }

    public EntryHistoryElement(String name, String unitOfQuantity, String details, String imageUri) {
        this.name = name;
        this.unitOfQuantity = unitOfQuantity;
        this.details = details;
        this.imageURI = imageUri;
        this.uid = UUID.randomUUID().toString();
    }

    public EntryHistoryElement(String name, String unitOfQuantity, String details, String imageUri, String uid) {
        this.name = name;
        this.unitOfQuantity = unitOfQuantity;
        this.details = details;
        this.imageURI = imageUri;
        this.uid = uid;
    }


    protected EntryHistoryElement(Parcel in) {
        name = in.readString();
        unitOfQuantity = in.readString();
        details = in.readString();
        imageURI = in.readString();
        uid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(unitOfQuantity);
        dest.writeString(details);
        dest.writeString(imageURI);
        dest.writeString(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EntryHistoryElement> CREATOR = new Creator<EntryHistoryElement>() {
        @Override
        public EntryHistoryElement createFromParcel(Parcel in) {
            return new EntryHistoryElement(in);
        }

        @Override
        public EntryHistoryElement[] newArray(int size) {
            return new EntryHistoryElement[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getUnitOfQuantity() {
        return unitOfQuantity;
    }

    public String getDetails() {
        return details;
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntryHistoryElement that = (EntryHistoryElement) o;
        return name.equals(that.name) &&
                Objects.equals(unitOfQuantity, that.unitOfQuantity) &&
                Objects.equals(details, that.details) &&
                Objects.equals(imageURI, that.imageURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, unitOfQuantity, details, imageURI);
    }

    @NonNull
    @Override
    public String toString() {
        return "EntryHistoryElement{" +
                "name='" + name + '\'' +
                ", unitOfQuantity='" + unitOfQuantity + '\'' +
                ", details='" + details + '\'' +
                ", imageUri='" + imageURI + '\'' +
                '}';
    }
}
