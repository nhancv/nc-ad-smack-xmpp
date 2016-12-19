package com.nhancv.hellosmack.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.xmpp.model.BaseMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ListsHolder> {

    private List<BaseMessage> listsItems;

    public ChatAdapter() {
        this.listsItems = new ArrayList<>();
    }

    /**
     * Add message
     *
     * @param message
     */
    public void addMessage(BaseMessage message) {
        listsItems.add(message);
        notifyItemInserted(listsItems.size());
    }

    public void setListsItems(List<BaseMessage> listsItems) {
        this.listsItems = listsItems;
        notifyDataSetChanged();
    }

    @Override
    public ChatAdapter.ListsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_item, parent, false);
        return new ListsHolder(view);

    }

    @Override
    public void onBindViewHolder(ChatAdapter.ListsHolder holder, int position) {
        BaseMessage baseMessage = listsItems.get(position);
        holder.tvTo.setText(baseMessage.getMessage().getTo());
        holder.tvMsg.setText(baseMessage.getMessage().getBody());
        if (baseMessage.isRead()) {
            holder.tvTo.setTextColor(Color.BLACK);
        } else {
            holder.tvTo.setTextColor(Color.BLUE);
        }

    }

    @Override
    public int getItemCount() {
        return listsItems.size();
    }

    public static final class ListsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vItem)
        View vItem;
        @BindView(R.id.tvTo)
        TextView tvTo;
        @BindView(R.id.tvMsg)
        TextView tvMsg;

        public ListsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
