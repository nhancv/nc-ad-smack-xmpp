package com.nhancv.hellosmack.bus;

import com.nhancv.hellosmack.helper.Invitation;

/**
 * Created by nhancao on 12/21/16.
 */

public class InvitationBus extends BaseBus<Invitation> {

    public InvitationBus(Class clazz, int code, Invitation data) {
        super(clazz, code, data);
    }

}
