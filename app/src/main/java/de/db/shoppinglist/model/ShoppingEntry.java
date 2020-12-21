package de.db.shoppinglist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class ShoppingEntry implements Parcelable {

    private float quantity;
    private String unitOfQuantity;
    private boolean done;
    private String name;
    private String details;

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
    }

    public ShoppingEntry(ShoppingEntry other) {
        this.quantity = other.quantity;
        this.unitOfQuantity = other.unitOfQuantity;
        this.name = other.name;
        this.details = other.details;
        done = false;
    }

    protected ShoppingEntry(Parcel in) {
        quantity = in.readFloat();
        unitOfQuantity = in.readString();
        done = in.readByte() != 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(quantity);
        dest.writeString(unitOfQuantity);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeString(name);
        dest.writeString(details);
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingEntry that = (ShoppingEntry) o;
        return Float.compare(that.quantity, quantity) == 0 &&
                done == that.done &&
                Objects.equals(unitOfQuantity, that.unitOfQuantity) &&
                Objects.equals(name, that.name) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, unitOfQuantity, done, name, details);
    }
}
