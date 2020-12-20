package de.db.shoppinglist.model;

public class ShoppingElement {

    private String name;
    private String details;

    /**
     * This empty constructor is required for Firebase
     */
    public ShoppingElement(){

    }

    public ShoppingElement(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }
    //image
}
