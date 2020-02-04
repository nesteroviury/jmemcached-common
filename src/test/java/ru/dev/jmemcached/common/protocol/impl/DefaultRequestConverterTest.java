package ru.dev.jmemcached.common.protocol.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import ru.dev.jmemcached.common.exception.JMemcachedException;
import ru.dev.jmemcached.common.protocol.model.Command;
import ru.dev.jmemcached.common.protocol.model.Request;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class DefaultRequestConverterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private final DefaultRequestConverter defaultRequestConverter = new DefaultRequestConverter();
    private final byte[] requestClear = new byte[]{16, 0, 0};
    private final byte[] requestPut = new byte[]{16, 1, 7, 3, 49, 50, 51, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 3, 1, 2, 3};

    @Test
    public void getFlagsByteEmpty() {
        Request request = new Request(Command.CLEAR);
        byte flags = defaultRequestConverter.getFlagsByte(request);
        assertEquals(0, flags);
    }

    @Test
    public void getFlagsByteAll() {
        Request request = new Request(Command.CLEAR, "key", System.currentTimeMillis(), new byte[]{1});
        byte flags = defaultRequestConverter.getFlagsByte(request);
        assertEquals(7, flags);
    }

    @Test
    public void writeKeySuccess() throws IOException {
        DataOutputStream dataOutputStream = spy(new DataOutputStream(mock(OutputStream.class)));
        String key = "key";
        defaultRequestConverter.writeKey(dataOutputStream, new Request(Command.GET, key));
        Mockito.verify(dataOutputStream).write(key.getBytes(StandardCharsets.US_ASCII));
        Mockito.verify(dataOutputStream).writeByte(3);
    }

    @Test
    public void writeKeyFailed() throws IOException {
        String key = StringUtils.repeat("a", 128);
        expectedException.expect(JMemcachedException.class);
        expectedException.expectMessage(is("Key length should be <= 127 bytes for key=" + key));
        DataOutputStream dataOutputStream = new DataOutputStream(null);
        defaultRequestConverter.writeKey(dataOutputStream, new Request(Command.GET, key));
    }

    @Test
    public void readSimpleRequest() throws IOException {
        Request request = defaultRequestConverter.readRequest(new ByteArrayInputStream(requestClear));
        assertEquals(Command.CLEAR, request.getCommand());
        assertFalse(request.hasKey());
        assertFalse(request.hasTtl());
        assertFalse(request.hasData());
    }

    @Test
    public void readComplexRequest() throws IOException {
        Request request = defaultRequestConverter.readRequest(new ByteArrayInputStream(requestPut));
        assertEquals(Command.PUT, request.getCommand());
        assertTrue(request.hasKey());
        assertEquals("123", request.getKey());
        assertTrue(request.hasTtl());
        assertEquals(Long.valueOf(5L), request.getTtl());
        assertTrue(request.hasData());
        assertArrayEquals(new byte[]{1, 2, 3,}, request.getData());
    }

    @Test
    public void writeRequestWithoutData() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        defaultRequestConverter.writeRequest(byteArrayOutputStream, new Request(Command.CLEAR));
        assertArrayEquals(requestClear, byteArrayOutputStream.toByteArray());
    }

    @Test
    public void writeREquestWithData() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        defaultRequestConverter.writeRequest(byteArrayOutputStream, new Request(Command.PUT, "123", 5L, new byte[]{1, 2, 3}));
        assertArrayEquals(requestPut, byteArrayOutputStream.toByteArray());
    }

}
