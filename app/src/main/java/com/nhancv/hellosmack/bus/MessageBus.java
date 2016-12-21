package com.nhancv.hellosmack.bus;

/**
 * Created by nhancao on 12/21/16.
 */

public class MessageBus extends BaseBus {
    public MessageBus(Class clazz, int code) {
        super(clazz, code);
    }

    public MessageBus(Class clazz, int code, Object data) {
        super(clazz, code, data);
    }
}
