package de.db.shoppinglist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;
import java.util.UUID;

public class ShoppingEntry implements Parcelable {

    private float quantity;
    private String unitOfQuantity;
    private boolean done;
    private String name;
    private String details;
    private String uid;

    /**
     * Empty constructor required by Firestore.
     */
    public ShoppingEntry(){
    }

    public ShoppingEntry(float quantity, String unitOfQuantity, String name, String details) {
        this.quantity = quantity;
        this.unitOfQuantity = unitOfQuantity;
        this.name = name;
        this.details = details;
        done = false;
        uid = String.valueOf(UUID.randomUUID());
    }

    public ShoppingEntry(ShoppingEntry other) {
        this.quantity = other.quantity;
        this.unitOfQuantity = other.unitOfQuantity;
        this.name = other.name;
        this.details = other.details;
        done = false;
        uid = String.valueOf(UUID.randomUUID());
    }


    protected ShoppingEntry(Parcel in) {
        quantity = in.readFloat();
        unitOfQuantity = in.readString();
        done = in.readByte() != 0;
        name = in.readString();
        details = in.readString();
        uid = in.readString();
    }

    public static final Creator<ShoppingEntry> CREATOR = new Creator<ShoppingEntry>() {
        @Override
        public ShoppingEntry createFromParcel(Parcel in) {
            return new ShoppingEntry(in);
        }

        @Override
        public ShoppingEntry[] newArray(int size) {
            return new ShoppingEntry[size];
        }
    };

    public void setDone(boolean done){
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getUnitOfQuantity() {
        return unitOfQuantity;
    }

    public String getDetails(){
        return details;
    }

    public boolean isDone() {
        return done;
    }

    public String getUid(){
        return uid;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public void setUnitOfQuantity(String unitOfQuantity) {
        this.unitOfQuantity = unitOfQuantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(quantity);
        dest.writeString(unitOfQuantity);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeString(name);
        dest.writeString(details);
        dest.writeString(uid);
    }
}
