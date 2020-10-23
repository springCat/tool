package org.springcat.tools;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * @Description RepeatN
 * @Author springCat
 * @Date 2020/10/23 9:24
 */
public class RepeatN {

    public static void main(String[] args) {

        String str = ClipboardUtil.getStr();

        List<String> parts = StrUtil.splitTrim(str, "\n\n");

        String template = CollectionUtil.get(parts,0);
        int n = Convert.toInt(CollectionUtil.get(parts,1),3);

        boolean containsSeq = false;
        int len = 0;
        if(ReUtil.contains("(#\\{i,)(\\d)(\\})",template) || ReUtil.contains("(#\\{i,)(\\})",template)){
            containsSeq = true;
            len = Convert.toInt(ReUtil.extractMulti("(#\\{i,)(\\d)(\\})", template, "$2"),0);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {

            if(containsSeq){
                String seqFormat = String.valueOf(i+1);
                if(len > 0){
                    seqFormat = StrUtil.padPre(seqFormat,len,'0');
                }
                String temp = ReUtil.replaceAll(template,"(#\\{i,)(\\d)(\\})",seqFormat);
                stringBuilder.append(temp)
                        .append("\n")
                        .append("\n");
            }else {
                stringBuilder.append(template)
                        .append("\n")
                        .append("\n");
            }
        }

        System.out.println(stringBuilder.toString());
        ClipboardUtil.setStr(stringBuilder.toString());
    }

}
