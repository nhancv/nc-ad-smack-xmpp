package com.nhancv.hellosmack.bus;

/**
 * Created by nhancao on 2/15/17.
 */

public class NetBus extends BaseBus {

    public NetBus(Class clazz, Type code) {
        super(clazz, code.ordinal());
    }

    public Type getType() {
        return Type.values()[getCode()];
    }

    public enum Type {
        CONNECTED,
        NO_CONNECTION
    }
}
