package com.abhigam.www.foodspot;

import android.support.v4.view.ViewPager;

/**
 * Created by sourabhzalke on 09/03/18.
 */

public class CircularViewPagerHandler implements ViewPager.OnPageChangeListener {
    private ViewPager   mViewPager;
    private int         mCurrentPosition;
    private int         mScrollState;

    public CircularViewPagerHandler(final ViewPager viewPager) {
        mViewPager = viewPager;
    }

    @Override
    public void onPageSelected(final int position) {
        mCurrentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        handleScrollState(state);
        mScrollState = state;
    }

    private void handleScrollState(final int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE && mScrollState == ViewPager.SCROLL_STATE_DRAGGING) {
            setNextItemIfNeeded();
        }
    }

    private void setNextItemIfNeeded() {
        if (!isScrollStateSettling()) {
            handleSetNextItem();
        }
    }

    private boolean isScrollStateSettling() {
        return mScrollState == ViewPager.SCROLL_STATE_SETTLING;
    }

    private void handleSetNextItem() {
        final int lastPosition = mViewPager.getAdapter().getCount() - 1;
        if(mCurrentPosition == lastPosition) {
            mViewPager.setCurrentItem(0, true);
        }
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
    }
}
