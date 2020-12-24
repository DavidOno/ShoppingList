package de.db.shoppinglist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class ShoppingList implements Parcelable, PositionContainer {

    private String name;
    private String uid;
    private int done;
    private int total;
    private int nextFreePosition = 1;

    public ShoppingList(){

    }

    public ShoppingList(String name) {
        this.name = name;
        this.done = 0;
        this.total = 0;
        uid = String.valueOf(UUID.randomUUID());
    }


    protected ShoppingList(Parcel in) {
        name = in.readString();
        uid = in.readString();
        done = in.readInt();
        total = in.readInt();
        nextFreePosition = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(uid);
        dest.writeInt(done);
        dest.writeInt(total);
        dest.writeInt(nextFreePosition);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public int getDone(){
        return done;
    }

    public int getTotal(){
        return total;
    }

    @Override
    public int getNextFreePosition() {
        int nextFree = nextFreePosition;
        nextFreePosition++;
        return nextFree;
    }


}
