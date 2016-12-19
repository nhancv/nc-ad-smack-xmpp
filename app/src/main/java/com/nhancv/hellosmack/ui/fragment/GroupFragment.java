package com.nhancv.hellosmack.ui.fragment;

import android.support.v4.app.Fragment;
import android.widget.Button;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.xmpp.model.BaseRoster;
import com.nhancv.xmpp.XmppPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
@EFragment(R.layout.fragment_groups)
public class GroupFragment extends Fragment {

    @ViewById(R.id.btChatRoom)
    Button btChatRoom;
    MultiUserChat chatRoom;

    @AfterViews
    void initView() {

    }

    @Click(R.id.btChatRoom)
    public void btChatRoomOnClick() {
        NUtil.aSyncTask(subscriber -> {
            try {
                chatRoom = XmppPresenter.getInstance().createGroupChat("Room test", XmppPresenter.getInstance().getCurrentUser());
                for (BaseRoster user : XmppPresenter.getInstance().getCurrentRosterList()) {
                    chatRoom.invite(user.getName(), "hi you");
                }
            } catch (XMPPException.XMPPErrorException | SmackException e) {
                e.printStackTrace();
            }
        });
    }


}
