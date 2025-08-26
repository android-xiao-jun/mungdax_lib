package com.allo.search.util;


import com.allo.search.model.PinyinBaseUnit;
import com.allo.search.model.PinyinSearchUnit;
import com.allo.search.model.PinyinUnit;

import java.util.List;

public class QwertyUtil {
    public QwertyUtil() {
    }

    public static boolean match(PinyinSearchUnit pinyinSearchUnit, String keyword) {
        if (pinyinSearchUnit == null || keyword == null){
            return false;
        }
        if (pinyinSearchUnit.getBaseData() == null || pinyinSearchUnit.getMatchKeyword() == null){
            return false;
        }
        pinyinSearchUnit.getMatchKeyword().delete(0, pinyinSearchUnit.getMatchKeyword().length());
        String searchLowerCase = keyword.toLowerCase();
        int index = pinyinSearchUnit.getBaseData().toLowerCase().indexOf(searchLowerCase);
        if (index > -1) {
            pinyinSearchUnit.getMatchKeyword().append(pinyinSearchUnit.getBaseData().substring(index, index + searchLowerCase.length()));
            return true;
        } else {
            int pinyinUnitsLength = pinyinSearchUnit.getPinyinUnits().size();
            StringBuffer searchBuffer = new StringBuffer();

            for (int i = 0; i < pinyinUnitsLength; ++i) {
                int j = 0;
                pinyinSearchUnit.getMatchKeyword().delete(0, pinyinSearchUnit.getMatchKeyword().length());
                searchBuffer.delete(0, searchBuffer.length());
                searchBuffer.append(searchLowerCase);
                boolean found = findPinyinUnits(pinyinSearchUnit.getPinyinUnits(), i, j, pinyinSearchUnit.getBaseData(), searchBuffer, pinyinSearchUnit.getMatchKeyword());
                if (found) {
                    return true;
                }
            }

            return false;
        }

    }

    private static boolean findPinyinUnits(List<PinyinUnit> pinyinUnits, int pinyinUnitIndex, int qwertyPinyinUnitIndex, String baseData, StringBuffer searchBuffer, StringBuffer matchKeyword) {
        if (pinyinUnits == null || baseData == null || searchBuffer == null || matchKeyword == null){
            return false;
        }
        String search = searchBuffer.toString();
        if (search.length() <= 0) {
            return true;
        }
        if (pinyinUnitIndex >= pinyinUnits.size()) {
            return false;
        }

        PinyinUnit pyUnit =  pinyinUnits.get(pinyinUnitIndex);


        if (qwertyPinyinUnitIndex >= pyUnit.getPinyinBaseUnitIndex().size()) {
            return false;
        }
        PinyinBaseUnit pinyinBaseUnit = pyUnit.getPinyinBaseUnitIndex().get(qwertyPinyinUnitIndex);
        boolean found;
        if (pyUnit.isPinyin()) {
            if (search.startsWith(String.valueOf(pinyinBaseUnit.getPinyin().charAt(0)))) {
                searchBuffer.delete(0, 1);
                matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
                found = findPinyinUnits(pinyinUnits, pinyinUnitIndex + 1, 0, baseData, searchBuffer, matchKeyword);
                if (found) {
                    return true;
                }

                searchBuffer.insert(0, pinyinBaseUnit.getPinyin().charAt(0));
                matchKeyword.deleteCharAt(matchKeyword.length() - 1);
            }

            if (pinyinBaseUnit.getPinyin().startsWith(search)) {
                matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
                searchBuffer.delete(0, searchBuffer.length());
                return true;
            }

            if (search.startsWith(pinyinBaseUnit.getPinyin())) {
                searchBuffer.delete(0, pinyinBaseUnit.getPinyin().length());
                matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
                found = findPinyinUnits(pinyinUnits, pinyinUnitIndex + 1, 0, baseData, searchBuffer, matchKeyword);
                if (found) {
                    return true;
                }

                searchBuffer.insert(0, pinyinBaseUnit.getPinyin());
                matchKeyword.deleteCharAt(matchKeyword.length() - 1);
            } else {
                return findPinyinUnits(pinyinUnits, pinyinUnitIndex, qwertyPinyinUnitIndex + 1, baseData, searchBuffer, matchKeyword);
            }
        } else {
            byte startIndex;
            if (pinyinBaseUnit.getPinyin().startsWith(search)) {
                startIndex = 0;
                matchKeyword.append(baseData.substring(startIndex + pyUnit.getStartPosition(), startIndex + pyUnit.getStartPosition() + search.length()));
                searchBuffer.delete(0, searchBuffer.length());
                return true;
            }

            if (search.startsWith(pinyinBaseUnit.getPinyin())) {
                startIndex = 0;
                searchBuffer.delete(0, pinyinBaseUnit.getPinyin().length());
                matchKeyword.append(baseData.substring(startIndex + pyUnit.getStartPosition(), startIndex + pyUnit.getStartPosition() + pinyinBaseUnit.getPinyin().length()));
                if (findPinyinUnits(pinyinUnits, pinyinUnitIndex + 1, 0, baseData, searchBuffer, matchKeyword)) {
                    return true;
                }

                searchBuffer.insert(0, pinyinBaseUnit.getPinyin());
                matchKeyword.delete(matchKeyword.length() - pinyinBaseUnit.getPinyin().length(), matchKeyword.length());
            } else if (matchKeyword.length() <= 0) {
                int numLength;
                if (pinyinBaseUnit.getPinyin().contains(search)) {
                    numLength = pinyinBaseUnit.getPinyin().indexOf(search);
                    matchKeyword.append(baseData.substring(numLength + pyUnit.getStartPosition(), numLength + pyUnit.getStartPosition() + search.length()));
                    searchBuffer.delete(0, searchBuffer.length());
                    return true;
                }

                numLength = pinyinBaseUnit.getPinyin().length();

                for (int i = 0; i < numLength; ++i) {
                    String subStr = pinyinBaseUnit.getPinyin().substring(i);
                    if (search.startsWith(subStr)) {
                        searchBuffer.delete(0, subStr.length());
                        matchKeyword.append(baseData.substring(i + pyUnit.getStartPosition(), i + pyUnit.getStartPosition() + subStr.length()));
                        if (findPinyinUnits(pinyinUnits, pinyinUnitIndex + 1, 0, baseData, searchBuffer, matchKeyword)) {
                            return true;
                        }

                        searchBuffer.insert(0, pinyinBaseUnit.getPinyin().substring(i));
                        matchKeyword.delete(matchKeyword.length() - subStr.length(), matchKeyword.length());
                    }
                }

                return findPinyinUnits(pinyinUnits, pinyinUnitIndex, qwertyPinyinUnitIndex + 1, baseData, searchBuffer, matchKeyword);
            } else {
                return findPinyinUnits(pinyinUnits, pinyinUnitIndex, qwertyPinyinUnitIndex + 1, baseData, searchBuffer, matchKeyword);
            }
        }

        return false;


    }
}
