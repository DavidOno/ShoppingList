package de.db.shoppinglist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShoppingList {

    private String name;
    private List<ShoppingEntry> entries = new ArrayList<>();

    public ShoppingList(String name) {
        this.name = name;
    }

    public void addEntry(ShoppingEntry entry){
        entries.add(new ShoppingEntry(entry));
    }

    public List<ShoppingEntry> getEntries(){
        return Collections.unmodifiableList(entries);
    }

    public String getName() {
        return name;
    }
}
