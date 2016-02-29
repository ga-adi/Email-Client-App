package com.boloutaredoubeni.emailapp.views.listeners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class InboxItemClickedListener
    implements RecyclerView.OnItemTouchListener {

  private OnItemClickListener mListener;
  private GestureDetector mDetector;

  public InboxItemClickedListener(Context context,
                                  OnItemClickListener listener) {
    mListener = listener;
    mDetector =
        new GestureDetector(context, new GestureDetector.OnGestureListener() {
          @Override
          public boolean onDown(MotionEvent e) {
            return false;
          }

          @Override
          public void onShowPress(MotionEvent e) {}

          @Override
          public boolean onSingleTapUp(MotionEvent e) {
            return true;
          }

          @Override
          public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                  float distanceX, float distanceY) {
            return false;
          }

          @Override
          public void onLongPress(MotionEvent e) {}

          @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2,
                                 float velocityX, float velocityY) {
            return false;
          }
        });
  }

  @Override
  public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
    View childView = rv.findChildViewUnder(e.getX(), e.getY());
    if (childView != null && mListener != null && mDetector.onTouchEvent(e)) {
      mListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
    }
    return false;
  }

  @Override
  public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

  @Override
  public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

  public interface OnItemClickListener {
    void onItemClick(View view, int position);
  }
}
