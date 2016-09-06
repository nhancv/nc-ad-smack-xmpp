package com.nhancv.hellosmack.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhancv.hellosmack.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ListsHolder> {

    private List<String> listsItems;

    public UsersAdapter() {
        this.listsItems = new ArrayList<>();
    }

    /**
     * Set list item
     *
     * @param listsItems
     */
    public void setListsItems(List<String> listsItems) {
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
        String item = listsItems.get(position);
        holder.tvName.setText(item);
        holder.vItem.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return listsItems.size();
    }

    public static final class ListsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vItem)
        View vItem;
        @BindView(R.id.tvName)
        TextView tvName;

        public ListsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
