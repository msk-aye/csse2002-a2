package lms.logistics;

import lms.logistics.belts.Belt;
import lms.logistics.container.Producer;
import lms.logistics.container.Receiver;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PathTest {
    private Path path1;
    private Path path2;
    private Path path3;
    private Path otherNull;
    private Path path;
    private Transport transport;
    private Transport transportNull;

    public PathTest() {
    }

    @Before
    public void setUp() {
        transport = new Producer(1, new Item("key"));
        transportNull = null;
        path1 = new Path(transport);
        path2 = new Path(new Belt(2));
        path3 = new Path(new Receiver(3, new Item("key")));
        otherNull = null;

        path1.setNext(path2);
        path2.setPrevious(path1);
        path2.setNext(path3);
        path3.setPrevious(path2);
    }

    @Test
    public void initialiseWithPath() {
        path = new Path(path1);
        assertEquals(path, path1);
    }

    @Test
    public void initialiseWithTransport() {
        path = new Path(transport);
        assertEquals(path, path1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialiseWithPathFail() {
        path = new Path(otherNull);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialiseWithTransportFail() {
        path = new Path(transportNull);
    }

    @Test
    public void initialiseWithPaths() {
        path = new Path(transport, path1, path2);
        assertEquals(path, path1);
        assertTrue(path.getNext() == path2 && path.getPrevious() == path1);
    }

    @Test
    public void getHeadTail() {
        assertSame(path1.head(), path2.head());
        assertSame(path3.head(), path2.head());
        assertSame(path1.tail(), path2.tail());
        assertSame(path3.tail(), path2.tail());
    }

    @Test
    public  void getNext() {
        assertSame(path1.getNext(), path2);
        assertNull(path3.getNext());
    }
    @Test
    public  void getPrevious() {
        assertSame(path3.getPrevious(), path2);
        assertNull(path1.getPrevious());
    }

    @Test
    public void toStringTest() {
        assertEquals(path1.toString(), path2.toString());
        assertEquals( "START -> <Producer-1> -> <Belt-2> -> " +
                "<Receiver-3> -> END", path1.toString());
    }

    @Test
    public void equals() {
        Path path = new Path(path3);
        assertNotSame(path1, path2);
        assertEquals(path1, path1);
        assertEquals(path3, path);
    }
}
