package de.db.shoppinglist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class ShoppingList implements Parcelable, PositionContainer {

    private String name;
    private String uid;
    private int nextFreePosition = 1;

    public ShoppingList(){

    }

    public ShoppingList(String name) {
        this.name = name;
        uid = String.valueOf(UUID.randomUUID());
    }


    protected ShoppingList(Parcel in) {
        name = in.readString();
        uid = in.readString();
        nextFreePosition = in.readInt();
    }

    public static final Creator<ShoppingList> CREATOR = new Creator<ShoppingList>() {
        @Override
        public ShoppingList createFromParcel(Parcel in) {
            return new ShoppingList(in);
        }

        @Override
        public ShoppingList[] newArray(int size) {
            return new ShoppingList[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public int getNextFreePosition() {
        int nextFree = nextFreePosition;
        nextFreePosition++;
        return nextFree;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(uid);
        dest.writeInt(nextFreePosition);
    }
}
