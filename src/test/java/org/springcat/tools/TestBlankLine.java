package org.springcat.tools;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.cli.*;

/**
 * @Description TestArgs
 * @Author springCat
 * @Date 2020/10/22 18:26
 */
public class TestBlankLine {
    public static void main(String[] args) throws ParseException {

        String str = ClipboardUtil.getStr();

        String[] split = StrUtil.split(str, "\n\n");

        for (String s : split) {
            System.out.println(s);
            System.out.println("---------------------------------");
        }
    }
}
