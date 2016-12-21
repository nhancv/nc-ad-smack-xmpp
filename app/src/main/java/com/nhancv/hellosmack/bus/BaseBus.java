package com.nhancv.hellosmack.bus;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class BaseBus<D extends Object> {
    private Class clazz;
    private int code;
    private D data;

    public BaseBus(Class clazz, int code) {
        this.clazz = clazz;
        this.code = code;
    }

    public BaseBus(Class clazz, int code, D data) {
        this.clazz = clazz;
        this.code = code;
        this.data = data;
    }

    public Class getClazz() {
        return clazz;
    }

    public int getCode() {
        return code;
    }

    public D getData() {
        return data;
    }
}
