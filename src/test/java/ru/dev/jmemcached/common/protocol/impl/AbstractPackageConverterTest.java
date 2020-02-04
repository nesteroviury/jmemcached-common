package ru.dev.jmemcached.common.protocol.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.dev.jmemcached.common.exception.JMemcachedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AbstractPackageConverterTest {

    @Rule
    public ExpectedException expectedExceptionRule = ExpectedException.none();
    private AbstractPackageConverter abstractPackageConverter = new AbstractPackageConverter() {
    };

    @Test
    public void checkProtocolVersionSuccess() {
        try {
            abstractPackageConverter.checkProtocolVersion((byte) 16);
        } catch (Exception e) {
            fail("Supported protocol version should be 1.0");
        }
    }

    @Test
    public void checkProtocolVersionFailed() {
        expectedExceptionRule.expect(JMemcachedException.class);
        expectedExceptionRule.expectMessage(is("Unsupported protocol version: 0.0"));
        abstractPackageConverter.checkProtocolVersion((byte) 0);
    }

    @Test
    public void getVersionByte() {
        assertEquals(16, abstractPackageConverter.getVersionByte());
    }

}
