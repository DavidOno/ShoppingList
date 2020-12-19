package de.db.shoppinglist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ShoppingEntry implements Parcelable {

    private float quantity;
    private String unitOfQuantity;
    private boolean done;
    private ShoppingElement shoppingElement;

    public ShoppingEntry(float quantity, String unitOfQuantity, ShoppingElement shoppingElement) {
        this.unitOfQuantity = unitOfQuantity;
        this.shoppingElement = shoppingElement;
        done = false;
    }

    public ShoppingEntry(ShoppingEntry other) {
        this.quantity = other.quantity;
        this.unitOfQuantity = other.unitOfQuantity;
        this.shoppingElement = other.shoppingElement;
        done = false;
    }

    protected ShoppingEntry(Parcel in) {
        quantity = in.readFloat();
        unitOfQuantity = in.readString();
        done = in.readByte() != 0;
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
        return shoppingElement.getName();
    }

    public float getQuantity() {
        return quantity;
    }

    public String getUnitOfQuantity() {
        return unitOfQuantity;
    }

    public String getDetails(){
        return shoppingElement.getDetails();
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
    }

    public boolean isDone() {
        return done;
    }
}
