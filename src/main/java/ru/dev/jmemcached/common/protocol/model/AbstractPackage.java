package ru.dev.jmemcached.common.protocol.model;

abstract class AbstractPackage {

    private byte[] data;

    public AbstractPackage() {
    }

    public AbstractPackage(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public final boolean hasData() {
        return data != null && data.length > 0;
    }

}
