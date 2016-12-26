package com.nhancv.hellosmack.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.XmppUtil;
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
public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ListsHolder> {

    private List<BaseMessage> listsItems;

    public ChatRoomAdapter() {
        this.listsItems = new ArrayList<>();
    }

    public void setListsItems(List<BaseMessage> listsItems) {
        this.listsItems = listsItems;
        notifyDataSetChanged();
    }

    @Override
    public ChatRoomAdapter.ListsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_item, parent, false);
        return new ListsHolder(view);

    }

    @Override
    public void onBindViewHolder(ChatRoomAdapter.ListsHolder holder, int position) {
        BaseMessage baseMessage = listsItems.get(position);

        boolean isLeft;
        String title;
        if (XmppUtil.isGroupMessage(baseMessage.getMessage().toXML().toString())) {
            String to = XmppStringUtils.parseResource(baseMessage.getMessage().getFrom());
            isLeft = XmppStringUtils.parseBareJid(to).contains(
                    parseBareJid(XmppPresenter.getInstance().getCurrentUser()));
            if (isLeft) baseMessage.setRead(true);

            //from format = <room>@<servervice name>/<user chat>
            title = XmppStringUtils.parseResource(baseMessage.getMessage().getFrom());
        } else {
            String to = baseMessage.getMessage().getTo();
            isLeft = !XmppStringUtils.parseBareJid(to).contains(
                    parseBareJid(XmppPresenter.getInstance().getCurrentUser()));

            title = baseMessage.getMessage().getTo() + (isLeft ? (baseMessage.isDelivered() ? " - delivered" : " - sent") : "");
        }

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
