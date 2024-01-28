package lms.logistics;

import java.util.function.Consumer;

/**
 * Maintains a doubly linked list to maintain the links for each node.
 * Has previous and next item.
 * The path can't have an empty node, as it will throw an illegal
 * argument exception.
 * @version 1.0
 * @ass2
 */
public class Path {
    /** The path variable directly before this path variable. (Corresponding to the transport
     * node preceding this transport node). */
    private Path previous;
    /** The path variable directly after this path variable. (Corresponding to the transport
     * node following this transport node).*/
    private Path next;
    /** The node that this path variable is associated to. */
    private final Transport node;

    /**
     * Constructs a new Path object with the same Transport node, previous Path and next Path as
     * the specified Path object.
     * @param path the Path object to copy.
     * @throws IllegalArgumentException if the path argument is null.
     */
    public Path(Path path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException();
        }

        this.previous = path.previous;
        this.next = path.next;
        this.node = path.node;
    }

    /**
     * Constructs a new Path object with the specified Transport node, and sets the previous and
     * next Path objects in the path to null.
     * @param node the Transport node for this Path.
     * @throws IllegalArgumentException if the node argument is null.
     */
    public Path(Transport node) throws IllegalArgumentException {
        if (node == null) {
            throw new IllegalArgumentException();
        }

        this.node = node;
        this.next = null;
        this.previous = null;
    }

    /**
     * Constructs a new Path object with the specified Transport node, and the previous and next
     * Path objects in the path.
     * @param node the Transport node for this Path.
     * @param previous the previous Path object in this Path sequence.
     * @param next the next Path object in this Path sequence.
     * @throws IllegalArgumentException if the node argument is null;
     */
    public Path(Transport node, Path previous, Path next) throws IllegalArgumentException {
        if (node == null) {
            throw new IllegalArgumentException();
        }

        this.previous = previous;
        this.next = next;
        this.node = node;
    }

    /**
     * Returns the head of this Path, which is the first element in the path. If this Path is the
     * first element, it is returned as is.
     * @return the Path object at the head of the Path sequence.
     */
    public Path head() {
        if (this.previous == null) {
            return this;
        }

        Path path = new Path(this);  // makes a copy for iteration
        while (path.previous != null) {
            path = path.previous;
        }
        return path;
    }

    /**
     * Returns the tail of this Path, which is the last element in the path. If this Path is the
     * last element, it is returned as is.
     * @return the Path object at the tail of the Path sequence.
     */
    public Path tail() {
        if (this.next == null) {
            return this;
        }

        Path path = new Path(this);  // makes a copy for iteration
        while (path.next != null) {
            path = path.next;
        }
        return path;
    }


    /**
     * Accessor method for the transport node associated with this path.
     * @return the Transport node associated with this Path.
     */
    public Transport getNode() {
        return node;
    }

    /**
     * Accessor method to get the previous Path object in the chain.
     * @return the previous pPath object in the chain, or null if this is the head.
     */
    public Path getPrevious() {
        return previous;
    }

    /**
     * Accessor method to get the next Path object in the chain.
     * @return the previous pPath object in the chain, or null if this is the tail.
     */
    public Path getNext() {
        return next;
    }

    /**
     * Sets the previous path for this path.
     * @param path the previous path to be set for this path.
     */
    public void setPrevious(Path path) {
        previous = path;
    }

    /**
     * Sets the next path for this path.
     * @param path the next path to be set for this path.
     */
    public void setNext(Path path) {
        next = path;
    }

    /**
     * This method takes a Transport Consumer,
     * using the Consumer&lt;T&gt; functional interface from java.util.
     * It finds the tail of the path and calls
     * Consumer&lt;T&gt;'s accept() method with the tail node as an argument.
     * Then it traverses the Path until the head is reached,
     * calling accept() on all nodes.
     * This is how we call the tick method for all the different transport items.
     *
     * @param consumer Consumer&lt;Transport&gt;
     * @provided
     * @see java.util.function.Consumer
     */
    public void applyAll(Consumer<Transport> consumer) {
        Path path = tail(); // IMPORTANT: go backwards to aid tick
        do {
            consumer.accept(path.node);
            path = path.previous;
        } while (path != null);
    }

    /**
     *  Provides a list of Path nodes from a Producer, along the belt to a Receiver, in a
     *  visually readable string format.
     *  @return String representing the entirety of the best path links in the format:
     *          START -> <"NODE-ID"> -> <"NODE-ID"> -> <"NODE-ID"> -> END.
     */
    @Override
    public String toString() {
        Path path = new Path(this.head());

        StringBuilder returnString = new StringBuilder("START -> ");

        while (true) {
            if (path == null) {
                returnString.append("END");
                break;
            }
            returnString.append(String.format("%s -> ", path.getNode()));
            path = path.next;
        }

        return returnString.toString();
    }

    /**
     * Compares this Path object to the specified object for equality. Returns true if and only
     * if the specified object is also a Path object and has the same Transport node as this Path
     * object.
     * @param other the object to compare this Path against.
     * @return true if the specified object is equal to this Path object, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        return this.getClass() == other.getClass() && this.getNode() == ((Path) other).getNode();
    }
}
