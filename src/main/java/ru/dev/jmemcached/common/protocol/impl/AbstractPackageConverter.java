package ru.dev.jmemcached.common.protocol.impl;

import ru.dev.jmemcached.common.exception.JMemcachedException;
import ru.dev.jmemcached.common.protocol.model.Version;

abstract class AbstractPackageConverter {

    protected void checkProtocolVersion(byte versionByte) {
        Version version = Version.valueOf(versionByte);
        if (version != Version.VERSION_1_0) {
            throw new JMemcachedException("Unsupported protocol version: " + version);
        }
    }

    protected byte getVersionByte() {
        return Version.VERSION_1_0.getByteCode();
    }

}
