package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.common.DataScope;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.ScopeKey;
import java.util.UUID;

class UUIDKey extends ScopeKey {
    private final UUID uuid;

    public UUIDKey(DataScope scope, String uuid) {
        this(scope, UUID.fromString(uuid));
    }

    public UUIDKey(DataScope scope, UUID uuid) {
        super(scope);
        this.uuid = uuid;
    }

    @Override
    protected String getKeyStr() {
        return scope + "/" + uuid;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof UUIDKey other && scope == other.scope && uuid.equals(other.uuid));
    }
}
