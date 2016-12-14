package com.nhancv.hellosmack.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.listener.XMPPStanzaListener;
import com.nhancv.hellosmack.model.User;
import com.nhancv.hellosmack.ui.adapter.UsersAdapter;
import com.nhancv.hellosmack.xmpp.XmppPresenter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;

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
    AlertDialog addContact;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        unbinder = ButterKnife.bind(this, view);

        initDialogAddContact();

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        vListsItems.setHasFixedSize(true);
        vListsItems.setLayoutManager(llm);
        adapter = new UsersAdapter();
        vListsItems.setAdapter(adapter);

        XmppPresenter.getInstance().setAutoAcceptSubscribe();
        XmppPresenter.getInstance().addAsyncStanzaListener(new XMPPStanzaListener(packet -> {
            if (packet instanceof Message) {
                Log.e(TAG, "Message: " + packet);
                Message message = (Message) packet;
                for (User user : XmppPresenter.getInstance().getUserList()) {
                    if (message.getFrom().contains(user.getName())) {
                        user.setLastMessage(message.getBody());
                        break;
                    }
                }
                NUtil.runOnUi(() -> {
                    adapter.setListsItems(XmppPresenter.getInstance().getUserList());
                });
            }
        }, Message.class));

        XmppPresenter.getInstance().getUserList(roster -> {
            adapter.setListsItems(XmppPresenter.getInstance().getUserList());
            roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
            roster.addRosterListener(new RosterListener() {
                @Override
                public void entriesAdded(Collection<String> addresses) {
                    for (String item : addresses) {
                        Presence presence = roster.getPresence(item);
                        XmppPresenter.getInstance().getUserList().add(new User(item, presence));
                    }
                    NUtil.runOnUi(() -> {
                        adapter.setListsItems(XmppPresenter.getInstance().getUserList());
                    });
                }

                @Override
                public void entriesUpdated(Collection<String> addresses) {
                    for (String item : addresses) {
                        for (User user : XmppPresenter.getInstance().getUserList()) {
                            if (item.contains(user.getName())) {
                                Presence presence = roster.getPresence(item);
                                user.setPresence(presence);
                                break;
                            }
                        }
                    }
                    NUtil.runOnUi(() -> {
                        adapter.setListsItems(XmppPresenter.getInstance().getUserList());
                    });
                }

                @Override
                public void entriesDeleted(Collection<String> addresses) {
                    for (String item : addresses) {
                        for (int i = 0; i < XmppPresenter.getInstance().getUserList().size(); i++) {
                            User user = XmppPresenter.getInstance().getUserList().get(i);
                            if (item.contains(user.getName())) {
                                XmppPresenter.getInstance().getUserList().remove(i);
                                break;
                            }
                        }
                    }
                    NUtil.runOnUi(() -> {
                        adapter.setListsItems(XmppPresenter.getInstance().getUserList());
                    });
                }

                @Override
                public void presenceChanged(Presence presence) {
                    NUtil.runOnUi(() -> {
                        for (User user : XmppPresenter.getInstance().getUserList()) {
                            if (presence.getFrom().contains(user.getName())) {
                                user.setPresence(presence);
                                break;
                            }
                        }
                        adapter.setListsItems(XmppPresenter.getInstance().getUserList());
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

    /**
     * Handle add new contact show dialog
     */
    public void btAddContactOnClick() {
        addContact.show();
    }

    /**
     * Init dialog add new contact
     */
    private void initDialogAddContact() {
        if (addContact == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Add contact");

            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint("test1@local.beesightsoft.com");
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                try {
                    XmppPresenter.getInstance().sendInviteRequest(input.getText().toString());
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });
            addContact = builder.create();
        }
    }

}
