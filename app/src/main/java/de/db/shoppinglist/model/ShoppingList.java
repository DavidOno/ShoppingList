package de.db.shoppinglist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * This model represents a shoppinglist.
 * It implements PositionContainer, so that the entries within this list have on order/position.
 */
public class ShoppingList implements Parcelable, PositionContainer {

    private String name;
    /** Id, which identifies this instance in the database. */
    private String uid;
    /** Counter, which tells how many entries in this list are considered finished.*/
    private int done;
    /** Counter, which tells the total number of entries.*/
    private int total;
    /** Holds the next free position, for the next entry, which could be added to the list.*/
    private int nextFreePosition;

    public ShoppingList() {

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

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public int getDone() {
        return done;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public int getNextFreePosition() {
        nextFreePosition++;
        return nextFreePosition;
    }
}