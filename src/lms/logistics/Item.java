package lms.logistics;

import java.util.Objects;

/**
 * Class to manage the name of an Item object. Provides implementation for equals, hashcode and
 * toString.
 */
public class Item {
    /** The name (key) for this Item. */
    private final String name;

    /**
     * Constructs an Item with the given name.
     * @param name the name or key of this item.
     * @throws IllegalArgumentException if the item name is null or empty string.
     */
    public Item(String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    /**
     * Default and expected implementation specific to the needs of the comparison requirements.
     * Indicates whether some other object is "equal to" this one. Compares the class and
     * properties of the other object and decides if they are equal.
     * @param other the object to compare this Item to for equality.
     * @return true if the given object is equal to this Item, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        return Objects.equals(this.getClass(), other.getClass()) && Objects.equals(this.hashCode(),
                other.hashCode());
    }

    /**
     * Hashcode implementation, where hashcode is calculated based ont the item's name.
     * @return a hashcode calculated based on the Item's name.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * A string representation of the Item.
     * @return the Item name.
     */
    @Override
    public String toString() {
        return name;
    }
}
