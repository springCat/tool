package org.springcat.tools;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.util.StrUtil.str;

/**
 * @Description TextAlign
 * @Author springCat
 * @Date 2020/10/22 15:49
 */
public class TextAlign {

    public static final String split = " ";

    public static void main(String[] args) {
        String text = ClipboardUtil.getStr();

        System.out.println("before:");
        System.out.println(text);

        String result = new TextAlign().formatAlign(text, 5);

        System.out.println("after:");
        System.out.println(result);

        ClipboardUtil.setStr(result);
    }

    public String formatAlign(String text,int limit){
        List<List<String>> table = parseText(text, limit);
        int size = table.size();

        int[] columnMaxLen = new int[limit];
        for (int j = 0; j < limit; j++) {
            for (int i = 0; i < size; i++) {
                List<String> row = CollectionUtil.get(table, i);
                String s = CollectionUtil.get(row, j);
                int len = length(s);
                if(columnMaxLen[j] < len){
                    columnMaxLen[j] = len;
                }
            }
        }

        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<String> newRow =  new ArrayList<>();
            for (int j = 0; j < limit; j++) {
                List<String> row = CollectionUtil.get(table, i);
                String s = StrUtil.nullToEmpty(CollectionUtil.get(row, j));
                String column = padAfter(s, columnMaxLen[j], ' ');
                newRow.add(column);
            }
            result.add(newRow);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (List<String> row : result) {
            stringBuilder.append(StrUtil.trim(StrUtil.join(split,row)));
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public List<List<String>> parseText(String text,int limit){
        List<List<String>> table = new ArrayList<>();

        List<String> rows = StrUtil.splitTrim(text, '\n');
        for (String row : rows) {
            if(StrUtil.isBlank(row)){
                continue;
            }
            row = row.replaceAll("\t"," ");
            table.add(StrUtil.splitTrim(row, split,limit));
        }
        return table;
    }


    public static String padAfter(CharSequence str, int minLength, char padChar) {
        if (null == str) {
            return null;
        }
        final int strLen = length(str);
        if (strLen == minLength) {
            return str.toString();
        } else if (strLen > minLength) {
            return StrUtil.sub(str, strLen - minLength, strLen);
        }

        return str.toString().concat(StrUtil.repeat(padChar, minLength - strLen));
    }
    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     */
    private static int length(CharSequence s) {
        if (s == null) {
            return 0;
        }

        int len = 0;
        for (int i = 0; i < s.length(); i++) {
            len++;
            if (!CharUtil.isAscii(s.charAt(i))){
                len++;
            }
        }
        return len;
    }


}
