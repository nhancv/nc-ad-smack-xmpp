package com.nhancv.hellosmack.ui.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.listener.XmppListener;
import com.nhancv.xmpp.model.BaseRoster;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.util.XmppStringUtils;

import java.util.UUID;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
@EFragment(R.layout.fragment_groups)
public class GroupFragment extends Fragment {
    private static final String TAG = GroupFragment.class.getSimpleName();

    @ViewById(R.id.btChatRoom)
    Button btChatRoom;
    MultiUserChat chatRoom;

    @AfterViews
    void initView() {

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            String serviceName = "conference." + XmppStringUtils.parseDomain(XmppPresenter.getInstance().getCurrentUser());
            MultiUserChatManager manager = XmppPresenter.getInstance().getMultiUserChatManager();
            for (HostedRoom hostedRoom : manager.getHostedRooms(serviceName)) {
                Log.e(TAG, "createGroupChat:hostedRoom " + hostedRoom.getJid() + " " + hostedRoom.getName());

            }
            for (String s : manager.getJoinedRooms()) {
                Log.e(TAG, "createGroupChat:joined " + s);
            }

            for (String s : manager.getServiceNames()) {
                Log.e(TAG, "createGroupChat:serviceName " + s);
            }
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btChatRoom)
    public void btChatRoomOnClick() {
        NUtil.aSyncTask(subscriber -> {
            try {
                chatRoom = XmppPresenter.getInstance().createGroupChat(
                        "Test group chat",
                        "Test group description",
                        UUID.randomUUID().toString(),
                        XmppPresenter.getInstance().getCurrentUser(),
                        new XmppListener.CreateGroupListener() {
                            @Override
                            public void joined(MultiUserChat chatRoom) {
                                showToast("User already joined the room");
                            }

                            @Override
                            public void created(MultiUserChat chatRoom) {
                                showToast("Successfully created a new room");
                            }

                            @Override
                            public void exists(MultiUserChat chatRoom) {
                                showToast("The same room is already exists");
                            }
                        });
                for (BaseRoster user : XmppPresenter.getInstance().getCurrentRosterList()) {
                    chatRoom.invite(user.getName(), "hi you");
                }
            } catch (XMPPException.XMPPErrorException | SmackException e) {
                e.printStackTrace();
            }
        });
    }

    public void showToast(String msg) {
        NUtil.runOnUi(() -> {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        });
    }

}
