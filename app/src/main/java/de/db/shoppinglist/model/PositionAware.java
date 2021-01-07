package de.db.shoppinglist.model;

/**
 * This interface, in combination with {@link PositionContainer} allows to define an order on
 * elements in a list. This requires a container-contained-relationship.
 * E.g.: A list implements PositionContainer, whereas each item implements PositionAware.
 */
public interface PositionAware {

    int getPosition();
}
