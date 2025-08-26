package com.allo.search.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PinyinSearchUnit implements Cloneable {
    private String mBaseData;
    private List<PinyinUnit> mPinyinUnits;
    private StringBuffer mMatchKeyword;

    public PinyinSearchUnit() {
        this.initPinyinSearchUnit();
    }

    public PinyinSearchUnit(String baseData) {
        this.mBaseData = baseData;
        this.initPinyinSearchUnit();
    }

    public String getBaseData() {
        return this.mBaseData;
    }

    public void setBaseData(String baseData) {
        this.mBaseData = baseData;
    }

    public List<PinyinUnit> getPinyinUnits() {
        return this.mPinyinUnits;
    }

    public void setPinyinUnits(List<PinyinUnit> pinyinUnits) {
        this.mPinyinUnits = pinyinUnits;
    }

    public StringBuffer getMatchKeyword() {
        return this.mMatchKeyword;
    }

    public void setMatchKeyword(StringBuffer matchKeyword) {
        this.mMatchKeyword = matchKeyword;
    }

    public Object clone() throws CloneNotSupportedException {
        PinyinSearchUnit obj = (PinyinSearchUnit)super.clone();
        obj.mPinyinUnits = new ArrayList<>();
        Iterator var2 = this.mPinyinUnits.iterator();

        while(var2.hasNext()) {
            PinyinUnit pu = (PinyinUnit)var2.next();
            obj.mPinyinUnits.add((PinyinUnit)pu.clone());
        }

        return obj;
    }

    private void initPinyinSearchUnit() {
        this.mPinyinUnits = new ArrayList<>();
        this.mMatchKeyword = new StringBuffer();
    }
}
