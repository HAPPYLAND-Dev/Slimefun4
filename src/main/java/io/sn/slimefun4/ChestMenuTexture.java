package io.sn.slimefun4;

public record ChestMenuTexture(String namespace, String id) {
    public ChestMenuTexture(String fullName) {
        this(fullName.split(":")[0], fullName.split(":")[1]);
    }

    public String getFullName() {
        return namespace + ":" + id;
    }

}
