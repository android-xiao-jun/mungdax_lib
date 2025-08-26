package com.allo.search.util;


import android.text.TextUtils;

import com.allo.search.model.PinyinBaseUnit;
import com.allo.search.model.PinyinSearchUnit;
import com.allo.search.model.PinyinUnit;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.List;

public class PinyinUtil {
    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    public PinyinUtil() {
    }

    public static void parse(PinyinSearchUnit pinyinSearchUnit) {
        if (null != pinyinSearchUnit && !TextUtils.isEmpty(pinyinSearchUnit.getBaseData()) && null != pinyinSearchUnit.getPinyinUnits()) {
            String chineseStr = pinyinSearchUnit.getBaseData().toLowerCase();
            if (null == format) {
                format = new HanyuPinyinOutputFormat();
            }

            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            int chineseStringLength = chineseStr.length();
            StringBuilder nonPinyinString = new StringBuilder();
            PinyinUnit pyUnit = null;
            String originalString ;
            String[] pinyinStr = null;
            boolean lastChineseCharacters = true;
            int startPosition = -1;

            for(int i = 0; i < chineseStringLength; ++i) {
                char ch = chineseStr.charAt(i);

                try {
                    pinyinStr = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                } catch (BadHanyuPinyinOutputFormatCombination var12) {
                    var12.printStackTrace();
                }

                if (null == pinyinStr) {
                    if (lastChineseCharacters) {
                        pyUnit = new PinyinUnit();
                        lastChineseCharacters = false;
                        startPosition = i;
                        nonPinyinString.delete(0, nonPinyinString.length());
                    }

                    nonPinyinString.append(ch);
                } else {
                    if (!lastChineseCharacters) {
                        originalString = nonPinyinString.toString();
                        String[] str = new String[]{nonPinyinString.toString()};
                        addPinyinUnit(pinyinSearchUnit.getPinyinUnits(), pyUnit, false, originalString, str, startPosition);
                        nonPinyinString.delete(0, nonPinyinString.length());
                        lastChineseCharacters = true;
                    }

                    pyUnit = new PinyinUnit();
                    startPosition = i;
                    originalString = String.valueOf(ch);
                    addPinyinUnit(pinyinSearchUnit.getPinyinUnits(), pyUnit, true, originalString, pinyinStr, i);
                }
            }

            if (!lastChineseCharacters) {
                originalString = nonPinyinString.toString();
                String[] str = new String[]{nonPinyinString.toString()};
                addPinyinUnit(pinyinSearchUnit.getPinyinUnits(), pyUnit, false, originalString, str, startPosition);
                nonPinyinString.delete(0, nonPinyinString.length());
                lastChineseCharacters = true;
            }

        }
    }

    public static String getFirstLetter(PinyinSearchUnit pinyinSearchUnit) {
        if (null != pinyinSearchUnit) {
            List<PinyinUnit> pinyinUnit = pinyinSearchUnit.getPinyinUnits();
            if (null != pinyinUnit && pinyinUnit.size() > 0) {
                List<PinyinBaseUnit> pinyinBaseUnit = (pinyinUnit.get(0)).getPinyinBaseUnitIndex();
                if (null != pinyinBaseUnit && pinyinBaseUnit.size() > 0) {
                    String pinyin = (pinyinBaseUnit.get(0)).getPinyin();
                    if (null != pinyin && pinyin.length() > 0) {
                        return String.valueOf(pinyin.charAt(0));
                    }
                }
            }
        }

        return null;
    }

    public static String getFirstCharacter(PinyinSearchUnit pinyinSearchUnit) {
        if (null != pinyinSearchUnit) {
            List<PinyinUnit> pinyinUnit = pinyinSearchUnit.getPinyinUnits();
            if (null != pinyinUnit && pinyinUnit.size() > 0) {
                List<PinyinBaseUnit> pinyinBaseUnit = pinyinUnit.get(0).getPinyinBaseUnitIndex();
                if (null != pinyinBaseUnit && pinyinBaseUnit.size() > 0) {
                    String originalString = pinyinBaseUnit.get(0).getOriginalString();
                    if (null != originalString && originalString.length() > 0) {
                        return String.valueOf(originalString.charAt(0));
                    }
                }
            }
        }

        return null;
    }

    public static String getSortKey(PinyinSearchUnit pinyinSearchUnit) {
        StringBuilder sortKeyBuffer = new StringBuilder();
        sortKeyBuffer.delete(0, sortKeyBuffer.length());
        String splitSymbol = " ";
        if (null != pinyinSearchUnit) {
            List<PinyinUnit> pinyinUnit = pinyinSearchUnit.getPinyinUnits();
            if (null != pinyinUnit && pinyinUnit.size() > 0) {
                for (PinyinUnit pu : pinyinUnit) {
                    if (pu.isPinyin()) {
                        sortKeyBuffer.append((pu.getPinyinBaseUnitIndex().get(0)).getPinyin()).append(splitSymbol);
                    }
                    sortKeyBuffer.append(( pu.getPinyinBaseUnitIndex().get(0)).getOriginalString()).append(splitSymbol);
                }

                return sortKeyBuffer.toString();
            }
        }

        return null;
    }

    public static boolean isKanji(char chr) {
        String[] pinyinStr = null;

        try {
            pinyinStr = PinyinHelper.toHanyuPinyinStringArray(chr, format);
        } catch (BadHanyuPinyinOutputFormatCombination var3) {
            var3.printStackTrace();
        }

        return null != pinyinStr;
    }

    private static void addPinyinUnit(List<PinyinUnit> pinyinUnit, PinyinUnit pyUnit, boolean pinyin, String originalString, String[] string, int startPosition) {
        if (null != pinyinUnit && null != pyUnit && null != originalString && null != string) {
            initPinyinUnit(pyUnit, pinyin, originalString, string, startPosition);
            pinyinUnit.add(pyUnit);
        }
    }

    private static void initPinyinUnit(PinyinUnit pinyinUnit, boolean pinyin, String originalString, String[] string, int startPosition) {
        if (null != pinyinUnit && null != originalString && null != string) {

            int strLength = string.length;
            pinyinUnit.setPinyin(pinyin);
            pinyinUnit.setStartPosition(startPosition);
            PinyinBaseUnit pinyinBaseUnit;
            if (pinyin && strLength > 1) {
                pinyinBaseUnit = new PinyinBaseUnit();
                initPinyinBaseUnit(pinyinBaseUnit, originalString, string[0]);
                pinyinUnit.getPinyinBaseUnitIndex().add(pinyinBaseUnit);

                for(int j = 1; j < strLength; ++j) {
                    int curStringIndexlength = pinyinUnit.getPinyinBaseUnitIndex().size();

                    int k = 0;

                    while (k < curStringIndexlength && !(pinyinUnit.getPinyinBaseUnitIndex().get(k)).getPinyin().equals(string[j])) {
                        ++k;
                    }

                    if (k == curStringIndexlength) {
                        pinyinBaseUnit = new PinyinBaseUnit();
                        initPinyinBaseUnit(pinyinBaseUnit, originalString, string[j]);
                        pinyinUnit.getPinyinBaseUnitIndex().add(pinyinBaseUnit);
                    }
                }
            } else {
                for (String s : string) {
                    pinyinBaseUnit = new PinyinBaseUnit();
                    initPinyinBaseUnit(pinyinBaseUnit, originalString, s);
                    pinyinUnit.getPinyinBaseUnitIndex().add(pinyinBaseUnit);
                }
            }

        }
    }

    private static void initPinyinBaseUnit(PinyinBaseUnit pinyinBaseUnit, String originalString, String pinyin) {
        if (null != pinyinBaseUnit && null != originalString && null != pinyin) {
            pinyinBaseUnit.setOriginalString(originalString);
            pinyinBaseUnit.setPinyin(pinyin);
            int pinyinLength = pinyin.length();
            StringBuilder numBuffer = new StringBuilder();
            numBuffer.delete(0, numBuffer.length());

            for(int i = 0; i < pinyinLength; ++i) {
                char ch = T9Util.getT9Number(pinyin.charAt(i));
                numBuffer.append(ch);
            }

            pinyinBaseUnit.setNumber(numBuffer.toString());
            numBuffer.delete(0, numBuffer.length());
        }
    }
}
