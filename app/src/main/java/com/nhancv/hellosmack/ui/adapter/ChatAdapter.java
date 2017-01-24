package com.nhancv.hellosmack.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.model.BaseMessage;

import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.jxmpp.util.XmppStringUtils.parseBareJid;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ListsHolder> {

    private List<BaseMessage> listsItems;

    public ChatAdapter() {
        this.listsItems = new ArrayList<>();
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
        String to = baseMessage.getMessage().getTo();
        boolean isLeft = !XmppStringUtils.parseBareJid(to).contains(
                parseBareJid(XmppPresenter.getInstance().getCurrentUser()));

        String title = baseMessage.getMessage().getTo() + (isLeft ? (baseMessage.isSeen() ? " - seen" : (baseMessage.isDelivered() ? " - delivered" : " - sent")) : "");
        holder.tvTo.setText(title);
        holder.tvMsg.setText(baseMessage.getMessage().getBody());

        if (!baseMessage.isRead()) {
            holder.vItem.setBackgroundResource(isLeft ? R.drawable.chat_left_unread : R.drawable.chat_right_unread);
            holder.tvTo.setTextColor(Color.BLUE);
        } else {
            holder.vItem.setBackgroundResource(isLeft ? R.drawable.chat_left_read : R.drawable.chat_right_read);
            holder.tvTo.setTextColor(Color.BLACK);
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
