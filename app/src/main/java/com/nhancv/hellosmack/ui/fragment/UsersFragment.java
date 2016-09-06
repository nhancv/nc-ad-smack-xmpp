package com.nhancv.hellosmack.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.XmppHandler;
import com.nhancv.hellosmack.ui.NDialog;
import com.nhancv.hellosmack.ui.adapter.UsersAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class UsersFragment extends Fragment {

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

        XmppHandler.getInstance().getUserList(listItems -> {
            adapter.setListsItems(listItems);
        });
        XmppHandler.getInstance().setupIncomingChat(chat -> {
            NDialog.showChatDialog(getContext(), chat, null).show();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
