package com.allo.search;

import android.util.Log;

import com.allo.search.match.MatchContacts;
import com.allo.search.model.PinyinSearchUnit;
import com.allo.search.util.QwertyUtil;
import com.allo.search.util.T9Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpellingSearchHelper {

    private static final String TAG = "SpellingSearchHelper";
    private final List<MatchContacts> mBaseContacts = new ArrayList<>();
    private List<MatchContacts> mSearchContacts = new ArrayList<>();

    private final StringBuffer mFirstNoSearchResultInput = new StringBuffer();
    private OnContactsLoad mOnContactsLoad = null;

    private boolean mContactsChanged = true;



    public interface OnContactsLoad {
        void onContactsLoadSuccess();

        void onContactsLoadFailed();
    }

    public interface OnContactsChanged {
        void onContactsChanged();
    }

    public List<MatchContacts> getBaseContacts() {
        return mBaseContacts;
    }

    public List<MatchContacts> getSearchContacts() {
        return mSearchContacts;
    }

    public int getSearchContactsIndex(MatchContacts contacts) {
        int index = -1;
        if (null == contacts) {
            return -1;
        }
        int searchContactsCount = mSearchContacts.size();
        for (int i = 0; i < searchContactsCount; i++) {
            if (contacts.getName().charAt(0) == mSearchContacts.get(i)
                    .getName().charAt(0)) {
                index = i;
                break;
            }
        }

        return index;
    }

    // public void setSearchContacts(List<Contacts> searchContacts) {
    // mSearchContacts = searchContacts;
    // }

    public OnContactsLoad getOnContactsLoad() {
        return mOnContactsLoad;
    }

    public void setOnContactsLoad(OnContactsLoad onContactsLoad) {
        mOnContactsLoad = onContactsLoad;
    }

    private boolean isContactsChanged() {
        return mContactsChanged;
    }

    private void setContactsChanged(boolean contactsChanged) {
        mContactsChanged = contactsChanged;
    }


    /**
     * @param keyword (valid characters include:'0'~'9','*','#')
     * search base data according to string parameter
     */
    public List<MatchContacts> t9InputSearch(String keyword,boolean matchNumber) {

        List<MatchContacts> mSearchByNameContacts = new ArrayList<>();
        List<MatchContacts> mSearchByPhoneNumberContacts = new ArrayList<>();

        if (null == keyword) {// add all base data to search
            mSearchContacts.clear();

            for (int i = 0; i < mBaseContacts.size(); i++) {
                MatchContacts currentContacts;
                for (currentContacts = mBaseContacts.get(i); null != currentContacts; currentContacts = currentContacts.getNextContacts()) {
                    currentContacts.setSearchByType(MatchContacts.SearchByType.SEARCH_BY_NULL);
                    currentContacts.clearMatchKeywords();
                    currentContacts.setMatchStartIndex(-1);
                    currentContacts.setMatchLength(0);
                    if (currentContacts.isFirstMultipleContacts()) {
                        mSearchContacts.add(currentContacts);
                    } else {
                        if (!currentContacts.isHideMultipleContacts()) {
                            mSearchContacts.add(currentContacts);
                        }
                    }
                }
            }

            //mSearchContacts.addAll(mBaseContacts);
            mFirstNoSearchResultInput.delete(0, mFirstNoSearchResultInput.length());
            return mSearchContacts;
        }

        if (mFirstNoSearchResultInput.length() > 0) {
            if (keyword.contains(mFirstNoSearchResultInput.toString())) {
                return mSearchContacts;
            } else {
                mFirstNoSearchResultInput.delete(0, mFirstNoSearchResultInput.length());
            }
        }

        if (null != mSearchContacts) {
            mSearchContacts.clear();
        } else {
            mSearchContacts = new ArrayList<>();
        }

        int contactsCount = mBaseContacts.size();


        for (int i = 0; i < contactsCount; i++) {

            MatchContacts match = mBaseContacts.get(i);
            if (match == null) {
                continue;
            }
            PinyinSearchUnit namePinyinSearchUnit = match.getNamePinyinSearchUnit();
            if (T9Util.match(namePinyinSearchUnit, keyword)) {// search by name;

                MatchContacts currentContacts;
                MatchContacts firstContacts;
                for (currentContacts = mBaseContacts.get(i), firstContacts = currentContacts; null != currentContacts; currentContacts = currentContacts.getNextContacts()) {
                    currentContacts.setSearchByType(MatchContacts.SearchByType.SEARCH_BY_NAME);
                    currentContacts.setMatchKeywords(namePinyinSearchUnit.getMatchKeyword().toString());
                    currentContacts.setMatchStartIndex(firstContacts.getName().indexOf(firstContacts.getMatchKeywords().toString()));
                    currentContacts.setMatchLength(firstContacts.getMatchKeywords().length());
                    mSearchByNameContacts.add(currentContacts);
                }
            } else {
                MatchContacts currentContacts;
                if (matchNumber){
                    for (currentContacts = mBaseContacts.get(i); null != currentContacts; currentContacts = currentContacts.getNextContacts()) {
                        if (currentContacts.getPhoneNumber().contains(keyword)) {// search by phone number
                            currentContacts.setSearchByType(MatchContacts.SearchByType.SEARCH_BY_PHONE_NUMBER);
                            currentContacts.setMatchKeywords(keyword);
                            currentContacts.setMatchStartIndex(currentContacts.getPhoneNumber().indexOf(keyword));
                            currentContacts.setMatchLength(keyword.length());
                            mSearchByPhoneNumberContacts.add(currentContacts);
                        }
                    }
                }
            }
        }

        if (mSearchByNameContacts.size() > 0) {
            Collections.sort(mSearchByNameContacts, MatchContacts.mSearchComparator);
        }
        if (mSearchByPhoneNumberContacts.size() > 0) {
            Collections.sort(mSearchByPhoneNumberContacts, MatchContacts.mSearchComparator);
        }

        mSearchContacts.clear();
        mSearchContacts.addAll(mSearchByNameContacts);
        mSearchContacts.addAll(mSearchByPhoneNumberContacts);

        if (mSearchContacts.size() <= 0) {
            if (mFirstNoSearchResultInput.length() <= 0) {
                mFirstNoSearchResultInput.append(keyword);
            }
        }
        return mSearchContacts;

    }

    /**
     * @param keyword
     * search base data according to string parameter
     */
    public List<MatchContacts> qwertySearch(String keyword,boolean mathName,boolean matchNumber) {
        if (null == keyword) {// add all base data to search
            mSearchContacts.clear();

            for (int i = 0; i < mBaseContacts.size(); i++) {
                MatchContacts currentContacts;
                for (currentContacts = mBaseContacts.get(i); null != currentContacts; currentContacts = currentContacts.getNextContacts()) {
                    currentContacts.setSearchByType(MatchContacts.SearchByType.SEARCH_BY_NULL);
                    currentContacts.clearMatchKeywords();
                    currentContacts.setMatchStartIndex(-1);
                    currentContacts.setMatchLength(0);
                    if (currentContacts.isFirstMultipleContacts()) {
                        mSearchContacts.add(currentContacts);
                    } else {
                        if (!currentContacts.isHideMultipleContacts()) {
                            mSearchContacts.add(currentContacts);
                        }
                    }
                }
            }

            //mSearchContacts.addAll(mBaseContacts);
            mFirstNoSearchResultInput.delete(0, mFirstNoSearchResultInput.length());
            Log.i(TAG, "null==search,mFirstNoSearchResultInput.length()=" + mFirstNoSearchResultInput.length());
            return mSearchContacts;
        }

        if (mFirstNoSearchResultInput.length() > 0) {
            if (keyword.contains(mFirstNoSearchResultInput.toString())) {

                return mSearchContacts;
            } else {

                mFirstNoSearchResultInput.delete(0,
                        mFirstNoSearchResultInput.length());
            }
        }

        mSearchContacts.clear();

        int contactsCount = mBaseContacts.size();


        for (int i = 0; i < contactsCount; i++) {
            PinyinSearchUnit namePinyinSearchUnit = mBaseContacts.get(i).getNamePinyinSearchUnit();

            if (mathName && mBaseContacts.get(i).getName().contains(keyword)){

                MatchContacts currentContacts = mBaseContacts.get(i);
                do {
                    currentContacts.setMatchKeywords(keyword);
                    currentContacts.setMatchStartIndex(currentContacts.getName().indexOf(keyword));
                    currentContacts.setMatchLength(keyword.length());
                    currentContacts = currentContacts.getNextContacts();
                }while (currentContacts != null);

            }

            if (QwertyUtil.match(namePinyinSearchUnit, keyword)) {// search by name;
                MatchContacts currentContacts ;
                MatchContacts firstContacts;
                for (currentContacts = mBaseContacts.get(i), firstContacts = currentContacts; null != currentContacts; currentContacts = currentContacts.getNextContacts()) {
                    currentContacts.setSearchByType(MatchContacts.SearchByType.SEARCH_BY_NAME);
                    currentContacts.setMatchKeywords(namePinyinSearchUnit.getMatchKeyword().toString());
                    currentContacts.setMatchStartIndex(firstContacts.getName().indexOf(firstContacts.getMatchKeywords().toString()));
                    currentContacts.setMatchLength(firstContacts.getMatchKeywords().length());
                    mSearchContacts.add(currentContacts);
                }
            } else {
                MatchContacts currentContacts;
                if (matchNumber){
                    for (currentContacts = mBaseContacts.get(i); null != currentContacts; currentContacts = currentContacts.getNextContacts()) {
                        if (currentContacts.getPhoneNumber().contains(keyword)) {// search by phone number
                            currentContacts.setSearchByType(MatchContacts.SearchByType.SEARCH_BY_PHONE_NUMBER);
                            currentContacts.setMatchKeywords(keyword);
                            currentContacts.setMatchStartIndex(currentContacts.getPhoneNumber().indexOf(keyword));
                            currentContacts.setMatchLength(keyword.length());
                            mSearchContacts.add(currentContacts);
                        }
                    }
                }

            }
        }

        if (mSearchContacts.size() <= 0) {
            if (mFirstNoSearchResultInput.length() <= 0) {
                mFirstNoSearchResultInput.append(keyword);

            }
        } else {
            Collections.sort(mSearchContacts, MatchContacts.mSearchComparator);
        }
        return mSearchContacts;
    }


    protected SpellingSearchHelper() {

        setContactsChanged(true);
        mBaseContacts.clear();
        mSearchContacts.clear();
        mFirstNoSearchResultInput.delete(0, mFirstNoSearchResultInput.length());

    }


    public void refreshContacts(List<MatchContacts> contacts) {
        if (null == contacts || contacts.size() < 1) {
            if (null != mOnContactsLoad) {
                mOnContactsLoad.onContactsLoadFailed();
            }
            return;
        }

        mBaseContacts.clear();
        for (MatchContacts contact : contacts) {
            if (!mBaseContacts.contains(contact)) {
                mBaseContacts.add(contact);
            }
        }

        if (null != mOnContactsLoad) {
            mOnContactsLoad.onContactsLoadSuccess();
        }

    }



    public static class Holder {
        private static final SpellingSearchHelper INSTANCE = new SpellingSearchHelper();
    }

    public static SpellingSearchHelper getInstance(){
        return Holder.INSTANCE;
    }

    public static String praseSortKey(String sortKey) {
        if (null == sortKey || sortKey.length() <= 0) {
            return null;
        }

        if ((sortKey.charAt(0) >= 'a' && sortKey.charAt(0) <= 'z')
                || (sortKey.charAt(0) >= 'A' && sortKey.charAt(0) <= 'Z')) {
            return sortKey;
        }

        return "#" + sortKey;
    }

    /**
     * key=id+phoneNumber
     */
    private String getSelectedContactsKey(MatchContacts contacts) {
        if (null == contacts) {
            return null;
        }

        return contacts.getId() + contacts.getPhoneNumber();
    }
}
