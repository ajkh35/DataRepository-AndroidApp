package com.repositoryworks.datarepository.utils.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * Created by ajay3 on 7/11/2017.
 */

public class CheckableRelativeLayout extends RelativeLayout implements Checkable{

    private static final int[] STATE_CHECKABLE = {android.R.attr.state_checked};
    private boolean isChecked = false;

    public CheckableRelativeLayout(Context context) {
        super(context);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked) mergeDrawableStates(drawableState, STATE_CHECKABLE);
        return drawableState;
    }
}