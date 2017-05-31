package de.tinf15b4.kino.smartcinema.util;


/**
 * Created by Michele on 16/11/2015.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;

public class ScrollableAppBar extends AppBarLayout {
    private AppBarLayout.Behavior mBehavior;
    private WeakReference<CoordinatorLayout> mParent;
    private ToolbarChange mQueuedChange = ToolbarChange.NONE;
    private boolean mAfterFirstDraw = false;

    public ScrollableAppBar(Context context) {
        super(context);
    }

    public ScrollableAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!(getLayoutParams() instanceof CoordinatorLayout.LayoutParams) || !(getParent() instanceof CoordinatorLayout)) {
            throw new IllegalStateException("ScrollableAppBar must be a direct child of CoordinatorLayout.");
        } else {
            mParent = new WeakReference<CoordinatorLayout>((CoordinatorLayout) getParent());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mBehavior == null) {
            mBehavior = (Behavior) ((CoordinatorLayout.LayoutParams) getLayoutParams()).getBehavior();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (r - l > 0 && b - t > 0 && mAfterFirstDraw && mQueuedChange != ToolbarChange.NONE) {
            analyzeQueuedChange();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mAfterFirstDraw) {
            mAfterFirstDraw = true;
            if (mQueuedChange != ToolbarChange.NONE) {
                analyzeQueuedChange();
            }
        }
    }

    private synchronized void analyzeQueuedChange() {
        switch (mQueuedChange) {
            case COLLAPSE:
                performCollapsingWithoutAnimation();
                break;
            case COLLAPSE_WITH_ANIMATION:
                performCollapsingWithAnimation();
                break;
            case EXPAND:
                performExpandingWithoutAnimation();
                break;
            case EXPAND_WITH_ANIMATION:
                performExpandingWithAnimation();
                break;
        }

        mQueuedChange = ToolbarChange.NONE;
    }

    public void collapseToolbar() {
        collapseToolbar(false);
    }

    public void collapseToolbar(boolean withAnimation) {
        mQueuedChange = withAnimation ? ToolbarChange.COLLAPSE_WITH_ANIMATION : ToolbarChange.COLLAPSE;
        requestLayout();
    }

    public void expandToolbar() {
        expandToolbar(false);
    }

    public void expandToolbar(boolean withAnimation) {
        mQueuedChange = withAnimation ? ToolbarChange.EXPAND_WITH_ANIMATION : ToolbarChange.EXPAND;
        requestLayout();
    }

    private void performCollapsingWithoutAnimation() {
        if (mParent.get() != null) {
            mBehavior.onNestedPreScroll(mParent.get(), this, null, 0, getHeight()/2, new int[]{0, 0});
        }
    }

    private void performCollapsingWithAnimation() {
        if (mParent.get() != null) {
            mBehavior.onNestedFling(mParent.get(), this, null, 0, getHeight(), true);
        }
    }

    private void performExpandingWithoutAnimation() {
        if (mParent.get() != null) {
            mBehavior.setTopAndBottomOffset(0);
        }
    }

    private void performExpandingWithAnimation() {
        if (mParent.get() != null) {
            mBehavior.onNestedFling(mParent.get(), this, null, 0, -getHeight() * 5, false);
        }
    }

    private enum ToolbarChange {
        COLLAPSE,
        COLLAPSE_WITH_ANIMATION,
        EXPAND,
        EXPAND_WITH_ANIMATION,
        NONE
    }

}