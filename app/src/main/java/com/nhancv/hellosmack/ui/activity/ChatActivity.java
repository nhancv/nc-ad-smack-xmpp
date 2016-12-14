package com.nhancv.hellosmack.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.ui.adapter.ChatAdapter;
import com.nhancv.xmpp.XmppPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.util.XmppStringUtils;

/**
 * Created by nhancao on 9/7/16.
 */
@EActivity(R.layout.activity_chat)
public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getName();
    @ViewById(R.id.tvTitle)
    TextView tvTitle;
    @ViewById(R.id.vListsItems)
    RecyclerView vListsItems;
    @ViewById(R.id.etInput)
    EditText etInput;

    @Extra
    String address;

    Chat chat;
    ChatAdapter adapter;
    StanzaListener chatSessionListener;

    @AfterViews
    void initView() {
        //Setup View
        vListsItems = (RecyclerView) findViewById(R.id.vListsItems);
        adapter = new ChatAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        vListsItems.setHasFixedSize(true);
        vListsItems.setLayoutManager(llm);
        vListsItems.setAdapter(adapter);

        chat = XmppPresenter.getInstance().preparingChat(address);
        chatSessionListener = packet -> {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                NUtil.runOnUi(() -> {
                    message.setTo("<--- " + message.getTo());
                    adapter.addMessage(message);
                    vListsItems.smoothScrollToPosition(adapter.getItemCount());
                });
            }
        };
        XmppPresenter.getInstance().openChatSession(chatSessionListener, address);
        if (chat != null) {
            tvTitle.setText(XmppStringUtils.parseBareJid(address));
        }
    }

    @Click(R.id.btSend)
    void btSendOnClick() {
        try {
            Message message = new Message(chat.getParticipant());
            message.setBody(etInput.getText().toString());
            chat.sendMessage(message);

            message.setTo("---> " + message.getTo());
            adapter.addMessage(message);
            vListsItems.smoothScrollToPosition(adapter.getItemCount());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btClose)
    public void btCloseOnClick() {
        if (chat != null) {
            chat.close();
            chat = null;
        }
        if (chatSessionListener != null) {
            XmppPresenter.getInstance().closeChatSession(chatSessionListener);
        }
        finish();
    }

    @Override
    protected void onStop() {
        btCloseOnClick();
        super.onStop();
    }
}
