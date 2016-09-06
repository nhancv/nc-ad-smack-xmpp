package com.nhancv.hellosmack.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.listener.ICollections;
import com.nhancv.hellosmack.ui.adapter.ChatAdapter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;

/**
 * Created by nhancao on 9/6/16.
 */
public class NDialog {
    private static final String TAG = NDialog.class.getName();

    public static Dialog showChatDialog(@NonNull Context uiContext, Chat chat, @Nullable ICollections.IDialogConfirmButtonImpl callback) {

        View view = LayoutInflater.from(uiContext).inflate(R.layout.dialog_chat, null, false);
        Dialog dialog = new Dialog(uiContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);

        //Setup View
        RecyclerView vListsItems = (RecyclerView) view.findViewById(R.id.vListsItems);
        ChatAdapter adapter = new ChatAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(uiContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        vListsItems.setHasFixedSize(true);
        vListsItems.setLayoutManager(llm);
        vListsItems.setAdapter(adapter);

        EditText etInput = (EditText) view.findViewById(R.id.etInput);
        Button btOk = (Button) view.findViewById(R.id.btSend);
        Button btCancel = (Button) view.findViewById(R.id.btClose);

        chat.addMessageListener((chat1, message) -> {
            adapter.addMessage(message);
            Log.e(TAG, "processMessage: " + message);
        });

        btOk.setOnClickListener(view1 -> {
            if (callback == null) {
                try {
                    chat.sendMessage(etInput.getText().toString());
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }


            } else {
                callback.positive(dialog, view1, view);
            }
        });
        btCancel.setOnClickListener(view1 -> {
            if (callback == null) {
                dialog.dismiss();
            } else {
                callback.negative(dialog, view1, view);
            }
        });

        return dialog;
    }


}
