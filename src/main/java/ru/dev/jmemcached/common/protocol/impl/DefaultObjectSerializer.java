package ru.dev.jmemcached.common.protocol.impl;

import ru.dev.jmemcached.common.exception.JMemcachedException;
import ru.dev.jmemcached.common.protocol.ObjectSerializer;

import java.io.*;

public class DefaultObjectSerializer implements ObjectSerializer {

    @Override
    public byte[] toByteArray(Object object) {
        if (object == null) {
            return null;
        }
        if (!(object instanceof Serializable)) {
            throw new JMemcachedException("Class " + object.getClass().getName() + " should implement java.io.Serializable" +
                    " interface");
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new JMemcachedException("Can't convert object to byte array: " + e.getMessage(), e);
        }
    }

    @Override
    public Object fromByteArray(byte[] array) {
        if (array == null) {
            return null;
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(array));
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new JMemcachedException("Can't convert byte array to object: " + e.getMessage(), e);
        }
    }

}
