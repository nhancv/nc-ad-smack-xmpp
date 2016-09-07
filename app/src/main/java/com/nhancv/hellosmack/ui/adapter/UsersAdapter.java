package com.nhancv.hellosmack.ui.adapter;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.Utils;
import com.nhancv.hellosmack.model.User;
import com.nhancv.hellosmack.ui.activity.ChatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ListsHolder> {
    private static final String TAG = UsersAdapter.class.getName();
    private List<User> listsItems;

    public UsersAdapter() {
        this.listsItems = new ArrayList<>();
    }

    /**
     * Set list item
     *
     * @param listsItems
     */
    public void setListsItems(List<User> listsItems) {
        this.listsItems = listsItems;
        notifyDataSetChanged();
    }

    @Override
    public UsersAdapter.ListsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_item, parent, false);
        return new ListsHolder(view);

    }

    @Override
    public void onBindViewHolder(UsersAdapter.ListsHolder holder, int position) {
        User user = listsItems.get(position);
        holder.tvName.setText(user.getName());
        holder.tvLastMsg.setText(user.getLastMessage());
        holder.vItem.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("address", user.getPresence().getFrom());
            v.getContext().startActivity(intent);
        });
        int color = ContextCompat.getColor(holder.vItem.getContext(), R.color.offline_status);
        if (user.getPresence().isAvailable()) {
            color = ContextCompat.getColor(holder.vItem.getContext(), R.color.online_status);
        }
        GradientDrawable gd = (GradientDrawable) holder.vStatus.getBackground().getCurrent();
        gd.setColor(color);
        gd.setStroke(5, Utils.adjustAlpha(color, 0.5f));
    }

    @Override
    public int getItemCount() {
        return listsItems.size();
    }

    public static final class ListsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vItem)
        View vItem;
        @BindView(R.id.vStatus)
        View vStatus;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvLastMsg)
        TextView tvLastMsg;

        public ListsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
