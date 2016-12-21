package com.nhancv.hellosmack.bus;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class RosterBus extends BaseBus {

    public RosterBus(Class clazz, int code) {
        super(clazz, code);
    }

    public RosterBus(Class clazz, int code, Object data) {
        super(clazz, code, data);
    }
}
