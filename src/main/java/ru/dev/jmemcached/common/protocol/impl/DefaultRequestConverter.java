package ru.dev.jmemcached.common.protocol.impl;

import org.apache.commons.io.IOUtils;
import ru.dev.jmemcached.common.exception.JMemcachedException;
import ru.dev.jmemcached.common.protocol.RequestConverter;
import ru.dev.jmemcached.common.protocol.model.Command;
import ru.dev.jmemcached.common.protocol.model.Request;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DefaultRequestConverter extends AbstractPackageConverter implements RequestConverter {

    @Override
    public Request readRequest(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        checkProtocolVersion(dataInputStream.readByte());
        byte command = dataInputStream.readByte();
        byte flags = dataInputStream.readByte();
        boolean hasKey = (flags & 1) != 0;
        boolean hasTtl = (flags & 2) != 0;
        boolean hasData = (flags & 4) != 0;
        return readRequest(command, hasKey, hasTtl, hasData, dataInputStream);

    }

    protected Request readRequest(byte command, boolean hasKey, boolean hasTtl, boolean hasData, DataInputStream dataInputStream) throws IOException {
        Request request = new Request(Command.valueOf(command));
        if (hasKey) {
            byte keyLength = dataInputStream.readByte();
            byte[] keyBytes = IOUtils.readFully(dataInputStream, keyLength);
            request.setKey(new String(keyBytes, StandardCharsets.US_ASCII));
        }
        if (hasTtl) {
            request.setTtl(dataInputStream.readLong());
        }
        if (hasData) {
            int dataLength = dataInputStream.readInt();
            request.setData(IOUtils.readFully(dataInputStream, dataLength));
        }
        return request;
    }

    @Override
    public void writeRequest(OutputStream outputStream, Request request) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeByte(getVersionByte());
        dataOutputStream.writeByte(request.getCommand().getByteCode());
        dataOutputStream.writeByte(getFlagsByte(request));
        if (request.hasKey()) {
            writeKey(dataOutputStream, request);
        }
        if (request.hasTtl()) {
            dataOutputStream.writeLong(request.getTtl());
        }
        if (request.hasData()) {
            dataOutputStream.writeInt(request.getData().length);
            dataOutputStream.write(request.getData());
        }
        dataOutputStream.flush();
    }

    protected void writeKey(DataOutputStream dataOutputStream, Request request) throws IOException {
        byte[] key = request.getKey().getBytes(StandardCharsets.US_ASCII);
        if (key.length > 127) {
            throw new JMemcachedException("Key length should be <= 127 bytes for key=" + request.getKey());
        }
        dataOutputStream.writeByte(key.length);
        dataOutputStream.write(key);
    }

    protected byte getFlagsByte(Request request) {
        byte flags = 0;
        if (request.hasKey()) {
            flags |= 1;
        }
        if (request.hasTtl()) {
            flags |= 2;
        }
        if (request.hasData()) {
            flags |= 4;
        }
        return flags;
    }

}