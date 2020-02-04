package ru.dev.jmemcached.common.protocol.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.dev.jmemcached.common.exception.JMemcachedException;
import test.SerializableFailedClass;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

public class DefaultObjectSerializerTest {

    private final DefaultObjectSerializer defaultObjectSerializer = new DefaultObjectSerializer();
    private final Object testObject = "Test";
    private final byte[] testObjectArray = {-84, -19, 0, 5, 116, 0, 4, 84, 101, 115, 116};
    private final byte[] testClassNotFoundArray = {-84, -19, 0, 5, 115, 114, 0, 3, 97, 46, 66,
            56, 54, 57, -101, -3, 120, 66, 4, 2, 0, 0, 120, 112};
    private final byte[] testIOExceptionDuringDeserialization = {-84, -19, 0, 5, 115, 114, 0, 28, 116, 101, 115, 116, 46, 83,
            101, 114, 105, 97, 108, 105, 122, 97, 98, 108, 101, 70, 97, 105, 108, 101, 100, 67, 108, 97, 115, 115, -88, 79,
            -1, -107, 1, -38, 92, -71, 2, 0, 0, 120, 112};

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void toByteArraySuccess() {
        byte[] actual = defaultObjectSerializer.toByteArray(testObject);
        assertArrayEquals(testObjectArray, actual);
    }

    @Test
    public void toByteArrayNull() {
        assertNull(defaultObjectSerializer.toByteArray(null));
    }

    @Test
    public void toByteArraySerializableException() {
        expectedException.expect(JMemcachedException.class);
        expectedException.expectMessage(is("Class java.lang.Object should implement java.io.Serializable interface"));
        defaultObjectSerializer.toByteArray(new Object());
    }

    @Test
    public void toByteArrayIOException() {
        expectedException.expect(JMemcachedException.class);
        expectedException.expectMessage(is("Can't convert object to byte array: Write IO"));
        expectedException.expectCause(isA(IOException.class));
        defaultObjectSerializer.toByteArray(new SerializableFailedClass());
    }

    @Test
    public void fromByteArrayNull() {
        assertNull(defaultObjectSerializer.fromByteArray(null));
    }

    @Test
    public void fromByteArrayIOException() throws IOException {
/*
        SerializableFailedClass serializableFailedClass = new SerializableFailedClass();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(serializableFailedClass);
        objectOutputStream.flush();
        byte[] bytes = byteArrayOutputStream.toByteArray();
*/
        expectedException.expect(JMemcachedException.class);
        expectedException.expectMessage(is("Can't convert byte array to object: Read IO"));
        expectedException.expectCause(isA(IOException.class));
        defaultObjectSerializer.fromByteArray(testIOExceptionDuringDeserialization);
    }

    @Test
    public void fromByteArrayClassNotFoundException() {
        expectedException.expect(JMemcachedException.class);
        expectedException.expectMessage(is("Can't convert byte array to object: a.B"));
        expectedException.expectCause(isA(ClassNotFoundException.class));
        defaultObjectSerializer.fromByteArray(testClassNotFoundArray);
    }

}
