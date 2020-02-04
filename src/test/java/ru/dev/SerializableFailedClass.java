package ru.dev;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableFailedClass implements Serializable {

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        throw new IOException("Read IO");
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        throw new IOException("Write IO");
    }

}
