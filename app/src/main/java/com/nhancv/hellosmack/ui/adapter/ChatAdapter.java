package com.nhancv.hellosmack.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.model.BaseBody;
import com.nhancv.xmpp.model.BaseMessage;

import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.jxmpp.util.XmppStringUtils.parseBareJid;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ListsHolder> {

    private List<BaseMessage> messageList;

    public ChatAdapter() {
        this.messageList = new ArrayList<>();
    }

    public void setListsItems(List<BaseMessage> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @Override
    public ChatAdapter.ListsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_item, parent, false);
        return new ListsHolder(view);

    }

    @Override
    public void onBindViewHolder(ChatAdapter.ListsHolder holder, int position) {
        BaseMessage baseMessage = messageList.get(position);
        BaseBody body = BaseBody.fromJson(baseMessage.getMessage().getBody());
        String to = baseMessage.getMessage().getTo();
        boolean isLeft = !XmppStringUtils.parseBareJid(to).contains(
                parseBareJid(XmppPresenter.getInstance().getCurrentUser()));

        String status = NUtil.toRelativeTime(holder.itemView.getContext(), body.getTimestamp()) + (isLeft ? (baseMessage.isSeen() ? " - seen" : (baseMessage.isDelivered() ? " - delivered" : " - sent")) : "");

        if (isLeft) {
            holder.tvTimeLeft.setText(status);
            holder.tvMsgLeft.setText(body.getContent());
            if (!baseMessage.isRead()) {
                holder.vItemLeft.setBackgroundResource(R.drawable.chat_left_unread);
            } else {
                holder.vItemLeft.setBackgroundResource(R.drawable.chat_left_read);
            }

            holder.vContentLeft.setVisibility(View.VISIBLE);
            holder.vContentRight.setVisibility(View.GONE);
        } else {

            holder.tvTimeRight.setText(status);
            holder.tvMsgRight.setText(BaseBody.fromJson(baseMessage.getMessage().getBody()).getContent());
            if (!baseMessage.isRead()) {
                holder.vItemLeft.setBackgroundResource(R.drawable.chat_right_unread);
            } else {
                holder.vItemLeft.setBackgroundResource(R.drawable.chat_right_read);
            }

            holder.vContentLeft.setVisibility(View.GONE);
            holder.vContentRight.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static final class ListsHolder extends RecyclerView.ViewHolder {

        View vItemLeft, vItemRight;
        View vContentLeft, vContentRight;
        ImageView vImgLeft;
        TextView tvMsgLeft, tvMsgRight;
        TextView tvTimeLeft, tvTimeRight;

        public ListsHolder(View itemView) {
            super(itemView);
            //Left content
            vContentLeft = itemView.findViewById(R.id.item_messenger_chat_ll_item_content_left);
            vItemLeft = itemView.findViewById(R.id.item_messenger_chat_ll_item_left);
            vImgLeft = (ImageView) itemView.findViewById(R.id.item_messenger_chat_ll_item_img_left);
            tvMsgLeft = (TextView) itemView.findViewById(R.id.item_messenger_chat_tv_msg_left);
            tvTimeLeft = (TextView) itemView.findViewById(R.id.item_messenger_chat_tv_time_left);
            //Right content
            vContentRight = itemView.findViewById(R.id.item_messenger_chat_ll_item_content_right);
            vItemRight = itemView.findViewById(R.id.item_messenger_chat_ll_item_right);
            tvMsgRight = (TextView) itemView.findViewById(R.id.item_messenger_chat_tv_msg_right);
            tvTimeRight = (TextView) itemView.findViewById(R.id.item_messenger_chat_tv_time_right);
        }
    }

}
