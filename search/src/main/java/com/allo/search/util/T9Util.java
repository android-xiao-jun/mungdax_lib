package com.allo.search.util;

import com.allo.search.model.PinyinBaseUnit;
import com.allo.search.model.PinyinSearchUnit;
import com.allo.search.model.PinyinUnit;
import com.allo.utils.RegexUtils;
import com.allo.utils.StringUtils;

import java.util.List;

public class T9Util {

    public T9Util() { }

    public static char getT9Number(char alphabet) {
        char ch = alphabet;
        switch(alphabet) {
            case 'A':
            case 'B':
            case 'C':
            case 'a':
            case 'b':
            case 'c':

            case 'ا':
            case 'ە':
            case 'ب':
            case 'س':
                ch = '2';
                break;
            case 'D':
            case 'E':
            case 'F':
            case 'd':
            case 'e':
            case 'f':

            case 'د':
            case 'ې':
            case 'ف':
                ch = '3';
                break;
            case 'G':
            case 'H':
            case 'I':
            case 'g':
            case 'h':
            case 'i':

            case 'گ':
            case 'غ':
            case 'خ':
            case 'ھ':
            case 'ى':
                ch = '4';
                break;
            case 'J':
            case 'K':
            case 'L':
            case 'j':
            case 'k':
            case 'l':

            case 'ج':
            case 'ك':
            case 'ق':
            case 'ل':
                ch = '5';
                break;
            case 'M':
            case 'N':
            case 'O':
            case 'm':
            case 'n':
            case 'o':

            case 'م':
            case 'ن':
            case 'و':
            case 'ڭ':
                ch = '6';
                break;
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'p':
            case 'q':
            case 'r':
            case 's':

            case 'پ':
            case 'چ':
            case 'ر':
            case 'ژ':
                ch = '7';
                break;
            case 'T':
            case 'U':
            case 'V':
            case 't':
            case 'u':
            case 'v':

            case 'ت':
            case 'ۇ':
            case 'ۆ':
            case 'ۈ':
                ch = '8';
                break;

            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case 'w':
            case 'x':
            case 'y':
            case 'z':

            case 'ۋ':
            case 'ش':
            case 'ي':
            case 'ز':
                ch = '9';
                break;
            case 'ئ':
                ch = '0';
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
        }

        return ch;
    }

    public static boolean match(PinyinSearchUnit pinyinSearchUnit, String keyword) {
        String mKeyword = keyword;
        if (!RegexUtils.uyghurFirst(pinyinSearchUnit.getBaseData())){
            mKeyword = keyword.replaceAll("0", "");
        }
        if (mKeyword == null){
            return false;
        }
        if (pinyinSearchUnit.getBaseData() == null && pinyinSearchUnit.getMatchKeyword() == null){
            return false;
        }

        pinyinSearchUnit.getMatchKeyword().delete(0, pinyinSearchUnit.getMatchKeyword().length());
        int pinyinUnitsLength = pinyinSearchUnit.getPinyinUnits().size();
        StringBuffer searchBuffer = new StringBuffer();

        for (int i = 0; i < pinyinUnitsLength; ++i) {
            int j = 0;
            pinyinSearchUnit.getMatchKeyword().delete(0, pinyinSearchUnit.getMatchKeyword().length());
            searchBuffer.delete(0, searchBuffer.length());
            searchBuffer.append(mKeyword);
            if (findPinyinUnits(pinyinSearchUnit.getPinyinUnits(), i, j, pinyinSearchUnit.getBaseData(), searchBuffer, pinyinSearchUnit.getMatchKeyword())) {
                return true;
            }
        }
        return false;
    }

    private static boolean findPinyinUnits(List<PinyinUnit> pinyinUnits, int pinyinUnitIndex, int t9PinyinUnitIndex, String baseData, StringBuffer searchBuffer, StringBuffer matchKeyword) {
        if (pinyinUnits == null || baseData == null || searchBuffer == null || matchKeyword == null){
            return false;
        }
        String search = searchBuffer.toString();
        if (search.length() <= 0) {
            return true;
        } else if (pinyinUnitIndex >= pinyinUnits.size()) {
            return false;
        } else {
            PinyinUnit pyUnit = pinyinUnits.get(pinyinUnitIndex);
            if (t9PinyinUnitIndex >= pyUnit.getPinyinBaseUnitIndex().size()) {
                return false;
            } else {
                PinyinBaseUnit pinyinBaseUnit = pyUnit.getPinyinBaseUnitIndex().get(t9PinyinUnitIndex);
                boolean found;
                if (pyUnit.isPinyin()) {
                    if (search.startsWith(String.valueOf(pinyinBaseUnit.getNumber().charAt(0)))) {
                        searchBuffer.delete(0, 1);
                        matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
                        found = findPinyinUnits(pinyinUnits, pinyinUnitIndex + 1, 0, baseData, searchBuffer, matchKeyword);
                        if (found) {
                            return true;
                        }

                        searchBuffer.insert(0, pinyinBaseUnit.getNumber().charAt(0));
                        matchKeyword.deleteCharAt(matchKeyword.length() - 1);
                    }

                    if (pinyinBaseUnit.getNumber().startsWith(search)) {
                        matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
                        searchBuffer.delete(0, searchBuffer.length());
                        return true;
                    }

                    if (search.startsWith(pinyinBaseUnit.getNumber())) {
                        searchBuffer.delete(0, pinyinBaseUnit.getNumber().length());
                        matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
                        found = findPinyinUnits(pinyinUnits, pinyinUnitIndex + 1, 0, baseData, searchBuffer, matchKeyword);
                        if (found) {
                            return true;
                        }

                        searchBuffer.insert(0, pinyinBaseUnit.getNumber());
                        matchKeyword.deleteCharAt(matchKeyword.length() - 1);
                    } else {
                        return findPinyinUnits(pinyinUnits, pinyinUnitIndex, t9PinyinUnitIndex + 1, baseData, searchBuffer, matchKeyword);
                    }
                } else {
                    byte startIndex;
                    if (pinyinBaseUnit.getNumber().startsWith(search)) {
                        startIndex = 0;
                        matchKeyword.append(baseData.substring(startIndex + pyUnit.getStartPosition(), startIndex + pyUnit.getStartPosition() + search.length()));
                        searchBuffer.delete(0, searchBuffer.length());
                        return true;
                    }

                    if (search.startsWith(pinyinBaseUnit.getNumber())) {
                        startIndex = 0;
                        searchBuffer.delete(0, pinyinBaseUnit.getNumber().length());
                        matchKeyword.append(baseData.substring(startIndex + pyUnit.getStartPosition(), startIndex + pyUnit.getStartPosition() + pinyinBaseUnit.getNumber().length()));
                        if (findPinyinUnits(pinyinUnits, pinyinUnitIndex + 1, 0, baseData, searchBuffer, matchKeyword)) {
                            return true;
                        }

                        searchBuffer.insert(0, pinyinBaseUnit.getNumber());
                        matchKeyword.delete(matchKeyword.length() - pinyinBaseUnit.getNumber().length(), matchKeyword.length());
                    } else if (matchKeyword.length() <= 0) {
                        int numLength;
                        if (pinyinBaseUnit.getNumber().contains(search)) {
                            numLength = pinyinBaseUnit.getNumber().indexOf(search);
                            String substring = baseData.substring(numLength + pyUnit.getStartPosition(), numLength + pyUnit.getStartPosition() + search.length());
                            char c = substring.charAt(0);
                            if (numLength == 1 && pinyinBaseUnit.getNumber().startsWith("0") && StringUtils.INSTANCE.specialLetters().contains(c)){
                                matchKeyword.append(StringUtils.SPECIAL_FIRST_LETTER);
                            }
                            matchKeyword.append(substring);
                            searchBuffer.delete(0, searchBuffer.length());
                            return true;
                        }

                        numLength = pinyinBaseUnit.getNumber().length();

                        for(int i = 0; i < numLength; ++i) {
                            String subStr = pinyinBaseUnit.getNumber().substring(i);
                            if (search.startsWith(subStr)) {
                                searchBuffer.delete(0, subStr.length());
                                matchKeyword.append(baseData.substring(i + pyUnit.getStartPosition(), i + pyUnit.getStartPosition() + subStr.length()));
                                if (findPinyinUnits(pinyinUnits, pinyinUnitIndex + 1, 0, baseData, searchBuffer, matchKeyword)) {
                                    return true;
                                }

                                searchBuffer.insert(0, pinyinBaseUnit.getNumber().substring(i));
                                matchKeyword.delete(matchKeyword.length() - subStr.length(), matchKeyword.length());
                            }
                        }
                        return findPinyinUnits(pinyinUnits, pinyinUnitIndex, t9PinyinUnitIndex + 1, baseData, searchBuffer, matchKeyword);
                    } else {
                        return findPinyinUnits(pinyinUnits, pinyinUnitIndex, t9PinyinUnitIndex + 1, baseData, searchBuffer, matchKeyword);
                    }
                }
                return false;
            }
        }
    }
}
