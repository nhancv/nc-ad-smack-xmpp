package com.nhancv.hellosmack.helper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.nhancv.hellosmack.R;

/**
 * Created by nhancao on 2/14/17.
 */

public class NPreviewImageDialog extends Dialog {

    private NZoomImageView ivPreview;

    public NPreviewImageDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_preview_image);
        View rlClose = findViewById(R.id.dialog_preview_image_rl_close);
        rlClose.setOnClickListener(v -> {
            dismiss();
        });
        ivPreview = (NZoomImageView) findViewById(R.id.dialog_preview_image_iv_preview);
    }

    public void showImage(String path) {
        show();
        if (ivPreview != null) {
            Glide
                    .with(getContext())
                    .load(path)
                    .crossFade()
                    .into(ivPreview);
        }
    }

}
