package com.nhancv.hellosmack.bus;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class BaseBus {
    private Class clazz;
    private int code;
    private Object data;

    public BaseBus(Class clazz, int code) {
        this.clazz = clazz;
        this.code = code;
    }

    public BaseBus(Class clazz, int code, Object data) {
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

    public Object getData() {
        return data;
    }
}
