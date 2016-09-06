package com.nhancv.hellosmack.bus;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class BaseBus {
    public Class clazz;
    public int code;
    public Object data;

    public BaseBus(Class clazz, int code) {
        this.clazz = clazz;
        this.code = code;
    }

    public BaseBus(Class clazz, int code, Object data) {
        this.clazz = clazz;
        this.code = code;
        this.data = data;
    }
}
