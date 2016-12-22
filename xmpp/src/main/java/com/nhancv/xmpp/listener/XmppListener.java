package com.nhancv.xmpp.listener;

import com.nhancv.xmpp.model.ParticipantPresence;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by nhancao on 12/13/16.
 */

public class XmppListener {

    public interface IXmppConnListener {
        void success();

        void error(Exception ex);
    }

    public interface IXmppLoginListener {

        void loginSuccess();

        void loginError(Exception ex);
    }

    public interface IXmppCreateListener {
        void createSuccess() throws SmackException.NotConnectedException;

        void createError(Exception ex);
    }

    public interface IXmppCallback<T> {
        void callback(T item);
    }

    public interface CreateGroupListener {
        void joined(MultiUserChat chatRoom);

        void created(MultiUserChat chatRoom);

        void exists(MultiUserChat chatRoom);
    }

    public interface ParticipantListener {
        void processPresence(ParticipantPresence participantPresence);
    }

}
