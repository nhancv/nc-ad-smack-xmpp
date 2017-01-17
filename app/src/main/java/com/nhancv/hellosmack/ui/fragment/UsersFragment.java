package com.nhancv.hellosmack.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.ui.activity.ChatActivity_;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.model.BaseRoster;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
@EFragment(R.layout.fragment_users)
public class UsersFragment extends Fragment {
    private static final String TAG = UsersFragment.class.getName();
    @ViewById(R.id.vListsItems)
    ListView vListsItems;
    @ViewById(R.id.btEditContact)
    Button btEditContact;

    QuickAdapter<BaseRoster> adapter;
    AlertDialog addContact;
    private boolean deleteMode;

    @AfterViews
    void initView() {

        initDialogAddContact();

        adapter = new QuickAdapter<BaseRoster>(getContext(), R.layout.view_user_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, BaseRoster user) {

                helper.setText(R.id.tvName, user.getName());
                helper.setText(R.id.tvLastMsg, TextUtils.isEmpty(user.getLastMessage()) ? "..." : user.getLastMessage());

                int color = ContextCompat.getColor(getContext(), R.color.offline_status);
                if (user.getPresence().isAvailable()) {
                    color = ContextCompat.getColor(getContext(), R.color.online_status);
                }

                helper.setVisible(R.id.vChatDelete, deleteMode);

                View vStatus = helper.getView(R.id.vStatus);
                GradientDrawable gd = (GradientDrawable) vStatus.getBackground().getCurrent();
                gd.setColor(color);
                gd.setStroke(5, NUtil.adjustAlpha(color, 0.5f));

            }
        };
        vListsItems.setAdapter(adapter);
    }

    @ItemClick(R.id.vListsItems)
    public void listItemClick(BaseRoster user) {
        ChatActivity_.intent(getContext()).address(user.getName()).start();
    }

    @ItemLongClick(R.id.vListsItems)
    public void listItemLongClick(BaseRoster user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove contact");
        builder.setMessage("Are you sure to remove this contact?");
        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            XmppPresenter.getInstance().sendUnFriendRequest(user.getName());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }

    @Click(R.id.btAddContact)
    void btAddContactOnClick() {
        addContact.show();
    }

    @Click(R.id.btEditContact)
    void btEditContactOnClick() {
        deleteMode = !deleteMode;
        adapter.notifyDataSetChanged();
        if (deleteMode) {
            btEditContact.setText("Done");
        } else {
            btEditContact.setText("Edit");
        }
    }

    public void updateAdapter() {
        NUtil.runOnUi(() -> {
            if (adapter != null)
                adapter.replaceAll(XmppPresenter.getInstance().getCurrentRosterList());
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
                XmppPresenter.getInstance().sendInviteRequest(input.getText().toString());
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });
            addContact = builder.create();
        }
    }

}
