package com.nhancv.hellosmack.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NTextChange;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.ui.adapter.ChatAdapter;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.XmppUtil;
import com.nhancv.xmpp.model.BaseMessage;
import com.nhancv.xmpp.model.BaseRoster;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jxmpp.util.XmppStringUtils;

import java.util.List;

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
    @ViewById(R.id.tvTyping)
    TextView tvTyping;

    @Extra
    String address;
    private List<BaseMessage> listBasemessage;
    private Chat chat;
    private ChatAdapter adapter;
    private StanzaListener chatSessionListener;
    private ChatStateManager chatStateManager;
    private NTextChange editTextAutoChange = new NTextChange(new NTextChange.TextListener() {
        @Override
        public void after(Editable editable) {
            try {
                chatStateManager.setCurrentState(ChatState.inactive, chat);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void before() {
            try {
                chatStateManager.setCurrentState(ChatState.composing, chat);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    });

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

        chatStateManager = XmppPresenter.getInstance().getChatStateManager();
        listBasemessage = XmppPresenter.getInstance().getMessageList(address);
        adapter.setListsItems(listBasemessage);
        chatSessionListener = packet -> {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                BaseRoster roster = XmppPresenter.getInstance().getRoster(message.getFrom());
                if (roster.getPresence().isAvailable()) {
                    String xml = message.toXML().toString();
                    if (XmppUtil.isMessage(xml)) {
                        NUtil.runOnUi(() -> {
                            adapter.notifyDataSetChanged();
                            vListsItems.smoothScrollToPosition(adapter.getItemCount());
                        });
                    } else {
                        ChatState chatState = XmppUtil.getChatState(xml);
                        NUtil.runOnUi(() -> {
                            if (chatState != null && chatState == ChatState.composing) {
                                updateTyping(message.getFrom() + " is typing ...");
                            } else {
                                updateTyping(null);
                            }
                        });
                    }
                }
            }
        };

        chat = XmppPresenter.getInstance().openChatSession(chatSessionListener, address);

        if (chat != null) {
            tvTitle.setText(XmppStringUtils.parseBareJid(address));
        }
        etInput.addTextChangedListener(editTextAutoChange);

        vListsItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int top = llm.findFirstCompletelyVisibleItemPosition();
                    int bot = llm.findLastCompletelyVisibleItemPosition();
                    for (int i = top; i <= bot; i++) {
                        listBasemessage.get(i).setRead(true);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Click(R.id.btSend)
    void btSendOnClick() {
        try {
            if (etInput.getText().length() > 0) {
                Message message = new Message(address);
                message.setBody(etInput.getText().toString());
                chat.sendMessage(message);

                listBasemessage.add(new BaseMessage(message, true));
                adapter.notifyDataSetChanged();
                vListsItems.smoothScrollToPosition(adapter.getItemCount());
            }
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

    public void updateTyping(String msg) {
        if (msg == null) {
            tvTyping.setVisibility(View.GONE);
        } else {
            tvTyping.setVisibility(View.VISIBLE);
            tvTyping.setText(msg);
        }
    }
}
