package org.springcat.tools;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.List;

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
                int len = StrUtil.length(s);
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
                newRow.add(StrUtil.padAfter(s, columnMaxLen[j], ' '));
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

        List<String> rows = StrUtil.split(text, '\n');
        for (String row : rows) {
            if(StrUtil.isBlank(row)){
                continue;
            }
            row = row.replaceAll("\t"," ");
            table.add(StrUtil.splitTrim(row, split,limit));
        }
        return table;
    }

}
