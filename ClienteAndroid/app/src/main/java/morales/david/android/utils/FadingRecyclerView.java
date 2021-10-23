package morales.david.android.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

public class FadingRecyclerView extends RecyclerView {

    public FadingRecyclerView(Context context) {
        super(context);
    }

    public FadingRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadingRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean isPaddingOffsetRequired() {
        return true;
    }

    @Override
    protected int getTopPaddingOffset() {
        return -getPaddingTop();
    }

    @Override
    protected int getBottomPaddingOffset() {
        return getPaddingBottom();
    }

}
