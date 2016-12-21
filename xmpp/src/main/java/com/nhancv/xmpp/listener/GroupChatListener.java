package com.nhancv.xmpp.listener;

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by nhancao on 12/21/16.
 */

public class GroupChatListener implements InvitationListener {
    private static final String TAG = GroupChatListener.class.getSimpleName();

    @Override
    public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
        Log.e(TAG, "invitationReceived: Entered invitation handler... " + message);
        try {
            room.join(inviter);
            Log.e(TAG, "invitationReceived: accepted");
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}