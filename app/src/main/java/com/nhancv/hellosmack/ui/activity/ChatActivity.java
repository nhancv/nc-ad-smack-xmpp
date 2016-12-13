package com.nhancv.hellosmack.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.XmppHandler;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.ui.adapter.ChatAdapter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nhancao on 9/7/16.
 */
public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getName();
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.vListsItems)
    RecyclerView vListsItems;
    @BindView(R.id.etInput)
    EditText etInput;
    ChatAdapter adapter;
    String address;
    Chat chat;
    StanzaListener chatSessionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        //Setup View
        vListsItems = (RecyclerView) findViewById(R.id.vListsItems);
        adapter = new ChatAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        vListsItems.setHasFixedSize(true);
        vListsItems.setLayoutManager(llm);
        vListsItems.setAdapter(adapter);

        address = getIntent().getStringExtra("address");

        chat = XmppHandler.getInstance().setupChat(address);
        chatSessionListener = packet -> {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                NUtil.runOnUi(() -> {
                    adapter.addMessage(message);
                    vListsItems.smoothScrollToPosition(adapter.getItemCount());
                });
            }
        };
        XmppHandler.getInstance().createChatSession(chatSessionListener, address);
        if (chat != null) {
            tvTitle.setText(XmppHandler.getInstance().parseUserJid(address));
        }
    }

    @OnClick(R.id.btSend)
    public void btSendOnClick() {
        try {
            Message message = new Message(chat.getParticipant());
            message.setBody(etInput.getText().toString());
            chat.sendMessage(message);
            adapter.addMessage(message);
            vListsItems.smoothScrollToPosition(adapter.getItemCount());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btClose)
    public void btCloseOnClick() {
        if (chat != null) {
            chat.close();
            chat = null;
        }
        if (chatSessionListener != null) {
            XmppHandler.getInstance().terminalChatSession(chatSessionListener);
        }
        finish();
    }

    @Override
    protected void onStop() {
        if (chat != null) {
            chat.close();
            chat = null;
        }
        if (chatSessionListener != null) {
            XmppHandler.getInstance().terminalChatSession(chatSessionListener);
        }
        super.onStop();
    }
}
