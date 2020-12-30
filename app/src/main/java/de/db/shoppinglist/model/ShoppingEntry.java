package de.db.shoppinglist.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class ShoppingEntry implements Parcelable, PositionAware {

    private float quantity;
    private String unitOfQuantity;
    private boolean done;
    private String name;
    private String details;
    private String uid;
    private int position = -1;
    private String imageURI;

    /**
     * Empty constructor required by Firestore.
     */
    public ShoppingEntry(){
    }

    public ShoppingEntry(float quantity, String unitOfQuantity, String name, String details, int position, String imageURI) {
        this.quantity = quantity;
        this.unitOfQuantity = unitOfQuantity;
        this.name = name;
        this.details = details;
        done = false;
        uid = String.valueOf(UUID.randomUUID());
        this.position = position;
        this.imageURI = imageURI;
    }

    public ShoppingEntry(ShoppingEntry other) {
        this.quantity = other.quantity;
        this.unitOfQuantity = other.unitOfQuantity;
        this.name = other.name;
        this.details = other.details;
        done = false;
        uid = String.valueOf(UUID.randomUUID());
        position = other.position;
        imageURI = other.imageURI;
    }


    protected ShoppingEntry(Parcel in) {
        quantity = in.readFloat();
        unitOfQuantity = in.readString();
        done = in.readByte() != 0;
        name = in.readString();
        details = in.readString();
        uid = in.readString();
        position = in.readInt();
        imageURI = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(quantity);
        dest.writeString(unitOfQuantity);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeString(name);
        dest.writeString(details);
        dest.writeString(uid);
        dest.writeInt(position);
        dest.writeString(imageURI);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public void setImageURI(String imageURI){
        this.imageURI = imageURI;
    }

    public String getImageURI(){
        return imageURI;
    }


    @Override
    public int getPosition() {
        return position;
    }



    public EntryHistoryElement extractHistoryElement(){
        return new EntryHistoryElement(name, unitOfQuantity, details);
    }


}
