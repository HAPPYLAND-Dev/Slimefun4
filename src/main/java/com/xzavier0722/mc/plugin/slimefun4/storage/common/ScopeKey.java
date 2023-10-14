package com.xzavier0722.mc.plugin.slimefun4.storage.common;

import javax.annotation.Nonnull;

public class ScopeKey {
    protected final DataScope scope;

    public ScopeKey(DataScope scope) {
        this.scope = scope;
    }

    @Nonnull
    public DataScope getScope() {
        return scope;
    }

    protected String getKeyStr() {
        return scope.name();
    }

    @Override
    public int hashCode() {
        return getKeyStr().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ScopeKey other && scope == other.scope;
    }

    @Override
    public String toString() {
        return getKeyStr();
    }
}
