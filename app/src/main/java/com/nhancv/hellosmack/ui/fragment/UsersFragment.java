package com.nhancv.hellosmack.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.XmppHandler;
import com.nhancv.hellosmack.helper.Utils;
import com.nhancv.hellosmack.listener.XMPPStanzaListener;
import com.nhancv.hellosmack.model.User;
import com.nhancv.hellosmack.ui.adapter.UsersAdapter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class UsersFragment extends Fragment {
    private static final String TAG = UsersFragment.class.getName();
    @BindView(R.id.vListsItems)
    RecyclerView vListsItems;
    UsersAdapter adapter;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        unbinder = ButterKnife.bind(this, view);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        vListsItems.setHasFixedSize(true);
        vListsItems.setLayoutManager(llm);
        adapter = new UsersAdapter();
        vListsItems.setAdapter(adapter);

        List<XMPPStanzaListener> stanzaListener = new ArrayList<>();
        stanzaListener.add(new XMPPStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                if (packet instanceof Presence) {
                    Log.e(TAG, "Presence: " + packet);
                    Presence presence = (Presence) packet;
                    if (presence.getType() != null) {
                        switch (presence.getType()) {
                            case subscribe:
                                XmppHandler.getInstance().requestUser(presence.getFrom(), Presence.Type.subscribed);
                                XmppHandler.getInstance().requestUser(presence.getFrom(), Presence.Type.subscribe);
                                break;
                            case unsubscribe:
                                XmppHandler.getInstance().requestUser(presence.getFrom(), Presence.Type.unsubscribed);
                                break;

                        }
                    }
                }
            }
        }, Stanza.class));
        stanzaListener.add(new XMPPStanzaListener(packet -> {
            if (packet instanceof Message) {
                Log.e(TAG, "Message: " + packet);
                Message message = (Message) packet;
                for (User user : XmppHandler.getInstance().getUserList()) {
                    if (message.getFrom().contains(user.getName())) {
                        user.setLastMessage(message.getBody());
                        break;
                    }
                }
                Utils.runOnUi(() -> {
                    adapter.setListsItems(XmppHandler.getInstance().getUserList());
                });
            }
        }, Message.class));
        XmppHandler.getInstance().setupListener(stanzaListener);
        XmppHandler.getInstance().getUserList(roster -> {
            adapter.setListsItems(XmppHandler.getInstance().getUserList());
            roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
            roster.addRosterListener(new RosterListener() {
                @Override
                public void entriesAdded(Collection<String> addresses) {
                    for (String item : addresses) {
                        Presence presence = roster.getPresence(item);
                        XmppHandler.getInstance().getUserList().add(new User(item, presence));
                    }
                    Utils.runOnUi(() -> {
                        adapter.setListsItems(XmppHandler.getInstance().getUserList());
                    });
                }

                @Override
                public void entriesUpdated(Collection<String> addresses) {
                    for (String item : addresses) {
                        for (User user : XmppHandler.getInstance().getUserList()) {
                            if (item.contains(user.getName())) {
                                Presence presence = roster.getPresence(item);
                                user.setPresence(presence);
                                break;
                            }
                        }
                    }
                    Utils.runOnUi(() -> {
                        adapter.setListsItems(XmppHandler.getInstance().getUserList());
                    });
                }

                @Override
                public void entriesDeleted(Collection<String> addresses) {
                    for (String item : addresses) {
                        for (int i = 0; i < XmppHandler.getInstance().getUserList().size(); i++) {
                            User user = XmppHandler.getInstance().getUserList().get(i);
                            if (item.contains(user.getName())) {
                                XmppHandler.getInstance().getUserList().remove(i);
                                break;
                            }
                        }
                    }
                    Utils.runOnUi(() -> {
                        adapter.setListsItems(XmppHandler.getInstance().getUserList());
                    });
                }

                @Override
                public void presenceChanged(Presence presence) {
                    Utils.runOnUi(() -> {
                        for (User user : XmppHandler.getInstance().getUserList()) {
                            if (presence.getFrom().contains(user.getName())) {
                                user.setPresence(presence);
                                break;
                            }
                        }
                        adapter.setListsItems(XmppHandler.getInstance().getUserList());
                    });
                }
            });
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
