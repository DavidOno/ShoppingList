package de.db.shoppinglist.model;

public class ShoppingEntry {

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
}
