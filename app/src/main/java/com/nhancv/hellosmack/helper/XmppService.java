package com.nhancv.hellosmack.helper;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;

import com.nhancv.hellosmack.bus.BaseBus;
import com.nhancv.hellosmack.bus.InvitationBus;
import com.nhancv.hellosmack.bus.MessageBus;
import com.nhancv.hellosmack.bus.RosterBus;
import com.nhancv.hellosmack.bus.XmppConnBus;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.listener.ErrorXmppConListener;
import com.nhancv.xmpp.model.BaseInvitation;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by nhancao on 12/21/16.
 */

@EService
public class XmppService extends IntentService {
    private static final String TAG = XmppService.class.getSimpleName();
    private static EventBus bus = new EventBus();

    @SystemService
    NotificationManager notificationManager;

    public XmppService() {
        super(XmppService.class.getSimpleName());
    }

    public static EventBus getBus() {
        return bus;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        XmppPresenter.getInstance().enableCarbonMessage();
        XmppPresenter.getInstance().connectionListenerRegister(new ErrorXmppConListener() {
            @Override
            public void connectionClosed() {
                postEvent(new XmppConnBus(XmppService.class, XmppConnBus.Type.CLOSED));
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                postEvent(new XmppConnBus(XmppService.class, XmppConnBus.Type.CLOSE_ERROR, e));
            }

            @Override
            public void reconnectionSuccessful() {
                postEvent(new XmppConnBus(XmppService.class, XmppConnBus.Type.RECONN_SUCCESS));
            }

            @Override
            public void reconnectingIn(int seconds) {
                postEvent(new XmppConnBus(XmppService.class, XmppConnBus.Type.RECONNECTING));
            }

            @Override
            public void reconnectionFailed(Exception e) {
                postEvent(new XmppConnBus(XmppService.class, XmppConnBus.Type.RECONN_FAILED));
            }
        });
        XmppPresenter.getInstance().setAutoAcceptSubscribe();
        XmppPresenter.getInstance().addMessageStanzaListener(baseMessage -> {
            postEvent(new MessageBus(XmppService.class, 0, baseMessage));
        });

        XmppPresenter.getInstance().setupRosterList(baseRoster -> {
            postEvent(new RosterBus(XmppService.class, 0, baseRoster));
        });

        XmppPresenter.getInstance().getMultiUserChatManager()
                .addInvitationListener((conn, room, inviter, reason, password, message) -> {
                    postEvent(new InvitationBus(XmppService.class, 0, new BaseInvitation(conn, room, inviter, reason, password, message)));
                });
    }

    @UiThread
    <O extends BaseBus> void postEvent(O object) {
        getBus().post(object);
    }


}
