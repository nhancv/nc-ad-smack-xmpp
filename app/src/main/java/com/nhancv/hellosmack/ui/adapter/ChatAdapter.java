package com.nhancv.hellosmack.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhancv.hellosmack.R;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ListsHolder> {

    private List<Message> listsItems;

    public ChatAdapter() {
        this.listsItems = new ArrayList<>();
    }

    /**
     * Add message
     *
     * @param message
     */
    public void addMessage(Message message) {
        listsItems.add(message);
        notifyItemInserted(listsItems.size() - 1);
    }

    @Override
    public ChatAdapter.ListsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_item, parent, false);
        return new ListsHolder(view);

    }

    @Override
    public void onBindViewHolder(ChatAdapter.ListsHolder holder, int position) {
        Message message = listsItems.get(position);
        holder.tvFrom.setText(message.getFrom());
        holder.tvMsg.setText(message.getBody());

    }

    @Override
    public int getItemCount() {
        return listsItems.size();
    }

    public static final class ListsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vItem)
        View vItem;
        @BindView(R.id.tvFrom)
        TextView tvFrom;
        @BindView(R.id.tvMsg)
        TextView tvMsg;

        public ListsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
