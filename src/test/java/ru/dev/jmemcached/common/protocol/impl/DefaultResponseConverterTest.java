package ru.dev.jmemcached.common.protocol.impl;

import org.junit.Test;
import ru.dev.jmemcached.common.protocol.model.Response;
import ru.dev.jmemcached.common.protocol.model.Status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class DefaultResponseConverterTest {

    private final DefaultResponseConverter defaultResponseConverter = new DefaultResponseConverter();
    private final byte[] responseWithoutData = new byte[]{16, 0, 0};
    private final byte[] responseWithData = new byte[]{16, 0, 1, 0, 0, 0, 3, 1, 2, 3};

    @Test
    public void readResponseWithoutData() throws IOException {
        Response response = defaultResponseConverter.readResponse(new ByteArrayInputStream(responseWithoutData));
        assertEquals(Status.ADDED, response.getStatus());
        assertFalse(response.hasData());
    }

    @Test
    public void readResponseWithData() throws IOException {
        Response response = defaultResponseConverter.readResponse(new ByteArrayInputStream(responseWithData));
        assertEquals(Status.ADDED, response.getStatus());
        assertTrue(response.hasData());
        assertArrayEquals(new byte[]{1, 2, 3}, response.getData());
    }

    @Test
    public void writeResponseWithoutData() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Response response = new Response(Status.ADDED);
        defaultResponseConverter.writeResponse(byteArrayOutputStream, response);
        assertArrayEquals(responseWithoutData, byteArrayOutputStream.toByteArray());
    }

    @Test
    public void writeResponseWithData() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Response response = new Response(Status.ADDED, new byte[]{1, 2, 3,});
        defaultResponseConverter.writeResponse(byteArrayOutputStream, response);
        assertArrayEquals(responseWithData, byteArrayOutputStream.toByteArray());
    }

}
