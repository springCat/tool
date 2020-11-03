package org.springcat.tools;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Console;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerUtil;
import cn.hutool.extra.tokenizer.Word;


import java.util.Iterator;

/**
 * @Description TestArgs
 * @Author springCat
 * @Date 2020/10/22 18:26
 */
public class TestBlankLine {

    public static void main(String[] args) throws InterruptedException {


        StopWatch stopWatch = new StopWatch("任务名称");

        // 任务1
        stopWatch.start("任务一");
        Thread.sleep(1000);
        stopWatch.stop();

        // 任务2
        stopWatch.start("任务一");
        Thread.sleep(2000);
        stopWatch.stop();

        // 任务1
        stopWatch.start();
        Thread.sleep(1000);
        stopWatch.stop();

        // 打印出耗时
        Console.log(stopWatch.prettyPrint());

    }
}
