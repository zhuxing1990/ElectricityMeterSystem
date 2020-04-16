package com.vunke.electricity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.bumptech.glide.Glide;
import com.vunke.electricity.util.LogUtil;


public class TvRecyclerView extends RecyclerView {
    private static final String TAG = "TvRecyclerView";
    private boolean isHasNextItem;
    private OnInterceptListener mInterceptLister;
    private int mLoadMoreBeforehandCount;
    public OnItemListener mOnItemListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mSelectedItemCentered;
    private int mSelectedItemOffsetEnd;
    private int mSelectedItemOffsetStart;
    //选择位置
    private int position;
    //当前选择的view
    private View selectView;
    private FocusGainListener mFocusGainListener;
    //是否可以纵向移出
    private boolean mCanFocusOutVertical = true;
    //是否可以横向移出
    private boolean mCanFocusOutHorizontal = true;

    private FocusLostListener mFocusLostListener;
    private OnloadComplete loadLis;
    private boolean bInitLay = false;
    private boolean isLoad;

    public interface OnloadComplete {
        public void layComplete();
    }

    public interface FocusGainListener {
        void onFocusGain(View child, View focued);
    }

    public interface FocusLostListener {
        void onFocusLost(View lastFocusChild, int direction);
    }

    public static String getTAG() {
        return TAG;
    }

    public void setFocusLostListener(FocusLostListener focusLostListener) {
        this.mFocusLostListener = focusLostListener;
    }

    public void setLoadCompleteListener(OnloadComplete listener){
        this.loadLis = listener;
    }

    public boolean ismCanFocusOutVertical() {
        return mCanFocusOutVertical;
    }

    public void setGainFocusListener(FocusGainListener focusListener) {
        this.mFocusGainListener = focusListener;
    }

    public void setCanFocusOutVertical(boolean mCanFocusOutVertical) {
        this.mCanFocusOutVertical = mCanFocusOutVertical;
    }

    public boolean ismCanFocusOutHorizontal() {
        return mCanFocusOutHorizontal;
    }

    public void setCanFocusOutHorizontal(boolean mCanFocusOutHorizontal) {
        this.mCanFocusOutHorizontal = mCanFocusOutHorizontal;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnItemListener {
        void onItemPreSelected(TvRecyclerView tvRecyclerView, View view, int i);

        void onItemSelected(TvRecyclerView tvRecyclerView, View view, int i);

        void onReviseFocusFollow(TvRecyclerView tvRecyclerView, View view, int i);
    }

    class C09481 implements OnFocusChangeListener {
        public void onFocusChange(View itemView, boolean hasFocus) {
            if (hasFocus) {
                TvRecyclerView.this.mOnItemListener.onItemSelected(TvRecyclerView.this, itemView, TvRecyclerView.this.getChildLayoutPosition(itemView));
            } else {
                TvRecyclerView.this.mOnItemListener.onItemPreSelected(TvRecyclerView.this, itemView, TvRecyclerView.this.getChildLayoutPosition(itemView));
            }
        }
    }

    public void setbInitLay(boolean bInitLay) {
        this.bInitLay = bInitLay;
    }

    public boolean isHasNextItem() {
        return this.isHasNextItem;
    }

    public void setHasNextItem(boolean hasNextItem) {
        this.isHasNextItem = hasNextItem;
    }

    public TvRecyclerView(Context context) {
        this(context, null);
    }

    public TvRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TvRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLoadMoreBeforehandCount = 0;
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        initView();
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        // setDescendantFocusability(AccessibilityNodeInfoCompat.ACTION_EXPAND);
        setHasFixedSize(true);
        setWillNotDraw(true);
        setOverScrollMode(2);
        setChildrenDrawingOrderEnabled(true);
        setClipChildren(false);
        setClipToPadding(false);
        setClickable(false);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setItemAnimator(null);
        setNestedScrollingEnabled(false);
        setItemViewCacheSize(2);
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        if (child != null && this.mOnItemListener != null) {
            child.setOnFocusChangeListener(new C09481());
        }
    }

    private int getPositionByView(View view) {
        if (view == null) {
            return -1;
        }
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (params == null || params.isItemRemoved()) {
            return -1;
        }
        return params.getViewPosition();
    }

    public View getSelectView() {
        if (this.selectView == null) {
            this.selectView = getFocusedChild();
        }
        return this.selectView;
    }

    public int getSelectPosition() {
        View view = getSelectView();
        if (view != null) {
            return getPositionByView(view);
        }
        return -1;
    }

    public void setmOnItemListener(OnItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

    private int getFreeWidth() {
        return (getWidth() - getPaddingLeft()) - getPaddingRight();
    }

    private int getFreeHeight() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }


    @Override
    public boolean isInTouchMode() {
        if (VERSION.SDK_INT == 19) {
            return !hasFocus() || super.isInTouchMode();
        } else {
            return super.isInTouchMode();
        }
    }

    @Override
    public View focusSearch(View focused, int direction) {
        LogUtil.i(TAG, "focusSearch " + focused + ",direction= " + direction);
        View view = super.focusSearch(focused, direction);
        if (focused == null) {
            return view;
        }

        if (view != null) {

            //该方法返回焦点view所在的父view,如果是在recyclerview之外，就会是null.所以根据是否是null,来判断是否是移出了recyclerview
            View nextFocusItemView = findContainingItemView(view);
            if (nextFocusItemView == null) {
                //调用移出的监听
                if (mFocusLostListener != null) {
                    mFocusLostListener.onFocusLost(focused, direction);
                }

                if (!mCanFocusOutVertical && (direction == View.FOCUS_DOWN || direction == View.FOCUS_UP)) {
                    //屏蔽焦点纵向移出recyclerview
                    return focused;
                }
                if (!mCanFocusOutHorizontal && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT)) {
                    //屏蔽焦点横向移出recyclerview
                    return focused;
                }

                return view;
            }
        }
        return view;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (!hasFocus()) {
            //recyclerview 子view 重新获取焦点，调用移入焦点的事件监听
            if (mFocusGainListener != null) {
                mFocusGainListener.onFocusGain(child, focused);
            }
        }
        super.requestChildFocus(child, focused);
        View view = getFocusedChild();
        if (view != null) {
            this.selectView = child;
            //处理焦点居中屏幕
            if (this.mSelectedItemCentered) {
                if (isVertical()) {
                    smoothScrollBy(0, ((int) (view.getY() - ((float) (getHeight() / 2)))) + (view.getHeight() / 2));
                } else {
                    smoothScrollBy(((int) (view.getX() - ((float) (getWidth() / 2)))) + (view.getWidth() / 2), 0);
                }
            }
        }


    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        int dx;
        int parentLeft = getPaddingLeft();
        int parentRight = getWidth() - getPaddingRight();
        int parentTop = getPaddingTop();
        int parentBottom = getHeight() - getPaddingBottom();
        int childLeft = child.getLeft() + rect.left;
        int childTop = child.getTop() + rect.top;
        int childRight = childLeft + rect.width();
        int childBottom = childTop + rect.height();
        int offScreenLeft = Math.min(0, (childLeft - parentLeft) - this.mSelectedItemOffsetStart);
        int offScreenRight = Math.max(0, (childRight - parentRight) + this.mSelectedItemOffsetEnd);
        int offScreenTop = Math.min(0, (childTop - parentTop) - this.mSelectedItemOffsetStart);
        int offScreenBottom = Math.max(0, (childBottom - parentBottom) + this.mSelectedItemOffsetEnd);
        boolean canScrollHorizontal = getLayoutManager().canScrollHorizontally();
        boolean canScrollVertical = getLayoutManager().canScrollVertically();
        if (!canScrollHorizontal) {
            dx = 0;
        } else if (ViewCompat.getLayoutDirection(this) == 1) {
            if (offScreenRight != 0) {
                dx = offScreenRight;
            } else {
                dx = Math.max(offScreenLeft, childRight - parentRight);
            }
        } else if (offScreenLeft != 0) {
            dx = offScreenLeft;
        } else {
            dx = Math.min(childLeft - parentLeft, offScreenRight);
        }
        int dy = canScrollVertical ? offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom) : 0;
        if (dx == 0 && dy == 0) {
            return false;
        }
        if (immediate) {
            scrollBy(dx, dy);
        } else {
            smoothScrollBy(dx, dy);
        }
        postInvalidate();
        return true;
    }

    public int getBaseline() {
        return -1;
    }

    public int getSelectedItemOffsetStart() {
        return this.mSelectedItemOffsetStart;
    }

    public int getSelectedItemOffsetEnd() {
        return this.mSelectedItemOffsetEnd;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    private boolean isVertical() {
        if (getLayoutManager() == null) {
            return false;
        }
        if (((LinearLayoutManager) getLayoutManager()).getOrientation() == 1) {
            return true;
        }
        return false;
    }

    public void setSelectedItemOffset(int offsetStart, int offsetEnd) {
        setSelectedItemAtCentered(false);
        this.mSelectedItemOffsetStart = offsetStart;
        this.mSelectedItemOffsetEnd = offsetEnd;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int direction = -1;
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    direction = FOCUS_LEFT;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    direction = FOCUS_RIGHT;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    direction = FOCUS_UP;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    direction = FOCUS_DOWN;
                    break;
                default:
                    break;
            }
            if (direction != -1) {
                View focusView = getFocusedChild();
                try {
                    View view = FocusFinder.getInstance().findNextFocus(this, focusView, direction);
                    if (view != null) {
                        view.requestFocus();
                        return true;
                    } else {
                        position = indexOfChild(focusView);
                        if (direction == FOCUS_RIGHT || direction == FOCUS_LEFT) {
                            if (!mCanFocusOutHorizontal) {
                                return true;
                            }
                        } else if (direction == FOCUS_UP || direction == FOCUS_DOWN) {
                            if (!mCanFocusOutVertical) {
                                return true;
                            }
                        }
                    }
                }catch (Exception e){

                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    //焦点居中设置
    public void setSelectedItemAtCentered(boolean isCentered) {
        this.mSelectedItemCentered = isCentered;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View view = getFocusedChild();
        if (view == null) {
            return i;
        }
        this.position = getChildAdapterPosition(view) - getFirstVisiblePosition();
        if (this.position < 0) {
            return i;
        }
        if (i == childCount - 1) {
            if (this.position > i) {
                this.position = i;
            }
            return this.position;
        } else if (i == this.position) {
            return childCount - 1;
        } else {
            return i;
        }
    }

    public int getFirstVisiblePosition() {
        if (getChildCount() == 0) {
            return 0;
        }
        return getChildAdapterPosition(getChildAt(0));
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLastVisiblePosition() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        }
        return getChildAdapterPosition(getChildAt(childCount - 1));
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setOnInterceptListener(OnInterceptListener listener) {
        this.mInterceptLister = listener;
    }

    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }

    @Override
    public void onScrollStateChanged(int state) {
        //修改网格模式加载更多时的算法
        if (isLoad){

        }
        if (state == SCROLL_STATE_IDLE) {
            Glide.with(getContext()).resumeRequests();
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            }
            // 加载更多回调
            if (null != mOnLoadMoreListener) {
                if (getLastVisiblePosition() >= getAdapter().getItemCount() - (1 + mLoadMoreBeforehandCount)) {
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        } else {
            Glide.with(getContext()).pauseRequests();
        }
        super.onScrollStateChanged(state);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);
    }



    @Override
    protected void onDetachedFromWindow() {
        if (getLayoutManager() != null) {
            super.onDetachedFromWindow();
        }
    }
    //是否是最右边
    public boolean isRightEdge(int childPosition) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
            int totalSpanCount = gridLayoutManager.getSpanCount();
            int totalItemCount = gridLayoutManager.getItemCount();
            int childSpanCount = 0;
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (!isVertical()) {
                int lastColumnSize = totalItemCount % totalSpanCount;
                if (lastColumnSize == 0) {
                    lastColumnSize = totalSpanCount;
                }
                if (childSpanCount > totalItemCount - lastColumnSize) {
                    return true;
                }
            } else if (childSpanCount % gridLayoutManager.getSpanCount() == 0) {
                return true;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (isVertical() || childPosition == getLayoutManager().getItemCount() - 1) {
                return true;
            }
            return false;
        }
        return false;
    }
    //是否是最左边
    public boolean isLeftEdge(int childPosition) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
            int totalSpanCount = gridLayoutManager.getSpanCount();
            int childSpanCount = 0;
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (isVertical()) {
                if (childSpanCount % gridLayoutManager.getSpanCount() == 1) {
                    return true;
                }
            } else if (childSpanCount <= totalSpanCount) {
                return true;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (isVertical() || childPosition == 0) {
                return true;
            }
            return false;
        }
        return false;
    }
    //是否是最上面
    public boolean isTopEdge(int childPosition) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
            int totalSpanCount = gridLayoutManager.getSpanCount();
            int childSpanCount = 0;
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (isVertical()) {
                if (childSpanCount <= totalSpanCount) {
                    return true;
                }
            } else if (childSpanCount % totalSpanCount == 1) {
                return true;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (!isVertical() || childPosition == 0) {
                return true;
            }
            return false;
        }
        return false;
    }
//是否是最下面
    public boolean isBottomEdge(int childPosition) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
            int itemCount = gridLayoutManager.getItemCount();
            int childSpanCount = 0;
            int totalSpanCount = gridLayoutManager.getSpanCount();
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (isVertical()) {
                int lastRowCount = itemCount % totalSpanCount;
                if (lastRowCount == 0) {
                    lastRowCount = gridLayoutManager.getSpanCount();
                }
                if (childSpanCount > itemCount - lastRowCount) {
                    return true;
                }
            } else if (childSpanCount % totalSpanCount == 0) {
                return true;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (!isVertical() || childPosition == getLayoutManager().getItemCount() - 1) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Nullable
    public View findContainingItemView(View view) {
        ViewParent parent = view.getParent();
        while (parent != null && parent != this && parent instanceof View) {
            view = (View) parent;
            parent = view.getParent();
        }
        return parent == this ? view : null;
    }

    /**
     * Returns the ViewHolder that contains the given view.
     *
     * @param view The view that is a descendant of the RecyclerView.
     * @return The ViewHolder that contains the given view or null if the provided view is not a
     * descendant of this RecyclerView.
     */
    @Nullable
    public ViewHolder findContainingViewHolder(View view) {
        View itemView = findContainingItemView(view);
        return itemView == null ? null : getChildViewHolder(itemView);
    }

    //处理焦点返回
    @Override
    public boolean requestFocus(int direction, Rect rect) {
//        LogUtilUtils.d("direction::" + position);
        if (getChildAt(position) != null) {
            getChildAt(position).requestFocus();
            return true;
        }
        return false;
    }
    public int getItemCount() {
        if(null != getAdapter()) {
            return getAdapter().getItemCount();
        }
        return 0;
    }
    /**
     * 选中指定项
     * 平滑的滚动到指定位置
     *
     * @param position 对应的位置索引
     */
    public void setSelectionWithSmooth(int position) {
        if(null == getAdapter() || position < 0 || position >= getItemCount()) {
            return;
        }
        this.position= position;
        TvSmoothScroller scroller = new TvSmoothScroller(getContext(), true,
                true, mSelectedItemOffsetStart);
        scroller.setTargetPosition(position);
        getLayoutManager().startSmoothScroll(scroller);
    }
    /**
     * 选中指定项 默认初始话化选中
     *
     * @param position 对应的位置索引
     */
    public void setSelection(int position) {
        if(null == getAdapter() || position < 0 || position >= getItemCount()) {
            return;
        }
        this.position = position;
        View view = null;
        if(null != getLayoutManager()) {
            view = getLayoutManager().findViewByPosition(position);
        }
        if(null != view) {
            if(!hasFocus()) {
                //模拟TvRecyclerView获取焦点
                onFocusChanged(true, FOCUS_DOWN, null);
            }
            view.requestFocus();
        }else {
            TvSmoothScroller scroller = new TvSmoothScroller(getContext(), true, false, mSelectedItemOffsetStart);
            scroller.setTargetPosition(position);
            getLayoutManager().startSmoothScroll(scroller);
        }
        scrollToPosition(position);
    }

    //重写滑动
    private class TvSmoothScroller extends LinearSmoothScroller {
        private boolean mRequestFocus;
        private boolean mIsSmooth;
        private int mOffset;

        public TvSmoothScroller(Context context, boolean isRequestFocus, boolean isSmooth, int offset) {
            super(context);
            mRequestFocus = isRequestFocus;
            mIsSmooth = isSmooth;
            mOffset = offset;
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            return mIsSmooth ? super.calculateTimeForScrolling(dx) :
                    ((int) Math.ceil(Math.abs(dx) * (4f / getContext().getResources().getDisplayMetrics().densityDpi)));
        }

        @Override
        protected void onTargetFound(View targetView, State state, Action action) {
            if(mSelectedItemCentered && null != getLayoutManager()) {
                getDecoratedBoundsWithMargins(targetView, mTempRect);
                mOffset = (getLayoutManager().canScrollHorizontally() ? (getFreeWidth() - mTempRect.width())
                        : (getFreeHeight() - mTempRect.height())) / 2;
            }
            super.onTargetFound(targetView, state, action);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            int dt = boxStart - viewStart + mOffset;
            return dt;
        }

        @Override
        protected void onStop() {
            if(mRequestFocus) {
                final int position = getTargetPosition();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final View itemView = TvRecyclerView.this.getLayoutManager().findViewByPosition(position);
                        if (null != itemView) {
                            if (!hasFocus()) {
                                onFocusChanged(true, FOCUS_DOWN, null);
                            }
                            itemView.requestFocus();
                        } else {

                        }
                    }
                }, mIsSmooth ? 400 : 100);
            }
            super.onStop();
        }
    }
    private final Rect mTempRect = new Rect();
    //处理数据初次加载完毕
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        LogUtil.e(TAG, "=====onGlobalLayout====" + changed);
        //数据加载完成回掉
        if (!changed&& !bInitLay) {
            if (loadLis != null) {
                bInitLay = true;
                loadLis.layComplete();
            }
        }
    }
}