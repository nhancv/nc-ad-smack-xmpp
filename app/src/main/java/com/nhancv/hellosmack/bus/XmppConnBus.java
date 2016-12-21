package com.nhancv.hellosmack.bus;

/**
 * Created by nhancao on 12/21/16.
 */

public class XmppConnBus extends BaseBus {

    public XmppConnBus(Class clazz, Type code) {
        super(clazz, code.ordinal());
    }

    public XmppConnBus(Class clazz, Type code, Object data) {
        super(clazz, code.ordinal(), data);
    }

    public Type getType() {
        return Type.values()[getCode()];
    }

    public enum Type {
        CLOSED,
        CLOSE_ERROR,
        RECONN_SUCCESS,
        RECONNECTING,
        RECONN_FAILED
    }


}
