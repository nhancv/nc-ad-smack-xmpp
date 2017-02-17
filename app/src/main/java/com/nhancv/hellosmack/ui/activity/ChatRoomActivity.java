package com.nhancv.hellosmack.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.bus.MessageBus;
import com.nhancv.hellosmack.bus.RosterBus;
import com.nhancv.hellosmack.bus.XmppConnBus;
import com.nhancv.hellosmack.helper.NTextChange;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.helper.RxHelper;
import com.nhancv.hellosmack.helper.XmppService;
import com.nhancv.hellosmack.ui.adapter.ChatRoomAdapter;
import com.nhancv.xmpp.ChatRoomStateManager;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.XmppUtil;
import com.nhancv.xmpp.model.BaseMessage;
import com.nhancv.xmpp.model.BaseRoom;
import com.nhancv.xmpp.model.BaseRoster;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jxmpp.util.XmppStringUtils;

import java.util.List;

/**
 * Created by nhancao on 9/7/16.
 */
@EActivity(R.layout.activity_chat)
public class ChatRoomActivity extends AppCompatActivity {

    private static final String TAG = ChatRoomActivity.class.getName();
    @ViewById(R.id.vToolbar)
    Toolbar vToolbar;

    @ViewById(R.id.vListsItems)
    RecyclerView vListsItems;
    @ViewById(R.id.etInput)
    EditText etInput;
    @ViewById(R.id.tvTyping)
    TextView tvTyping;

    @Extra
    String roomId;
    BaseRoom chatRoom;

    private ChatRoomAdapter adapter;
    private ChatRoomStateManager chatRoomStateManager;
    private List<BaseMessage> listBaseMessage;
    private NTextChange editTextAutoChange = new NTextChange(new NTextChange.TextListener() {
        @Override
        public void after(Editable editable) {
            try {
                chatRoomStateManager.setCurrentState(ChatState.inactive, chatRoom.getMultiUserChat());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void before() {
            try {
                chatRoomStateManager.setCurrentState(ChatState.composing, chatRoom.getMultiUserChat());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Subscribe
    public void xmppConnSubscribe(XmppConnBus xmppConnBus) {
        Log.e(TAG, "xmppConnSubscribe: " + xmppConnBus.getType());
        switch (xmppConnBus.getType()) {
            case CLOSE_ERROR:
                NUtil.showToast(this, ((Exception) xmppConnBus.getData()).getMessage());
                finish();
                break;
            default:
                NUtil.showToast(this, xmppConnBus.getType().name());
                break;

        }
    }

    @Subscribe
    public void messageSubscribe(MessageBus messageBus) {
        BaseMessage baseMessage = (BaseMessage) messageBus.getData();
        if (baseMessage != null) {
            updateAdapter();
        }
    }

    @Subscribe
    public void rosterSubscribe(RosterBus rosterBus) {
        BaseRoster baseRoster = ((BaseRoster) rosterBus.getData());
        String status = (baseRoster != null ? baseRoster.getJid() + " -> " + baseRoster.getPresence().getType() : null);
        if (status != null) {
            Log.e(TAG, "rosterSubscribe: " + status);
            NUtil.showToast(this, "roster's status changed: " + status);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        XmppService.getBus().register(this);
        if (XmppPresenter.getInstance().isConnected()) {
            updateAdapter();
        }
    }

    @Override
    protected void onPause() {
        XmppService.getBus().unregister(this);
        super.onPause();
    }

    @AfterViews
    void initView() {
        setupToolbar(vToolbar, XmppStringUtils.parseBareJid(roomId));

        vListsItems = (RecyclerView) findViewById(R.id.vListsItems);
        adapter = new ChatRoomAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        vListsItems.setHasFixedSize(true);
        vListsItems.setLayoutManager(llm);
        vListsItems.setAdapter(adapter);

        chatRoomStateManager = XmppPresenter.getInstance().getChatRoomStateManager();
        chatRoom = XmppPresenter.getInstance().getRoom(roomId);

        listBaseMessage = XmppPresenter.getInstance().getMessageList(roomId);
        adapter.setListsItems(listBaseMessage);

        chatRoom.getMultiUserChat().addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                BaseRoster roster = XmppPresenter.getInstance().getRoster(XmppStringUtils.parseResource(message.getFrom()));
                if (roster != null && roster.getPresence().isAvailable()) {
                    String xml = message.toXML().toString();
                    if (XmppUtil.isMessage(xml)) {
                        RxHelper.runOnUi(() -> {
                            updateAdapter();
                            vListsItems.smoothScrollToPosition(adapter.getItemCount());
                        });
                    } else {
                        ChatState chatState = XmppUtil.getChatState(xml);
                        RxHelper.runOnUi(() -> {
                            if (chatState != null && chatState == ChatState.composing) {
                                updateTyping(roster.getJid() + " is typing ...");
                            } else {
                                updateTyping(null);
                            }
                        });
                    }
                }

            }
        });

        etInput.addTextChangedListener(editTextAutoChange);
        vListsItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int top = llm.findFirstCompletelyVisibleItemPosition();
                    int bot = llm.findLastCompletelyVisibleItemPosition();
                    for (int i = top; i <= bot; i++) {
                        try {
                            listBaseMessage.get(i).setReadType(BaseMessage.ReadType.READ);
                        } catch (Exception ignored) {
                        }
                    }
                    updateAdapter();
                }
            }
        });
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    private void setupToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    @Click(R.id.btSend)
    void btSendOnClick() {
        try {
            if (etInput.getText().length() > 0) {
                Message message = new Message(roomId, Message.Type.groupchat);
                message.setBody(etInput.getText().toString());
                chatRoom.getMultiUserChat().sendMessage(message);
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void btCloseOnClick() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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
