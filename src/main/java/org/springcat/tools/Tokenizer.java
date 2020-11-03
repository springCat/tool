package org.springcat.tools;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerUtil;
import cn.hutool.extra.tokenizer.Word;

import java.util.Iterator;

/**
 * @Description Tokenizer
 * @Author springCat
 * @Date 2020/10/27 9:48
 */
public class Tokenizer {

    //自动根据用户引入的分词库的jar来自动选择使用的引擎
    private final static TokenizerEngine engine = TokenizerUtil.createEngine();

    public static void main(String[] args) {
        //解析文本
        String text = ClipboardUtil.getStr();

        Result result = engine.parse(text);
        //输出：这 两个 方法 的 区别 在于 返回 值
        String resultStr = IterUtil.join((Iterator<Word>) result, " ");
        Console.log(resultStr);

        //ClipboardUtil.setStr(resultStr);
    }
}
