package me.matl114.matlib.nmsMirror.interfaces;

public interface PdcCompoundHolder {
    Object getPersistentDataCompound(Object val, boolean create);

    void setPersistentDataCompound(Object itemStack, Object compound);
}
