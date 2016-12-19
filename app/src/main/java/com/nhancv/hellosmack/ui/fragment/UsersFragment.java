package com.nhancv.hellosmack.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.widget.EditText;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.ui.adapter.UsersAdapter;
import com.nhancv.xmpp.XmppPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smack.SmackException;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
@EFragment(R.layout.fragment_users)
public class UsersFragment extends Fragment {
    private static final String TAG = UsersFragment.class.getName();
    @ViewById(R.id.vListsItems)
    RecyclerView vListsItems;
    UsersAdapter adapter;
    AlertDialog addContact;

    @AfterViews
    void initView() {

        initDialogAddContact();

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        vListsItems.setHasFixedSize(true);
        vListsItems.setLayoutManager(llm);
        adapter = new UsersAdapter();
        vListsItems.setAdapter(adapter);

        NUtil.aSyncTask(subscriber -> {
            XmppPresenter.getInstance().setAutoAcceptSubscribe();
            XmppPresenter.getInstance().addMessageStanzaListener(message -> UsersFragment.this.updateAdapterList());

            XmppPresenter.getInstance().setupRosterList(this::updateAdapterList);
            updateAdapterList();
        });
    }

    @Click(R.id.btAddContact)
    void btAddContactOnClick() {
        addContact.show();
    }

    private void updateAdapterList() {
        NUtil.runOnUi(() -> {
            adapter.setListsItems(XmppPresenter.getInstance().getCurrentRosterList());
        });
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
