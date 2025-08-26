package com.allo.search.match;

import android.text.TextUtils;

import com.allo.search.model.PinyinSearchUnit;
import com.allo.utils.RegexUtils;
import com.allo.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class MatchContacts  extends BaseContacts implements Cloneable{
    private static final String TAG="ContactsContacts";
    public enum SearchByType {
        SEARCH_BY_NULL, SEARCH_BY_NAME, SEARCH_BY_PHONE_NUMBER,
    }
    private String mSortKey; // as the sort key word

    private PinyinSearchUnit mNamePinyinSearchUnit;// save the mName converted to Pinyin characters.

    private SearchByType mSearchByType;     // Used to save the type of search
    private StringBuffer mMatchKeywords;    // Used to save the type of Match Keywords.(name or phoneNumber)
    private int mMatchStartIndex;		    //the match start  position of mMatchKeywords in original string(name or phoneNumber).
    private int mMatchLength;			    //the match length of mMatchKeywords in original string(name or phoneNumber).
    private boolean mSelected;	                //whether select contact
    private boolean mFirstMultipleContacts;       //whether the first multiple Contacts
    private boolean mHideMultipleContacts;	      //whether hide multiple contacts
    private boolean mBelongMultipleContactsPhone; //whether belong multiple contacts phone, the value of the variable will not change once you set.

    private boolean mHideOperationView; 		//whether hide operation view
    private MatchContacts mNextContacts;        //point the contacts information who has multiple numbers.

    public MatchContacts(long id , String name, String phoneNumber) {
        super();
        setId(id);
        setName(name);
        setPhoneNumber(phoneNumber);
        setNamePinyinSearchUnit(new PinyinSearchUnit(name));
        setSearchByType(SearchByType.SEARCH_BY_NULL);
        setMatchKeywords(new StringBuffer());
        getMatchKeywords().delete(0, getMatchKeywords().length());
        setMatchStartIndex(-1);
        setMatchLength(0);
        setNextContacts(null);
        setSelected(false);
        setFirstMultipleContacts(true);
        setHideMultipleContacts(false);
        setHideOperationView(true);
        setBelongMultipleContactsPhone(false);
    }

    public MatchContacts(long id, String name, String phoneNumber, String sortKey) {
        super();
        setId(id);
        setName(name);
        setPhoneNumber(phoneNumber);
        setSortKey(sortKey);
        setNamePinyinSearchUnit(new PinyinSearchUnit(name));
        setSearchByType(SearchByType.SEARCH_BY_NULL);
        setMatchKeywords(new StringBuffer());
        getMatchKeywords().delete(0, getMatchKeywords().length());
        setMatchStartIndex(-1);
        setMatchLength(0);
        setNextContacts(null);
        setSelected(false);
        setFirstMultipleContacts(true);
        setHideMultipleContacts(false);
        setHideOperationView(true);
        setBelongMultipleContactsPhone(false);
    }

    @NotNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        MatchContacts obj=(MatchContacts) super.clone();
        obj.mNamePinyinSearchUnit=(PinyinSearchUnit) mNamePinyinSearchUnit.clone();
        obj.mSearchByType=mSearchByType;
        obj.mMatchKeywords=new StringBuffer(mMatchKeywords);
        obj.mNextContacts=mNextContacts;

        return super.clone();
    }

    private static final Comparator<Object> mChineseComparator = Collator.getInstance(Locale.CHINA);

    public static final Comparator<MatchContacts> mDesComparator = (lhs, rhs) -> mChineseComparator.compare(rhs.mSortKey, lhs.mSortKey);

    public static final Comparator<MatchContacts> mAscComparator = (lhs, rhs) -> mChineseComparator.compare(lhs.mSortKey, rhs.mSortKey);

    public static final Comparator<MatchContacts> mSearchComparator = (left, right) -> {
        if (RegexUtils.uyghurFirst(left.getName())&& RegexUtils.uyghurFirst(right.getName())){
            if (left.mMatchStartIndex == right.mMatchStartIndex){
               String rightFirst = StringUtils.INSTANCE.getUyghurFirst(right.getMatchKeywords().toString());
               String leftFirst =  StringUtils.INSTANCE.getUyghurFirst(left.getMatchKeywords().toString());
               return StringUtils.INSTANCE.compareUyLetters(leftFirst,rightFirst);
            } else {
                return Integer.compare(left.mMatchStartIndex,right.mMatchStartIndex);
            }
        }else if (!RegexUtils.uyghurFirst(left.getName()) && RegexUtils.uyghurFirst(right.getName())){
            return 1;
        }else if (RegexUtils.uyghurFirst(left.getName()) && !RegexUtils.uyghurFirst(right.getName())){
            return -1;
        }else {
            int compare = Integer.compare(left.mMatchStartIndex, right.mMatchStartIndex);
            if (compare == 0){
                compare = Integer.compare(left.mMatchLength, right.mMatchLength) *(-1);
            }
            if (compare == 0){
                compare = Integer.compare(left.getName().length(), right.getName().length());
            }
            return compare;
        }
    };

    public static final Comparator<MatchContacts> mUyContactsComparator =(left,right)->{
        return left.getMatchKeywords().substring(0,1).compareToIgnoreCase(right.getMatchKeywords().substring(0,1));
    };


    public PinyinSearchUnit getNamePinyinSearchUnit() {
        return mNamePinyinSearchUnit;
    }

    public void setNamePinyinSearchUnit(PinyinSearchUnit namePinyinSearchUnit) {
        mNamePinyinSearchUnit = namePinyinSearchUnit;
    }


    public String getSortKey() {
        return mSortKey;
    }

    public void setSortKey(String sortKey) {
        mSortKey = sortKey;
    }

    public SearchByType getSearchByType() {
        return mSearchByType;
    }

    public void setSearchByType(SearchByType searchByType) {
        mSearchByType = searchByType;
    }

    public StringBuffer getMatchKeywords() {
        return mMatchKeywords;
    }

    public void setMatchKeywords(StringBuffer matchKeywords) {
        mMatchKeywords = matchKeywords;
    }

    public void setMatchKeywords(String matchKeywords) {
        mMatchKeywords.delete(0, mMatchKeywords.length());
        mMatchKeywords.append(matchKeywords);
    }

    public void clearMatchKeywords() {
        mMatchKeywords.delete(0, mMatchKeywords.length());
    }

    public int getMatchStartIndex() {
        return mMatchStartIndex;
    }

    public void setMatchStartIndex(int matchStartIndex) {
        mMatchStartIndex = matchStartIndex;
    }

    public int getMatchLength() {
        return mMatchLength;
    }

    public void setMatchLength(int matchLength) {
        mMatchLength = matchLength;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public boolean isFirstMultipleContacts() {
        return mFirstMultipleContacts;
    }

    public void setFirstMultipleContacts(boolean firstMultipleContacts) {
        mFirstMultipleContacts = firstMultipleContacts;
    }

    public boolean isHideMultipleContacts() {
        return mHideMultipleContacts;
    }

    public void setHideMultipleContacts(boolean hideMultipleContacts) {
        mHideMultipleContacts = hideMultipleContacts;
    }

    public boolean isBelongMultipleContactsPhone() {
        return mBelongMultipleContactsPhone;
    }

    public void setBelongMultipleContactsPhone(boolean belongMultipleContactsPhone) {
        mBelongMultipleContactsPhone = belongMultipleContactsPhone;
    }

    public boolean isHideOperationView() {
        return mHideOperationView;
    }

    public void setHideOperationView(boolean hideOperationView) {
        mHideOperationView = hideOperationView;
    }

    public MatchContacts getNextContacts() {
        return mNextContacts;
    }

    public void setNextContacts(MatchContacts nextContacts) {
        mNextContacts = nextContacts;
    }

    public static MatchContacts addMultipleContact(MatchContacts contacts, String phoneNumber){
        do{
            if((TextUtils.isEmpty(phoneNumber))||(null==contacts)){
                break;
            }

            MatchContacts currentContact=null;
            MatchContacts nextContacts;
            for(nextContacts=contacts; null!= nextContacts; nextContacts=nextContacts.getNextContacts()){
                currentContact=nextContacts;
                if(nextContacts.getPhoneNumber().equals(phoneNumber)){
                    break;
                }
            }
            MatchContacts cts=null;
            if(null==nextContacts){
                MatchContacts cs = currentContact;
                cts = new MatchContacts(cs.getId(), cs.getName(),phoneNumber);
                cts.setSortKey(cs.getSortKey());
                cts.setNamePinyinSearchUnit(cs.getNamePinyinSearchUnit());// not deep copy
                cts.setFirstMultipleContacts(false);
                cts.setHideMultipleContacts(true);
                cts.setBelongMultipleContactsPhone(true);
                cts.setLookup(cs.getLookup());
                cts.setAvatar(cs.getAvatar());
                cs.setBelongMultipleContactsPhone(true);
                cs.setNextContacts(cts);
                cts.setRawId(cs.getRawId());
                cts.setStar(cs.isStar());
            }

            return cts;
        }while(false);

        return null;
    }


}