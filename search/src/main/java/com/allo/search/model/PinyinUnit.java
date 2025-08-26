package com.allo.search.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PinyinUnit implements Cloneable {
    private boolean mPinyin = false;
    private int mStartPosition = -1;
    private List<PinyinBaseUnit> mPinyinBaseUnitIndex = new ArrayList<>();

    public PinyinUnit() {
    }

    public boolean isPinyin() {
        return this.mPinyin;
    }

    public void setPinyin(boolean pinyin) {
        this.mPinyin = pinyin;
    }

    public int getStartPosition() {
        return this.mStartPosition;
    }

    public void setStartPosition(int startPosition) {
        this.mStartPosition = startPosition;
    }

    public List<PinyinBaseUnit> getPinyinBaseUnitIndex() {
        return this.mPinyinBaseUnitIndex;
    }

    public void setPinyinBaseUnitIndex(List<PinyinBaseUnit> pinyinBaseUnitIndex) {
        this.mPinyinBaseUnitIndex = pinyinBaseUnitIndex;
    }

    @NotNull
    public Object clone() throws CloneNotSupportedException {
        PinyinUnit obj = (PinyinUnit)super.clone();
        obj.mPinyinBaseUnitIndex = new ArrayList<>();

        for (PinyinBaseUnit pbu : this.mPinyinBaseUnitIndex) {
            obj.mPinyinBaseUnitIndex.add((PinyinBaseUnit) pbu.clone());
        }

        return obj;
    }
}

