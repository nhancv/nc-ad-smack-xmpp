package com.nhancv.hellosmack.bus;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class LoginBus extends BaseBus {
    public static final int SUCCESS = 0;
    public static final int ERROR = 1;

    public LoginBus(Class clazz, int code) {
        super(clazz, code);
    }

    public LoginBus(Class clazz, int code, Object data) {
        super(clazz, code, data);
    }
}
