package com.dots.focus.model;

/**
 * Created by AdrianHsu on 2015/10/10.
 */
public class InboxModel {

  public InboxModel(String _inbox) {mInbox = _inbox;}
  public String mInbox = "";


  public String getmInbox() {
    return mInbox;
  }

  public void setmInbox(String mInbox) {
    this.mInbox = mInbox;
  }

  @Override
  public String toString() {
    return mInbox;
  }
}
