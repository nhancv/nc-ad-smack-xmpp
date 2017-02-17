package com.nhancv.hellosmack.bus;


import com.nhancv.xmpp.model.BaseInvitation;

/**
 * Created by nhancao on 12/21/16.
 */

public class InvitationBus extends BaseBus<BaseInvitation> {

    public InvitationBus(Class clazz, int code, BaseInvitation data) {
        super(clazz, code, data);
    }

}
