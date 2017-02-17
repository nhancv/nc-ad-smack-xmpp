package com.nhancv.hellosmack.helper;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by nhancao on 2/16/17.
 */

public class NLinearLayoutManager extends LinearLayoutManager {

    private boolean isScrollVerticallyEnabled = true;
    private boolean isScrollHorizontallyEnabled = true;

    public NLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollVerticallyEnabled && super.canScrollVertically();
    }

    @Override
    public boolean canScrollHorizontally() {
        return isScrollHorizontallyEnabled && super.canScrollHorizontally();
    }

    public void setScrollHorizontallyEnabled(boolean flag) {
        this.isScrollHorizontallyEnabled = flag;
    }

    public void setScrollVerticallyEnabled(boolean flag) {
        this.isScrollVerticallyEnabled = flag;
    }

}