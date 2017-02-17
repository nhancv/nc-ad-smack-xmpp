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
import com.nhancv.hellosmack.helper.SntpClient;
import com.nhancv.hellosmack.helper.XmppService;
import com.nhancv.hellosmack.model.NType;
import com.nhancv.hellosmack.model.Notify;
import com.nhancv.hellosmack.ui.adapter.ChatAdapter;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.XmppUtil;
import com.nhancv.xmpp.model.BaseBody;
import com.nhancv.xmpp.model.BaseMessage;
import com.nhancv.xmpp.model.BaseRoster;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jxmpp.util.XmppStringUtils;

import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by nhancao on 9/7/16.
 */
@EActivity(R.layout.activity_chat)
public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getName();
    @ViewById(R.id.vToolbar)
    Toolbar vToolbar;

    @ViewById(R.id.vListsItems)
    RecyclerView vListsItems;
    @ViewById(R.id.etInput)
    EditText etInput;
    @ViewById(R.id.tvTyping)
    TextView tvTyping;

    @Extra
    String addressJid;

    private Chat chat;
    private ChatAdapter adapter;
    private StanzaListener chatSessionListener;
    private ChatStateManager chatStateManager;
    private List<BaseMessage> listBaseMessage;
    private boolean scrollingFlag;
    
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
            scrollToBottom();
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
        setupToolbar(vToolbar, "Chat activity");

        vListsItems = (RecyclerView) findViewById(R.id.vListsItems);
        adapter = new ChatAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        vListsItems.setHasFixedSize(true);
        vListsItems.setLayoutManager(llm);
        vListsItems.setAdapter(adapter);

        chatStateManager = XmppPresenter.getInstance().getChatStateManager();
        listBaseMessage = XmppPresenter.getInstance().getMessageList(addressJid);
        adapter.setListsItems(listBaseMessage);
        chatSessionListener = packet -> {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                BaseRoster roster = XmppPresenter.getInstance().getRoster(message.getFrom());
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
        };

        chat = XmppPresenter.getInstance().openChatSession(chatSessionListener, addressJid);

        if (chat != null) {
            setupToolbar(vToolbar, XmppStringUtils.parseBareJid(addressJid));
        }
        etInput.addTextChangedListener(editTextAutoChange);

        vListsItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int top = llm.findFirstCompletelyVisibleItemPosition();
                    int bot = llm.findLastCompletelyVisibleItemPosition();
                    for (int i = top; i <= bot; i++) {
                        try {
                            BaseMessage msg = listBaseMessage.get(i);
                            if (!msg.isRead()) {
                                Message message = new Message(addressJid);
                                String content = String.format("-mid-%1$s-end-",
                                        msg.getMessage().getStanzaId());
                                message.setBody(new Notify(content).toString());
                                chat.sendMessage(message);
                                msg.setReadType(BaseMessage.ReadType.READ);
                            }

                        } catch (Exception ignored) {
                        }
                    }
                    updateAdapter();
                    scrollingFlag = false;
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
        if (etInput.getText().length() > 0) {
            if (chat != null) {
                String id = UUID.randomUUID().toString();
                String content = etInput.getText().toString();
                NType type = NType.text;
                getUTCTime(utcTimeStamp -> {
                    try {
                        //parse data
                        BaseBody body = new BaseBody(
                                XmppStringUtils.parseLocalpart(XmppPresenter.getInstance().getCurrentUser()),
                                XmppStringUtils.parseLocalpart(addressJid),
                                type.name(),
                                content,
                                utcTimeStamp,
                                id);
                        Message message = new Message(addressJid);
                        message.setStanzaId(id);
                        message.setBody(body.toString());

                        DeliveryReceiptRequest.addTo(message);
                        chat.sendMessage(message);

                        //add to list
                        BaseMessage baseMessage = new BaseMessage(message);
                        listBaseMessage.add(baseMessage);

                        //scroll to bottom
                        scrollingFlag = true;
                        vListsItems.smoothScrollToPosition(adapter.getItemCount());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

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

    public void scrollToBottom() {
        //scroll to bottom
        scrollingFlag = true;
        vListsItems.smoothScrollToPosition(adapter.getItemCount());
    }

    public void getUTCTime(UTCTime callback) {
        Observable.defer(() -> Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                long utcTimeStamp = 0;
                SntpClient sntpClient = new SntpClient();
                if (sntpClient.requestTime("pool.ntp.org")) {
                    utcTimeStamp = sntpClient.getNtpTime();
                }
                if (utcTimeStamp == 0) subscriber.onNext(System.currentTimeMillis());
                else subscriber.onNext(utcTimeStamp);

            }
        }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::getTime);
    }

    interface UTCTime {
        void getTime(Long utcTimeStamp);
    }

}
