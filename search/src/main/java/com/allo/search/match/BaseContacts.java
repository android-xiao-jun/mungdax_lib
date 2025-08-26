package com.allo.search.match;

import android.net.Uri;

public class BaseContacts implements Cloneable{
    private long mId;
    private String mLookup;
    private String mName;
    private String mPhoneNumber;
    private Uri mAvatar;

    private Long time; /// 拨打电话时间

    private Long rawId = 0L;

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    private boolean star;

    public Long getRawId() {
        return rawId;
    }

    public void setRawId(Long rId) {
        rawId = rId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public Uri getAvatar() {
        return mAvatar;
    }

    public void setAvatar(Uri avatar){
        this.mAvatar = avatar;
    }

    public void setLookup(String lookup) {
        this.mLookup = lookup;
    }
    public String getLookup() {
        return mLookup;
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {

        return super.clone();
    }
}